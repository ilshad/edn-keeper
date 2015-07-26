(ns edn-keeper.core
  (:require [durable-queue :as q]
            [edn-keeper.s3 :as s3]))

(defprotocol IKeeper
  (start! [_])
  (save!  [_ part data])
  (load!  [_ part]))

(defn start-uploader [queue upload]
  (future
    (loop []
      (let [[key data] (deref (q/take! queue ::save))]
        (upload key data))
      (recur))))

(defn keeper [config]
  (let [queue (q/queues (:queue-dir config "/tmp") {})]
    (reify IKeeper
      
      (start! [_]
        (start-uploader queue (partial s3/upload-edn (:s3-bucket config))))

      (save! [_ part data]
        (s3/upload-edn (:s3-bucket config) part data))

      (load! [_ part]
        (s3/download-edn (:s3-bucket config) part)))))
