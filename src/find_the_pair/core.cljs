(ns find-the-pair.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [find-the-pair.init :as init]))

(enable-console-print!)

(defonce app-state (atom init/app-state))

;; ===== UI =====

(defn shuffle-icons! []
  (swap! app-state assoc :icons (init/icons)))

(defn reduce-points! []
  (swap! app-state assoc :points (dec (:points @app-state))))

(defn reset-flipped-cards! []
  (swap! app-state assoc :flipped-cards [[nil nil] [nil nil]]))

(defn board-width []
  (:board-width @app-state))

(defn board-height []
  (:board-height @app-state))

(defn reset-board! []
  (swap! app-state assoc :board (init/board (board-width) (board-height))))

(defn reset-points! []
  (swap! app-state assoc :points 0))

(defn flipped-card [index]
  (get-in @app-state [:flipped-cards index]))

(defn card-flipped? [index]
  (not (every? nil? (flipped-card index))))

(defn remove-card! [[x y]]
  (swap! app-state assoc-in [:board y x] nil))

(defn add-points! []
  (swap! app-state assoc :points (+ (:points @app-state) 4)))

(defn success []
  (add-points!)
  (remove-card! (flipped-card 0))
  (remove-card! (flipped-card 1))
  (reset-flipped-cards!))

(defn card-rank
  ([x y] (get-in @app-state [:board y x]))
  ([[x y]] (get-in @app-state [:board y x])))

(defn found-pair? []
  (= (card-rank (flipped-card 0))
     (card-rank (flipped-card 1))))

(defn mistake []
  (reduce-points!)
  (reset-flipped-cards!))

(defn check-for-pair []
  (js/setTimeout #(if (found-pair?)
                    (success)
                    (mistake)) 1000))

(defn second-card-flipped [x y]
  (swap! app-state assoc-in [:flipped-cards 1] [x y])
  (check-for-pair))

(defn set-flipped-card! [x y]
  (if (card-flipped? 0)
    (second-card-flipped x y)
    (swap! app-state assoc-in [:flipped-cards 0] [x y])))

(defn both-cards-flipped? []
  (and (= (card-flipped? 0)
          (card-flipped? 1)
          true)))

(defn set-flipped-cards [x y]
  (if (not (both-cards-flipped?))
    (set-flipped-card! x y)))

(defn debug-state []
  #_(prn (:board @app-state))
  #_(prn (:flipped-cards @app-state)))

(defn flipped-card? [x y]
  (or (= [x y] (flipped-card 0))
      (= [x y] (flipped-card 1))))

(defn card-icon [x y]
  (get-in @app-state [:icons (card-rank x y)] ))

(defn card [x y]
  (let [card-size 0.96]
    [:g
     [:rect {:width card-size
             :height card-size
             :x x
             :y y
             :fill (if (flipped-card? x y)
                     "#1abc9c"
                     "#bdc3c7")
             :rx 0.1
             :ry 0.1
             :on-click
             (fn card-click [e]
               (set-flipped-cards x y)
               (debug-state))}]
     [:text {:x (+ x (/ card-size 2))
             :y (+ y (/ card-size 2))
             :fill "#fff"
             :font-size 0.25
             :text-anchor "middle"}
      (if (flipped-card? x y)
        (card-icon x y))]]))

(defn card-exists? [x y]
  (not (nil? (card-rank x y))))

(defn set-board-width! [new-value]
  (swap! app-state assoc :board-width new-value))

(defn set-board-height! [new-value]
  (swap! app-state assoc :board-height new-value))

(defn set-board-dimensions [new-dimensions]
  (let [[new-width new-height] (str/split new-dimensions "x")]
    (set-board-width! (int new-width))
    (set-board-height! (int new-height))))

(defn reset-game []
  (reset-flipped-cards!)
  (reset-board!)
  (reset-points!)
  (shuffle-icons!))

(defn find-the-pair []
  [:div
   [:h1 "Find the pair!"]
   [:p
    [:button {:on-click
              (fn new-game-click [e]
                (reset-game))}
     "New game"]]
   [:p
    [:label "Difficulty: "]
    [:select {:on-change
              (fn difficulty-change [x]
                (set-board-dimensions (.. x -target -value))
                (reset-game))}
     [:option {:value "2x2"} "Drunk"]
     [:option {:value "3x2"} "Supa easy"]
     [:option {:value "4x3"} "Easy"]
     [:option {:value "4x4" :selected true} "Medium"]
     [:option {:value "6x5"} "Hard"]
     [:option {:value "8x7"} "Nightmare"]
     [:option {:value "10x10"} "Hell"]]]
   (into
     [:svg
      {:view-box (str "0 0 " (board-width) " " (board-height))
       :width 500
       :height 500}]
     (for [x (range (board-width))
           y (range (board-height))]
       (if (card-exists? x y)
         (card x y))))
   [:p "Points: " (:points @app-state)]])

(reagent/render-component [find-the-pair]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  )
