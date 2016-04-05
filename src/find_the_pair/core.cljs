(ns find-the-pair.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def card-size 0.95)

(defn new-board [size]
  (vec (repeat size (vec (repeat size 0)))))

(prn (new-board 4))

(defonce app-state
  (atom {:text "Find the pair!"
         :board (new-board 4)
         :selected-cards [[99 99] [99 99]]
         :turn 0}))

(defn card-value [row column]
  (get-in @app-state [:board row column]))

(defn turn-value []
  (get-in @app-state [:turn]))

(defn set-selected-cards [row column]
  (if (< (turn-value) 2)
    (swap! app-state assoc-in [:selected-cards (turn-value)] [row column])
    (swap! app-state assoc-in [:selected-cards] [[99 99] [99 99]])))

(defn set-turn []
  (if (< (turn-value) 2)
    (swap! app-state assoc-in [:turn] (inc (get-in @app-state [:turn])))
    (swap! app-state assoc-in [:turn] 0)))

(defn debug-state []
  ;;(prn (:board @app-state))
  (prn (:selected-cards @app-state))
  (prn (:turn @app-state)))

(defn change-board-state [row column]
  (swap! app-state assoc-in [:board row column]
         (inc (card-value row column))))

(defn selected-card? [row column]
  (or (= [row column] (get-in @app-state [:selected-cards 0]))
      (= [row column] (get-in @app-state [:selected-cards 1]))))

(defn find-the-pair []
  [:div
   [:h1 (:text @app-state)]
   (into
     [:svg
      {:view-box "0 0 4 4"
       :width 500
       :height 500}]
     (for [column (range (count (:board @app-state)))
           row (range (count (:board @app-state)))]
       [:rect {:x column
               :y row
               :width card-size
               :height card-size
               :fill (if (selected-card? row column)
                       "#00ABE1"
                       "#ccc")
               :on-click
               (fn card-click [e]
                 ;;(prn "You clicked card" column row)
                 (set-selected-cards row column)
                 (set-turn)
                 (change-board-state row column)
                 (debug-state))}]))])

(reagent/render-component [find-the-pair]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;;(swap! app-state assoc-in [:text] "Siili")
  )
