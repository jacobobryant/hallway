(ns findka.hallway.client.app
  (:require [biff.client :as bc]
            [goog.net.Cookies]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [rum.core :as rum]
            [findka.hallway.client.app.components :as c]
            [findka.hallway.client.app.db :as db]
            [findka.hallway.client.app.handlers :as h]
            [findka.hallway.client.app.mutations :as m]
            [findka.hallway.client.app.routes :as r]
            [findka.hallway.client.app.system :as s]))

(defn ^:export mount []
  (rum/mount (c/main) (js/document.querySelector "#app")))

(defn ^:export init []
  (reset! s/system
    (bc/init-sub {:url "/api/chsk"
                  :handler #(h/api % (second (:?data %)))
                  :sub-results db/sub-results
                  :subscriptions db/subscriptions
                  :csrf-token (js/decodeURIComponent
                                (.get (new goog.net.Cookies js/document) "csrf"))}))
  (rfe/start!
    (rf/router r/client-routes)
    #(reset! db/route %)
    {:use-fragment false})
  (mount))
