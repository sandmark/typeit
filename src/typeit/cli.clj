(ns typeit.cli
  (:require [clojure.java.io :as io]
            [typeit.gcs :as gcs]
            [typeit.transcript :as transcript]))

(defn- filename [path]
  (-> path io/file .getName))

(defn- wait-for-future [future]
  (while (not (future-done? future))
    (print ".")
    (Thread/sleep 1000))
  (println))

(defn upload-and-transcribe [file]
  (let [filename (filename file)
        bucket   "typeit-clj"
        blob     (str "cli/" filename)]
    (wait-for-future (future
                       (print (str "Uploading " file " as " (gcs/uri bucket blob)))
                       (gcs/upload-flac bucket blob file)))
    (print (str "Transcribing " (gcs/uri bucket blob)))
    (let [response (transcript/transcribe-async bucket blob)]
      (wait-for-future response)
      (doseq [transcript (->> @response
                              transcript/results->seq
                              (map transcript/alternative)
                              (map transcript/transcript))]
        (println transcript)))))


(comment
  (gcs/connect!)
  (upload-and-transcribe "filename.flac"))
