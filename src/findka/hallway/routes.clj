(ns findka.hallway.routes
  (:require [biff.util :as bu]
            [findka.hallway.api :as api]
            [findka.hallway.feed :as feed]
            [findka.hallway.views :as views]
            [lambdaisland.uri :as uri]
            [rum.core :as rum]))

(defn on-error [{:keys [status]}]
  {:status status
   :headers/Content-Type "text/html"
   :body (str "<h1>" (get bu/http-status->msg status "There was an error.") "</h1>")})

(def source-opts
  {:hn {:title "Hacker News"
        :bg-color "#ff6600"}
   :reddit {:title "Reddit"
            :bg-color "#ff4500"
            :text-color "white"}
   :twitter {:title "Twitter"
             :bg-color "#1da1f2"
             :text-color "white"}})

(defn header [url]
  (list
    [:div "Discussions for "
     [:a.link {:href url :target "_blank"} url]]
    [:div
     "Start a new discussion: "
     [:a.link {:href (str "https://news.ycombinator.com/submitlink?u="
                          (uri/query-encode url))}
      "Hacker News"]
     " | "
     [:a.link {:href (str "https://wwww.reddit.com/submit?url="
                          (uri/query-encode url))}
      "Reddit"]
     " | "
     [:a.link {:href (str "https://twitter.com/intent/tweet?text="
                          (uri/query-encode url))}
      "Twitter"]]
    [:div
     [:a.link {:href (str "/feed?url=" (uri/query-encode url))}
      "Atom feed"]]))

(defn discussion
  [{:keys [url title n-comments created points author]}]
  [:.my-3
   [:.line-clamp-1
    [:a.link.break-all {:href url :target "_blank"} title]]
   [:.text-sm points " points | "
    n-comments " comments | "
    (bu/format-date created "yyyy-MM-dd H:mm")
    (when author
      (str " | by " author))]])

(defn sections [docs]
  (for [source [:hn :reddit :twitter]
        :let [{:keys [title
                      bg-color
                      text-color]} (get source-opts source)
              docs (->> docs
                        (filter #(= source (:source %)))
                        (sort-by :points #(compare %2 %1))
                        (take 10))]]
    (list
      [:.px-3.py-1.font-bold
       {:style {:background-color bg-color
                :color text-color}}
       title]
      (when (empty? docs)
        [:.my-3 "No discussions found."])
      (map discussion docs)
      [:.h-4])))

(def footer
  [:div
   [:a.link {:href "/"} "Home"]
   " | "
   [:a.link {:href "https://findka.com" :target "_blank"}
    "Findka"]
   " | "
   [:a.link {:href "https://github.com/jacobobryant/hallway"
             :target "_blank"}
    "Github"]])

(defn view-body [url docs]
  (rum/render-static-markup
    (views/base
      {}
      [:.p-3.mx-auto.max-w-screen-md
       (header url)
       [:.h-6]
       (sections docs)
       [:hr.mt-8.mb-2.border-gray-400]
       footer])))

(defn feed-body [sys docs]
  (str feed/xml-doc
       (rum/render-static-markup
         (feed/feed sys docs))))

(defn view [{:keys [params/url]}]
  {:status 200
   :headers/Content-Type "text/html"
   :body (view-body url (api/search-all url))})

(defn feed [{:keys [params/url] :as sys}]
  {:status 200
   :headers/Content-Type "application/atom+xml"
   :body (feed-body sys (api/search-all url))})

(defn routes []
  [["/view" {:get #(view %)}]
   ["/feed" {:get #(feed %)}]])
