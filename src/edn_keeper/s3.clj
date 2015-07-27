(ns edn-keeper.s3
  (:require [clojure.edn :as edn]
            [amazonica.aws.s3 :as s3])
  (:import [java.io ByteArrayInputStream]))

(defn upload-edn [bucket key data]
  (let [bytes (.getBytes (pr-str data) "UTF-8")]
    (s3/put-object :bucket-name bucket
                   :key key
                   :input-stream (ByteArrayInputStream. bytes)
                   :metadata {:content-length (count bytes)
                              :content-type "application/edn"})))

(defn download-edn [bucket key]
  (let [object (s3/get-object bucket key)]
    (edn/read-string (slurp (:input-stream object)))))

(defn list-keys [bucket]
  (map :key (:object-summaries (s3/list-objects bucket))))
