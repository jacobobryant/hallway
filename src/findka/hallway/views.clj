(ns findka.hallway.views
  (:require [biff.rum :as br]))

(def simple-analytics
  (list
    [:script
     {:src "https://sa.findka.com/latest.js",
      :defer "defer",
      :async "async"}]
    [:noscript [:img {:alt "", :src "https://sa.findka.com/noscript.gif"}]]))

(def default-opts
  #:base{:title "Hallway"
         :lang "en-US"
         :description "View discussions for a given URL on Hacker News, Reddit and Twitter."})

(def head*
  [[:link {:rel "stylesheet" :href "/css/main.css"}]
   [:link {:rel "stylesheet" :href "/css/custom.css"}]])

(defn base [{:keys [base/head] :as opts} & contents]
  (br/base
    (merge
      default-opts
      opts
      {:base/head (concat head* head)})
    [:.p-3.mx-auto.max-w-200.w-full contents]
    simple-analytics))

(def footer
  [:div
   [:a.link {:href "https://findka.com" :target "_blank"} "Findka"]
   " | "
   [:a.link {:href "https://github.com/jacobobryant/hallway"
             :target "_blank"}
    "Github"]])

(def home
  (base
    {}
    [:.p-3.mx-auto.max-w-screen-md
     [:.text-lg "Enter a URL:"]
     [:.h-3]
     [:form {:method "GET"
             :action "/view"}
      [:input#url.border.border-gray-500.rounded.p-2.w-full
       {:name "url" :type "url" :placeholder "https://example.com"}]
      [:.h-3]
      [:button.btn {:type "submit"} "View discussions"]]
     [:.h-8]
     [:div "Bookmarklet: "
      [:a.link
       {:href "javascript:window.location=%22https://discuss.findka.com/view?url=%22+encodeURIComponent(document.location)"}
       "view discussions"]]
     [:.text-sm "Drag this link to your bookmarks menu. Use it to view discussions for the current page."]
     [:.h-11]
     [:hr.mb-2.border-gray-400]
     footer]))

(def not-found
  (base
    {}
    [:p "Not found."]))

(def static-pages
  {"/" home
   "/404.html" not-found})
