(ns typeit.credential
  (:require [clojure.java.io :as io])
  (:import com.google.auth.oauth2.ServiceAccountCredentials))

(defn build-from-file
  "Returns ServiceAccountCredentials information generated with given argument, a JSON file."
  [path]
  (-> path io/input-stream ServiceAccountCredentials/fromStream))
