(ns hallway.rss
  (:require
    [biff.util :as bu]
    [hallway.util :as u]))

(def xml-doc
  "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")

(defn entry [doc]
  [:entry
   [:title (:title doc)]
   [:link {:href (:url doc)}]
   [:id (:url doc)]
   [:updated (u/format-date (:created doc))]
   [:category 
    {:term (:source doc)}]
   [:summary (:title doc)]])

(defn feed [url docs]
  (let [sorted-docs (sort-by :created docs)
        self (str "https://discuss.findka.com/rss?url=" url)] 
    [:feed {:xmlns "http://www.w3.org/2005/Atom"}
     [:title (str "Discussions for " url)]
     [:link {:href self :rel "self"}]
     [:id self]
     [:author [:name "Findka"]]
     (when (not (empty? docs)) [:updated (u/format-date (:created (last sorted-docs)))])

     (map entry sorted-docs)
     ]))
