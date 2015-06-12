(ns ^:figwheel-always cljs-leaflet-experiment.core
    (:require
              [reagent.core :as reagent :refer [atom]]
              [cljsjs.leaflet]))

(enable-console-print!)

(defonce app-state (atom {:text "ClojureScript + Leaflet + Reagent"
                          :zoom 10
                          :center #js [41.505, -0.09]}))

(defonce lmap (.map js/L "map"))
(defonce osm (js/L.tileLayer "http://{s}.tile.osm.org/{z}/{x}/{y}.png"))

(defn sync-state-to-map []
  (let [zoom (.getZoom lmap)
        ll (.getCenter lmap)
        center #js [(.-lat ll) (.-lng ll) ]]
    (swap! app-state merge {:zoom zoom :center center})))

(defonce zoomend-handler (.on lmap "zoomend" sync-state-to-map))
(defonce moveend-hander  (.on lmap "moveend" sync-state-to-map))
(defonce layeradd (.addLayer lmap osm))

(defn the-map []
  (.setView lmap (:center @app-state) (:zoom @app-state))
  [:div])

(defn zoom [nz]
  (swap! app-state assoc :zoom nz))

(defn zoom-in []
  (let [nz (inc (:zoom @app-state))]
    (zoom nz)))

(defn zoom-out []
  (let [nz (dec (:zoom @app-state))]
    (zoom nz)))

(defn zoom-philly []
  (swap! app-state merge {:zoom 11 :center #js [39.95 -75.16]}))

(defn map-deets []
  [:div
   [:div "zoom " (:zoom @app-state)]
   [:div "lat " (first (:center @app-state))]
   [:div "lng " (second (:center @app-state))]])

(defn map-and-controlls []
  [:div
   [the-map]
   [:div [:b (:text @app-state)]]
   [map-deets]
   [:button {:on-click zoom-in} "zoom in"]
   [:button {:on-click zoom-out} "zoom out"]
   [:button {:on-click zoom-philly} "philly"]])

(reagent/render-component [map-and-controlls]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
