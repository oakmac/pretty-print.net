(ns pp-jvm.core.handler
  (:use ring.adapter.jetty)
  (:require [ring.middleware.refresh :refer [wrap-refresh]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :refer [parse-string]]
            [pp-jvm.core.fn-maps :refer [mapfn]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.tools.logging :as log]))

(def config (parse-string (slurp "../config/config.json") true))

(defn- decode-body! [req] (parse-string (slurp (:body req)) true))

(defroutes app-routes
  (POST "/jvm/format/:tipe" request
    (log/debug request)
    (let [tipe (:tipe (:params request))
          body (decode-body! request)
          input (:input body)
          settings (:settings body)]
      (log/info "Formating to type " tipe)
      (log/debug "input: " input)
      (log/debug "settings: " settings)
      (mapfn input tipe settings)))
  (route/not-found "Not Found"))

(run-jetty (wrap-refresh app-routes) {:port (:jvm-server-port config)})