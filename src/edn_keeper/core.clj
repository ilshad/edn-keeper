(ns edn-keeper.core
  (:refer-clojure :exclude [get keys])
  (:require [durable-queue :as q]
            [edn-keeper.fs :as fs]
            [edn-keeper.s3 :as s3]))

(defprotocol IKeeper
  (put  [_ key data])
  (get  [_ key])
  (keys [_]))

(defn- start-uploader [queue upload]
  (future
    (loop []
      (let [task (q/take! queue ::save)
            [key data] (deref task)]
        (upload key data)
        (q/complete! task))
      (recur))))

(defn- start-queue [config uploader]
  (let [queue (q/queues (:queue-dir config "/tmp") {})]
    (start-uploader queue uploader)
    queue))

(defmulti keeper :storage)

(defmethod keeper :fs [config]
  (let [queue (start-queue config (partial fs/save-edn (:path config)))]
    (reify IKeeper
      (put  [_ key data] (q/put! queue ::save [key data]))
      (get  [_ key]      (fs/load-edn (:path config) key))
      (keys [_]          (fs/list-keys (:path config))))))

(defmethod keeper :s3 [config]
  (let [queue (start-queue config (partial s3/upload-edn (:bucket config)))]
    (reify IKeeper
      (put  [_ key data] (q/put! queue ::save [key data]))
      (get  [_ key]      (s3/download-edn (:bucket config) key))
      (keys [_]          (s3/list-keys (:bucket config))))))

