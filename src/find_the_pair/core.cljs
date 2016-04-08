(ns find-the-pair.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defn generate-board [cards width]
  (vec (map
         #(vec %)
         (partition width (shuffle cards)))))

(defn duplicate [card]
  [card card])

(defn max-card [width height]
  (+ (/ (* width height) 2) 1))

(defn init-board [width height]
  (let [possible-cards (range 1 (max-card width height))
        all-cards (apply concat (map duplicate possible-cards))]
    (generate-board all-cards width)))

(def grid-width 4)
(def grid-height 3)
(def card-size 0.99)

(def initial-app-state
  {:text "Find the pair!"
   :board (init-board grid-width grid-height)
   :flipped-cards [[nil nil] [nil nil]]
   :turn 0})

(defonce app-state
  (atom initial-app-state))


;; ===== UI =====

(defn turn-value []
  (get-in @app-state [:turn]))

(defn set-selected-cards! [x y]
  (if (< (turn-value) 2)
    (swap! app-state assoc-in [:flipped-cards (turn-value)] [x y])
    (swap! app-state assoc-in [:flipped-cards] [[nil nil] [nil nil]])))

(defn set-turn! []
  (if (< (turn-value) 2)
    (swap! app-state assoc-in [:turn] (inc (get-in @app-state [:turn])))
    (swap! app-state assoc-in [:turn] 0)))

(defn debug-state []
  (prn (:board @app-state))
  (prn (:flipped-cards @app-state))
  #_(prn (:turn @app-state)))

(defn flipped-card [index]
  (get-in @app-state [:flipped-cards index]))

(defn flipped-card? [x y]
  (or (= [x y] (flipped-card 0))
      (= [x y] (flipped-card 1))))

(defn card-rank [x y]
  (get-in @app-state [:board y x]))

(defn remove-card! [x y]
  (swap! app-state assoc-in [:board y x] nil))

(defn remove-found-pair [first-card-x first-card-y second-card-x second-card-y]
  (remove-card! first-card-x first-card-y)
  (remove-card! second-card-x second-card-y))

(defn check-for-pair []
  (let [[[first-card-x first-card-y] [second-card-x second-card-y]] (:flipped-cards @app-state)
        first-card-rank (card-rank first-card-x first-card-y)
        second-card-rank (card-rank second-card-x second-card-y)]
    (if (or (nil? first-card-rank) (nil? second-card-rank))
      false
      (if (= first-card-rank second-card-rank)
        (remove-found-pair first-card-x first-card-y second-card-x second-card-y)))))

(defn card [x y]
  [:g
   [:rect {:width card-size
           :height card-size
           :x x
           :y y
           :fill (if (flipped-card? x y)
                   "#00ABE1"
                   "#ccc")
           :on-click
           (fn card-click [e]
             (set-selected-cards! x y)
             (card-rank x y)
             (set-turn!)
             (check-for-pair)
             (debug-state))}]
   [:text {:x (+ x (/ card-size 2))
           :y (+ y (/ card-size 2))
           :fill "yellow"
           :font-size 0.25
           :font-family "Verdana"
           :text-anchor "middle"}
    (if (flipped-card? x y)
      (card-rank x y))]])

(defn card-exists? [x y]
  (not (nil? (card-rank x y))))

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
       (if (card-exists? x y)
         (card x y))))])

(reagent/render-component [find-the-pair]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;;(swap! app-state assoc-in [:text] "Siili")
  )
