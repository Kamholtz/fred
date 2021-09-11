(ns fred.annotate
  (:require [org.httpkit.client :as http]
            [org.httpkit.sni-client :as sni-client]
            [cheshire.core :as json]
            [clojure.data.codec.base64 :as b64]
            [clojure.string :as str]
            [clojure.java.io :as io]))


;; Change default client for your whole application:
(alter-var-root #'org.httpkit.client/*default-client* (fn [_] sni-client/default-client))

(defn- request [token feature-type image-data]
  {:method :post
   :timeout 300000
   :url (str "https://vision.googleapis.com/v1/images:annotate?key=" token)
   ;:insecure? true
   :body (json/encode
          {:requests
           [{:image {:content image-data}
             :features [{:type feature-type}]}]})})


(defn- encode-image [path]
  (let [file (io/file path)
        ary (byte-array (.length file))]
    (with-open [is (java.io.FileInputStream. file)]
      (.read is ary))
    (String. (b64/encode ary) "UTF-8")))

(defn- annotate [feature-type path]
  (let [image-data (encode-image path)
        token   (str/replace (slurp "./secrets/api.txt") "\n" "")

        ;; (slurp "./secrets/api.txt")
        request-data (request token feature-type image-data)
        response @(http/request request-data)]
    (if (= 200 (:status response))
      {:valid? true :annotate-type feature-type
       :data (-> response
                 :body
                 (json/decode keyword))}
      {:valid? false :annotate-type feature-type
       :data (dissoc response :opts)})))

(defn annotate-text [path] (annotate "TEXT_DETECTION" path))
(defn annotate-document [path] (annotate "DOCUMENT_TEXT_DETECTION" path))
