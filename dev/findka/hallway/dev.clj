(ns findka.hallway.dev
  (:require [biff.dev :as dev]
            [biff.rum :as br]
            [biff.util :as bu]
            [clojure.stacktrace :as st]
            [clojure.string :as str]
            [clojure.test :as t]
            [hf.depstar.uberjar :as uber]
            [nrepl.cmdline :as nrepl-cmd]
            [findka.hallway :as core]
            [findka.hallway-test]
            [findka.hallway.dev.css :as css]
            [findka.hallway.views :refer [static-pages]]))

(defn tests []
  (t/run-all-tests #"findka.hallway.*test"))

(defn html []
  (br/export-rum static-pages "target/resources/public"))

(defn css []
  (css/write-css {:output-file "target/resources/public/css/main.css"
                  :paths ["src" "dev"]}))

(defn on-file-change []
  (println "=== tests ===")
  (time (tests))
  (println "\n=== html ===")
  (time (html))
  (println "\n=== css ===")
  (time (css)))

(defn build [_]
  (let [{:keys [fail error]} (tests)]
    (if (< 0 (+ fail error))
      (System/exit 1)
      (do
        (html)
        (css)
        (if (:success (uber/build-jar
                        {:aot true
                         :main-class 'findka.hallway
                         :jar "target/app.jar"}))
          (System/exit 0)
          (System/exit 2))))))

(defn start []
  (bu/start-system
    (assoc core/config
           :biff/after-refresh `start
           :biff.hawk/callback `on-file-change
           :biff.hawk/paths ["src" "dev"])
    (into [dev/use-hawk] core/components)))

(defn -main [& args]
  (on-file-change)
  (start)
  (apply nrepl-cmd/-main args))
