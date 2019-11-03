(ns typeit.transcript
  (:import [com.google.cloud.speech.v1p1beta1 RecognitionAudio RecognitionConfig RecognitionConfig$AudioEncoding SpeechClient])
  (:require [typeit.gcs :as gcs]))

(defn- configure
  "Returns parameters for transcription of `blob-id` in `bucket`."
  [bucket blob-id]
  [(.. (RecognitionConfig/newBuilder)
       (setEncoding RecognitionConfig$AudioEncoding/FLAC)
       (setLanguageCode "ja-JP")
       ;; (setSampleRateHertz 16000)
       build)
   (.. (RecognitionAudio/newBuilder)
       (setUri (gcs/uri bucket blob-id))
       build)])

(defn transcribe-async
  "Returns future object for transcription of `blob-id` in `bucket`."
  [bucket blob-id]
  (let [[config audio] (configure bucket blob-id)]
    (.. (SpeechClient/create)
        (longRunningRecognizeAsync config audio))))

(defn results->seq
  "Returns a vector of results of Speech-to-Text response."
  [response]
  (->> (range 0 (.. response getResultsCount))
       (map #(.getResults response %))))

(defn alternative
  "Returns n (defaults to 0) th alternative of given result."
  ([result]
   (alternative result 0))

  ([result n]
   (.getAlternatives result n)))

(defn transcript
  "Returns a transcript of a given alternative."
  [alternative]
  (.getTranscript alternative))
