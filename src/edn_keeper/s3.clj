(ns edn-keeper.s3
  (:require [amazonica.aws.s3 :as s3]
            [edn-keeper.util :as util]))

(defn upload-edn [bucket key data]
  (let [[stream length] (util/edn->stream data)]
    (s3/put-object :bucket-name bucket
                   :key key
                   :input-stream stream
                   :metadata {:content-length length
                              :content-type "application/edn"})))

(defn download-edn [bucket key]
  (let [object (s3/get-object bucket key)]
    (util/stream->edn (:input-stream object))))

(defn list-keys [bucket]
  (map :key (:object-summaries (s3/list-objects bucket))))
