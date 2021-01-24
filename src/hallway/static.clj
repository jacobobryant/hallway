(ns hallway.static)

(def simple-analytics
  (list
    [:script
     {:src "https://sa.findka.com/latest.js",
      :defer "defer",
      :async "async"}]
    [:noscript [:img {:alt "", :src "https://sa.findka.com/noscript.gif"}]]))

(defn base-page [& contents]
  [:html
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:title "Hallway"]
    [:link {:rel "stylesheet" :href "/css/main.css"}]]
   [:body contents simple-analytics]])

(def footer
  [:div
   [:a.link {:href "https://essays.findka.com" :target "_blank"}
    "Findka"]
   " | "
   [:a.link {:href "https://github.com/jacobobryant/hallway"
             :target "_blank"}
    "Github"]])

(def home
  (base-page
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
  (base-page {}
    [:p "Not found."]))

(def pages
  {"/" home
   "/404.html" not-found})

#_(do
    (biff.components/write-static-resources
      (assoc @biff.core/system :biff/static-pages pages))
    nil)
