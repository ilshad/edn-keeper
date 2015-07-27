(ns edn-keeper.util
  (:require [clojure.edn :as edn])
  (:import [java.io ByteArrayInputStream]))

(defn edn->stream [data]
  (let [bytes (.getBytes (pr-str data) "UTF-8")]
    [(ByteArrayInputStream. bytes) (count bytes)]))

(defn stream->edn [input-stream]
  (edn/read-string (slurp input-stream)))
