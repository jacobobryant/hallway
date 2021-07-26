(ns findka.hallway.env
  (:require [biff.util :as bu]))

; See https://biff.findka.com/codox/biff.util.html#var-read-env

(def env-keys
  [["HOST"               :biff/host]
   ["PORT"               :biff/port #(Long/parseLong %)]
   ["BASE_URL"           :biff/base-url]
   ["NREPL_PORT"         :biff.nrepl/port #(Long/parseLong %)]
   ["SECURE_COOKIES"     :biff.middleware/secure #(= "true" %)]
   ["REITIT_MODE"        :biff.reitit/mode keyword]])

(defn use-env [sys]
  (merge sys (bu/read-env env-keys)))
