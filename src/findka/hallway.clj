(ns findka.hallway
  (:require [biff.middleware :as mid]
            [biff.misc :as misc]
            [biff.util :as bu]
            [findka.hallway.env :refer [use-env]]
            [findka.hallway.routes :refer [routes on-error]])
  (:gen-class))

(def components
  [use-env
   misc/use-nrepl
   misc/use-reitit
   mid/use-default-middleware
   misc/use-jetty
   (fn [{:keys [biff/base-url] :as sys}]
     (println "Go to" base-url)
     sys)])

(def config {:biff.reitit/routes       (fn [] (routes))
             :biff/after-refresh       `-main
             :biff/on-error            (fn [req] (on-error req))})

(defn -main []
  (bu/start-system config components))
