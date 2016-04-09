(ns find-the-pair.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]))

(enable-console-print!)

(defn generate-board [cards width]
  (vec (map
         #(vec %)
         (partition width (shuffle cards)))))

(defn max-card [width height]
  (+ (/ (* width height) 2) 1))

(defn init-board [width height]
  (let [possible-cards (range 1 (max-card width height))
        all-cards (flatten (repeat 2 possible-cards))]
    (generate-board all-cards width)))

(def grid-width 4)
(def grid-height 4)
(def card-size 0.96)

(def initial-app-state
  {:text "Find the pair!"
   :board (init-board grid-width grid-height)
   :flipped-cards [[nil nil] [nil nil]]
   :points 0
   :board-width grid-width
   :board-height grid-height})

(defonce app-state
  (atom initial-app-state))


;; ===== UI =====

(defn reduce-points! []
  (swap! app-state assoc :points (dec (:points @app-state))))

(defn reset-flipped-cards! []
  (swap! app-state assoc-in [:flipped-cards] [[nil nil] [nil nil]]))

(defn reset-board! []
  (swap! app-state assoc-in [:board] (init-board (:board-width @app-state) (:board-height @app-state))))

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

(defn card [x y]
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
      (card-rank x y))]])

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
  (reset-points!))

(defn find-the-pair []
  [:div
   [:h1 (:text @app-state)]
   [:p
    [:button {:on-click
              (fn new-game-click [e]
                (reset-game))}
     "New game"]]
   [:p
    [:select {:on-change
              (fn difficulty-change [x]
                (set-board-dimensions (.. x -target -value))
                (reset-game))}
     [:option {:value "3x2"} "Supa easy"]
     [:option {:value "4x3"} "Easy"]
     [:option {:value "4x4" :selected true} "Medium"]
     [:option {:value "6x5"} "Hard"]
     [:option {:value "8x7"} "Nightmare"]
     [:option {:value "10x10"} "Hell"]]]
   (into
     [:svg
      {:view-box (str "0 0 " (:board-width @app-state) " " (:board-height @app-state))
       :width 500
       :height 500}]
     (for [x (range (:board-width @app-state))
           y (range (:board-height @app-state))]
       (if (card-exists? x y)
         (card x y))))
   [:p "Points: " (:points @app-state)]])

(reagent/render-component [find-the-pair]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;;(swap! app-state assoc-in [:text] "Siili")
  )
