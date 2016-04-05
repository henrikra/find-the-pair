(ns find-the-pair.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def card-size 0.95)

(defn new-board [size]
  (vec (repeat size (vec (repeat size 0)))))

(prn (new-board 4))

(defonce app-state
  (atom {:text "Find the pair!"
         :board (new-board 4)}))

(defn card-value [row column]
  (get-in @app-state [:board row column]))

(defn find-the-pair []
  [:div
   [:h1 (:text @app-state)]
   [:svg
    {:view-box "0 0 4 4"
     :width 500
     :height 500}
    (for [column (range (count (:board @app-state)))
          row (range (count (:board @app-state)))]
      [:rect {:x column
              :y row
              :width card-size
              :height card-size
              :fill (if (zero? (card-value row column))
                      "#ccc"
                      "#00ABE1")
              :on-click
              (fn card-click [e]
                (prn "You clicked card" column row)
                (swap! app-state assoc-in [:board row column]
                       (inc (card-value row column)))
                (prn (:board @app-state)))}])]])

(reagent/render-component [find-the-pair]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;;(swap! app-state assoc-in [:text] "Siili")
  )
