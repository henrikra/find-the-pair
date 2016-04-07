(ns find-the-pair.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)


(def card-size 0.95)
(def grid-width 2)
(def grid-height 3)

(defn random-card [card-numbers]
  (let [random-number (rand-int (- (count card-numbers) 1))
        chosen-number (nth card-numbers random-number)]
    chosen-number))

(defn init-cards []
  (let [pair-amount (range 1 (+ (/ (* grid-width grid-height) 2) 1))
        card-numbers (apply concat (map (fn [x] [x x]) pair-amount))
        card-numbers-partitioned (partition 2 (shuffle card-numbers))]
    card-numbers-partitioned))

(defn generate-board [width height]
  (vec (repeat height (vec (repeat width 0)))))

(defn fill-board-with-cards [board cards]
  (map (fn [[x1 x2] [y1 y2]] [y1 y2]) board cards))

(defn init-board [width height]
  (fill-board-with-cards (generate-board width height) (init-cards)))

(prn (init-board grid-width grid-height))

(def initial-app-state
  {:text "Find the pair!"
   :board (init-board grid-width grid-height)
   :selected-cards [[nil nil] [nil nil]]
   :turn 0})

(defonce app-state
  (atom initial-app-state))

(defn card-value [row column]
  (get-in @app-state [:board row column]))

(defn turn-value []
  (get-in @app-state [:turn]))

(defn set-selected-cards! [row column]
  (if (< (turn-value) 2)
    (swap! app-state assoc-in [:selected-cards (turn-value)] [row column])
    (swap! app-state assoc-in [:selected-cards] [[nil nil] [nil nil]])))

(defn set-turn! []
  (if (< (turn-value) 2)
    (swap! app-state assoc-in [:turn] (inc (get-in @app-state [:turn])))
    (swap! app-state assoc-in [:turn] 0)))

(defn debug-state []
  (prn (:board @app-state))
  (prn (:selected-cards @app-state))
  #_(prn (:turn @app-state)))

(defn selected-card? [row column]
  (or (= [row column] (get-in @app-state [:selected-cards 0]))
      (= [row column] (get-in @app-state [:selected-cards 1]))))

(defn find-the-pair []
  [:div
   [:h1 (:text @app-state)]
   (into
     [:svg
      {:view-box (str "0 0 " grid-width " " grid-height)
       :width 500
       :height 500}]
     (for [column (range grid-width)
           row (range grid-height)]
       [:rect {:x column
               :y row
               :width card-size
               :height card-size
               :fill (if (selected-card? row column)
                       "#00ABE1"
                       "#ccc")
               :on-click
               (fn card-click [e]
                 ()
                 (set-selected-cards! row column)
                 (set-turn!)
                 (debug-state))}]))])

(reagent/render-component [find-the-pair]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;;(swap! app-state assoc-in [:text] "Siili")
  )
