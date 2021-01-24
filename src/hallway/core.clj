(ns hallway.core
  (:require
    [biff.components :as c]
    [biff.core :as biff]
    [biff.project :as project]
    [hallway.routes :refer [routes]]
    [hallway.static :refer [pages]]))

(defn start [first-start]
  (let [sys (biff/start-system
              #:biff{:first-start first-start
                     :routes routes
                     :static-pages pages
                     :after-refresh `after-refresh}
              [#(merge {:biff.init/start-nrepl true
                        :biff.init/start-shadow false} %)
               c/init
               c/set-defaults
               #(dissoc % :biff.http/spa-path)
               c/start-crux
               c/set-http-handler
               c/start-web-server
               c/write-static-resources
               c/print-mpa-help])]
    (when (:biff/dev sys)
      (project/update-mpa-files sys))
    (println "System started.")))

(defn -main []
  (start true))

(defn after-refresh []
  (start false))

(comment
  (biff.core/refresh)
  (->> @biff.core/system keys sort (run! prn))
  )
