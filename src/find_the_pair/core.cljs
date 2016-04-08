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
   :flipped-cards [[nil nil] [nil nil]]})

(defonce app-state
  (atom initial-app-state))


;; ===== UI =====

(defn reset-flipped-cards! []
  (swap! app-state assoc-in [:flipped-cards] [[nil nil] [nil nil]]))

(defn flipped-card [index]
  (get-in @app-state [:flipped-cards index]))

(defn card-flipped? [index]
  (not (every? nil? (flipped-card index))))

(defn set-flipped-card! [x y]
  (if (card-flipped? 0)
    (swap! app-state assoc-in [:flipped-cards 1] [x y])
    (swap! app-state assoc-in [:flipped-cards 0] [x y])))

(defn both-cards-flipped? []
  (and (= (card-flipped? 0)
          (card-flipped? 1)
          true)))

(defn set-flipped-cards [x y]
  (if (both-cards-flipped?)
    (reset-flipped-cards!)
    (set-flipped-card! x y)))

(defn debug-state []
  #_(prn (:board @app-state))
  #_(prn (:flipped-cards @app-state)))

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

(defn pair? [first-card second-card]
  (= first-card second-card))

(defn check-for-pair []
  (let [[[first-card-x first-card-y] [second-card-x second-card-y]]
        (:flipped-cards @app-state)
        first-card (card-rank first-card-x first-card-y)
        second-card (card-rank second-card-x second-card-y)]
    (if (and (both-cards-flipped?)
             (pair? first-card second-card))
      (remove-found-pair first-card-x first-card-y second-card-x second-card-y))))

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
             (set-flipped-cards x y)
             (card-rank x y)
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
