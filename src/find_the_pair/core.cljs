(ns find-the-pair.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defn create-board [width height]
  (vec (repeat height (vec (repeat width 0)))))

(defn generate-board [cards width height]
  (vec (map (fn [x y] (vec y)) (create-board width height) (partition width (shuffle cards)))))

(defn init-board [width height]
  (let [possible-card-ranks (range 1 (+ (/ (* width height) 2) 1))
        actual-cards (vec (apply concat (map (fn [x] [x x]) possible-card-ranks)))]
    (generate-board actual-cards width height)))

(def grid-width 4)
(def grid-height 3)
(def card-size 0.98)

(def initial-app-state
  {:text "Find the pair!"
   :board (init-board grid-width grid-height)
   :selected-cards [[nil nil] [nil nil]]
   :turn 0})

(defonce app-state
  (atom initial-app-state))


;; ===== UI =====

(defn turn-value []
  (get-in @app-state [:turn]))

(defn set-selected-cards! [x y]
  (if (< (turn-value) 2)
    (swap! app-state assoc-in [:selected-cards (turn-value)] [x y])
    (swap! app-state assoc-in [:selected-cards] [[nil nil] [nil nil]])))

(defn set-turn! []
  (if (< (turn-value) 2)
    (swap! app-state assoc-in [:turn] (inc (get-in @app-state [:turn])))
    (swap! app-state assoc-in [:turn] 0)))

(defn debug-state []
  (prn (:board @app-state))
  #_(prn (:selected-cards @app-state))
  #_(prn (:turn @app-state)))

(defn selected-card? [x y]
  (or (= [x y] (get-in @app-state [:selected-cards 0]))
      (= [x y] (get-in @app-state [:selected-cards 1]))))

(defn show-card-rank [x y]
  (prn (get-in @app-state [:board y x]))
  (get-in @app-state [:board y x]))

(defn find-the-pair []
  [:div
   [:h1 (:text @app-state)]
   (into
     [:svg
      {:view-box (str "0 0 " grid-width " " grid-height)
       :width 500
       :height 500}]
     (for [x (range grid-width)
           y (range grid-height)]
       [:rect {:x x
               :y y
               :width card-size
               :height card-size
               :fill (if (selected-card? x y)
                       "#00ABE1"
                       "#ccc")
               :on-click
               (fn card-click [e]
                 (set-selected-cards! x y)
                 (show-card-rank x y)
                 (set-turn!)
                 (debug-state))}]))])

(reagent/render-component [find-the-pair]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;;(swap! app-state assoc-in [:text] "Siili")
  )
