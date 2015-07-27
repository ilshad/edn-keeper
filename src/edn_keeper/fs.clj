(ns edn-keeper.fs
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn save-edn [path key data]
  (spit (str path "/" key) (pr-str data)))

(defn load-edn [path key]
  (edn/read-string (slurp (str path "/" key) :encoding "UTF-8")))

(defn list-keys [path]
  (map #(.getName %) (.listFiles (io/file path))))
