(ns hallway.routes
  (:require
    [biff.util :as bu]
    [hallway.api :as api]
    [hallway.static :as static]
    [hallway.util :as u]
    [lambdaisland.uri :as uri]
    [rum.core :as rum]))

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
                       (uri/query-encode (str url " @FindkaEssays")))}
      "Twitter"]]))

(defn discussion
  [{:keys [url title n-comments created points author]}]
  [:.my-3
   [:.line-clamp-1
    [:a.link.break-all {:href url :target "_blank"} title]]
   [:.text-sm points " points | "
    n-comments " comments | "
    (u/format-date created "yyyy-MM-dd H:mm")
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
   [:a.link {:href "https://essays.findka.com" :target "_blank"}
    "Findka"]
   " | "
   [:a.link {:href "https://github.com/jacobobryant/hallway"
             :target "_blank"}
    "Github"]])

(defn discussions-body [url docs]
  (rum/render-static-markup
    (static/base-page
      [:.p-3.mx-auto.max-w-screen-md
       (header url)
       [:.h-6]
       (sections docs)
       [:hr.mt-8.mb-2.border-gray-400]
       footer])))

(defn view-discussions [{:keys [params/url]}]
  {:status 200
   :headers/Content-Type "text/html"
   :body (discussions-body url (api/search-all url))})

(def routes
  [["/view" {:get #(view-discussions %)}]])
