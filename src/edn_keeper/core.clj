(ns edn-keeper.core
  (:refer-clojure :exclude [get keys])
  (:require [durable-queue :as q]
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

(defn s3-keeper [config]
  (let [queue (q/queues (:queue-dir config "/tmp") {})]
    (start-uploader queue (partial s3/upload-edn (:s3-bucket config)))
    (reify IKeeper
      (put  [_ key data] (q/put! queue ::save [key data]))
      (get  [_ key]      (s3/download-edn (:s3-bucket config) key))
      (keys [_]          (s3/list-keys (:s3-bucket config))))))
