(ns halboy.navigator-test
  (:use org.httpkit.fake)
  (:require [expectations :refer :all]
            [halboy.navigator :as navigator]
            [halboy.resource :as resource]
            [halresource.resource
             :refer [new-resource
                     add-link
                     resource->representation]]
            [halboy.support.api :refer [on-discover]]))

(def base-url "https://service.example.com")

; discover
(with-fake-http
  (concat
    (on-discover
      base-url
      {:status 200
       :body   (-> (new-resource base-url)
                   (add-link :rel :users :href "https://service.example.com/users")
                   (resource->representation :json))}))
  (let [response (navigator/discover base-url)
        status (-> (navigator/get-meta response)
                   :status)
        resource (navigator/get-resource response)]

    ;; should return status code 200
    (expect 200 status)

    ;; should return the contents of the discovery endpoint
    (expect
      "https://service.example.com/users"
      (resource/get-link resource :users))))
