(ns findka.hallway.routes
  (:require [biff.util :as bu]
            [findka.hallway.views :as v]))

(defn on-error [{:keys [status]}]
  {:status status
   :headers/Content-Type "text/html"
   :body (str "<h1>" (get bu/http-status->msg status "There was an error.") "</h1>")})

(defn routes []
  [
   
   ])
