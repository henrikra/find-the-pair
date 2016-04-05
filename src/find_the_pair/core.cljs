(ns find-the-pair.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defn new-board [size]
  (vec (repeat size (vec (repeat size 0)))))

(prn (new-board 4))

(defonce app-state
  (atom {:text "Find the pair!"
         :board (new-board 4)}))

(defn find-the-pair []
  [:div
   [:h1 (:text @app-state)]
   [:svg
    {:view-box "0 0 4 4"
     :width 500
     :height 500}
    (for [i (range (count (:board @app-state)))
          j (range (count (:board @app-state)))]
      [:rect {:x i
              :y j
              :width 0.9
              :height 0.9
              :fill "#00ABE1"
              :on-click
              (fn [e]
                (prn "You just clicked!"))}])]])

(reagent/render-component [find-the-pair]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;;(swap! app-state assoc-in [:text] "Siili")
  )
