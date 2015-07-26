(ns edn-keeper.core
  (:require [durable-queue :as q]
            [edn-keeper.s3 :as s3]))

(defprotocol IKeeper
  (save! [_ part data])
  (load! [_ part]))

(defn- start-uploader [queue upload]
  (future
    (loop []
      (let [task (q/take! queue ::save)
            [key data] (deref task)]
        (upload key data)
        (q/complete! task))
      (recur))))

(defn keeper [config]
  (let [queue (q/queues (:queue-dir config "/tmp") {})]
    (start-uploader queue (partial s3/upload-edn (:s3-bucket config)))
    (reify IKeeper
      (save! [_ part data]
        (s3/upload-edn (:s3-bucket config) part data))
      (load! [_ part]
        (s3/download-edn (:s3-bucket config) part)))))
