(ns pp-server.core.fn-maps
  (:require [pp-server.format.clojure-pp :refer [format-clj!]]
            [pp-server.format.web-pp :refer [format-json!]]
            [pp-server.format.data-pp :refer [format-xml!]]
            [pp-server.format.parse-error-handler :refer [parse-exception!]]
            [clojure.tools.logging :as log]))

(def typefn
  {"clojure"  format-clj!
   "edn"      format-clj!
   "json"     format-json!
   "xml"      format-xml!})

(defn- to-lower
  [word]
  (clojure.string/lower-case word))

(defn mapfn
  "Retrieves a function from the typefn map to execute formatting. Function
  signature must be `[input format-type]`."
  [input tipe]
  (let [type-norm (to-lower tipe)]
    (try
      {:status 200
       :headers {"Content-Type" "text/plain"}
       :body ((get typefn type-norm) input type-norm)}
      (catch Exception e
        (let [err-msg (.getMessage e)]
          (log/error err-msg)
           {:status 400
            :headers {"Content-Type" "application/json"}
            :body (parse-exception! err-msg)})))))