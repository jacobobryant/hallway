(ns hallway.jobs)

(defn some-job [sys]
  (println "Hello from hallway.jobs!")
  (println "This function will run every 2 minutes, beginning 1 minute after your app starts."))

(def jobs
  [{:offset-minutes 1
    :period-minutes 2
    :job-fn #'some-job}])
