(ns findka.hallway.views
  (:require [biff.rum :as br]))

(def default-opts
  #:base{:title "Example app"
         :lang "en-US"
         :description "Here is my example app."})

(def head*
  [[:link {:rel "stylesheet" :href "/css/main.css"}]
   [:link {:rel "stylesheet" :href "/css/custom.css"}]])

(defn base [{:keys [base/head] :as opts} & contents]
  (br/base
    (merge
      default-opts
      opts
      {:base/head (concat head* head)})
    [:.p-3.mx-auto.max-w-prose.w-full contents]))

(def home
  (base
    {}
    [:p "Welcome."]))

(def not-found
  (base
    {}
    [:p "Not found."]))

(def static-pages
  {"/" home
   "/404.html" not-found})
