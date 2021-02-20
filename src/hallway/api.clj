(ns hallway.api
  (:require
    [biff.util :as bu]
    [clj-http.client :as http]
    [clojure.java.shell :as sh]
    [clojure.string :as str]
    [hallway.util :as u]))

; todo rate limit
; todo cache results in crux

(defmacro catchall-verbose [& forms]
  `(try ~@forms (catch Exception e# (.printStackTrace e#))))

(defn search-hn [subject-url]
  (->> (http/get "http://hn.algolia.com/api/v1/search"
         {:query-params {:query (str "\"" subject-url "\"")
                         :restrictSearchableAttributes "url"
                         :attributesToRetrieve "created_at,num_comments,points,title,author"
                         :attributesToHighlight ""
                         :queryType "prefixNone"}
          :as :json})
    catchall-verbose
    :body
    :hits
    (keep (fn [{:keys [objectID
                      title
                      num_comments
                      created_at
                      points
                      author]}]
           (catchall-verbose
             {:source :hn
              :url (str "https://news.ycombinator.com/item?id=" objectID)
              :title title
              :n-comments num_comments
              :created (u/parse-date created_at)
              :points points
              :author author})))))

(defn search-reddit [subject-url]
  (->> (http/get "https://www.reddit.com/api/info.json"
         {:query-params {:url subject-url}
          :headers {"User-Agent" "https://github.com/jacobobryant/hallway"}
          :as :json})
    catchall-verbose
    :body
    :data
    :children
    (keep (fn [{{:keys [name
                       permalink
                       num_comments
                       created_utc
                       title
                       author
                       score
                       subreddit_name_prefixed]} :data}]
            (catchall-verbose
              {:source :reddit
               :url (str "https://www.reddit.com" permalink)
               :title (str subreddit_name_prefixed ": " title)
               :n-comments num_comments
               :created (java.util.Date. (* (long created_utc) 1000))
               :points score
               :author author})))))

(defn search-twitter [subject-url]
  (->> (sh/sh ".local/bin/twint" "-s" subject-url "--format"
         ">>> {username} {id} {date}T{time} {replies} {retweets} {likes} {tweet}"
         "-pt" "--limit" "20" :dir (System/getProperty "user.home"))
    :out
    str/split-lines
    (keep (fn [line]
            (catchall-verbose
              (when (str/starts-with? line ">>> ")
                (let [[_ username id datetime replies retweets likes text]
                      (str/split line #" " 8)

                      datetime (u/parse-date datetime "yyyy-MM-dd'T'HH:mm:ss")
                      replies (Long/parseLong replies)
                      retweets (Long/parseLong retweets)
                      likes (Long/parseLong likes)]
                  {:source :twitter
                   :url (str "https://twitter.com/" username "/status/" id)
                   :title (str "@" username ": " text)
                   :author username
                   :n-comments replies
                   :created datetime
                   :points (+ replies retweets likes)})))))))

(defn search-all [subject-url]
  (apply concat
    (pmap #(%1 subject-url)
      [search-hn search-reddit search-twitter])))
