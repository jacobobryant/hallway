(ns findka.hallway
  (:require [biff.middleware :as mid]
            [biff.misc :as misc]
            [biff.util :as bu]
            [findka.hallway.env :refer [use-env]]
            [findka.hallway.routes :refer [routes on-error]]
            [muuntaja.middleware :as muuntaja]
            [ring.middleware.defaults :as rd]
            [ring.middleware.session.memory :as mem])
  (:gen-class))

;; Modified from biff.middleware/wrap-defaults since we don't need sessions.
(defn wrap-defaults [handler {:keys [secure on-error env]
                              :or {secure true}
                              :as opts}]
  (let [changes {[:session]                false
                 [:security :anti-forgery] false
                 [:security :ssl-redirect] false
                 [:static]                 false}
        ring-defaults (reduce (fn [m [path value]]
                                (assoc-in m path value))
                              (if secure
                                rd/secure-site-defaults
                                rd/site-defaults)
                              changes)]
    (-> handler
        (mid/wrap-env env)
        mid/wrap-flat-keys
        muuntaja/wrap-params
        muuntaja/wrap-format
        (mid/wrap-resource {})
        (rd/wrap-defaults ring-defaults)
        (mid/wrap-internal-error {:on-error on-error})
        mid/wrap-log-requests)))

(defn use-default-middleware [{:keys [biff.middleware/secure biff/on-error]
                               :or {secure true}
                               :as sys}]
  (update sys :biff/handler wrap-defaults
          {:secure secure
           :env sys
           :on-error on-error}))

(def components
  [use-env
   misc/use-nrepl
   misc/use-reitit
   use-default-middleware
   misc/use-jetty
   (fn [{:keys [biff/base-url] :as sys}]
     (println "Go to" base-url)
     sys)])

(def config {:biff.reitit/routes (fn [] (routes))
             :biff/after-refresh `-main
             :biff/on-error      (fn [req] (on-error req))})

(defn -main []
  (bu/start-system config components))
