(ns typeit.gcs
  (:require [clojure.java.io :as io]
            [typeit.credential :as credential])
  (:import [com.google.cloud.storage BlobId BlobInfo Storage$BlobTargetOption StorageOptions]
           java.nio.file.Files))

(def storage (atom nil))

(defn uri [bucket blob-id]
  (str "gs://" bucket "/" blob-id))

(defn connect!
  "Establish a permanent connection `storage` to Google Cloud Storage,
   which is authenticated with Service Account Credentials.json
   (given as a first argument or an environment variable GOOGLE_APPLICATION_CREDENTIALS)"
  ([]
   (-> "GOOGLE_APPLICATION_CREDENTIALS"
       System/getenv
       credential/build-from-file
       connect!))

  ([credential]
   (reset! storage (.. (StorageOptions/newBuilder)
                       (setCredentials credential)
                       build
                       getService))))

(defn upload
  "Uploads a file `path` to `bucket` as `blob-name` with `content-type`."
  [bucket blob-name path content-type]
  (let [blob-id   (BlobId/of bucket blob-name)
        blob-info (.. (BlobInfo/newBuilder blob-id)
                      (setContentType content-type)
                      build)
        stream    (-> path io/file .toPath Files/readAllBytes)]
    (.create @storage blob-info stream (into-array Storage$BlobTargetOption nil))))

(defn upload-flac
  "Uploads a FLAC file `path` to `bucket` as `blob-name`"
  [bucket blob-name path]
  (let [content-type "audio/x-flac"]
    (upload bucket blob-name path content-type)))
