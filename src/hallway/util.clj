(ns hallway.util)

(def rfc3339 "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

(defn parse-format-date [date in-format out-format]
  (cond->> date
    in-format (.parse (new java.text.SimpleDateFormat in-format))
    out-format (.format (new java.text.SimpleDateFormat out-format))))

(defn parse-date
  ([date]
   (parse-date date rfc3339))
  ([date in-format]
   (parse-format-date date in-format nil)))

(defn format-date
  ([date]
   (format-date date rfc3339))
  ([date out-format]
   (parse-format-date date nil out-format)))
