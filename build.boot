(def +version+ "0.1.0")

(task-options!
  pom {:project 'ilshad/edn-keeper
       :version +version+
       :description "Keeping EDN in Amazon S3 or local disk"
       :url "https://github.com/ilshad/edn-keeper"
       :scm {:url "https://github.com/ilshad/edn-keeper"}
       :license {"Eclipse Public License"
                 "http://www.eclipse.org/legal/epl-v10.html"}})

(set-env!
  :source-paths #{"src"}
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.7.0" :scope "provided"]
                  [org.clojure/tools.logging "0.3.1" :scope "provided"]
                  [adzerk/bootlaces "0.1.11" :scope "test"]
                  [adzerk/boot-test "1.0.4" :scope "test"]
                  [factual/durable-queue "0.1.5"]
                  [byte-streams "0.2.0"]
                  [amazonica "0.3.28"]])

(require '[adzerk.bootlaces :refer :all])
(require '[adzerk.boot-test :refer :all])

(bootlaces! +version+)

(defn test-env! []
  (merge-env! :source-paths #{"test"}))

(deftask clojars-snapshot []
  (comp (build-jar) (push-snapshot)))

(deftask clojars-release []
  (comp (build-jar) (push-release)))
