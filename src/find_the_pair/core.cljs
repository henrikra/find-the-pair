(ns find-the-pair.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [find-the-pair.init :as init]
            [find-the-pair.difficulty-dropdown :as dropdown]
            [find-the-pair.card :as card]
            [find-the-pair.victory-view :as victory-view]))

(enable-console-print!)

(defonce app-state (atom init/app-state))

;; ===== UI =====

(defn reset-icons! []
  (swap! app-state assoc :icons (init/icons)))

(defn game-points []
  (:points @app-state))

(defn reduce-points! []
  (swap! app-state assoc :points
         (- (game-points) init/points-decrease)))

(defn reset-flipped-cards! []
  (swap! app-state assoc :flipped-cards init/flipped-cards))

(defn reset-show-increase! []
  (swap! app-state assoc :show-increase false))

(defn reset-show-decrease! []
  (swap! app-state assoc :show-decrease false))

(defn cards-per-row []
  (:cards-per-row @app-state))

(defn cards-per-column []
  (:cards-per-column @app-state))

(defn reset-board! []
  (swap! app-state assoc :board
         (init/board (cards-per-row) (cards-per-column))))

(defn reset-points! []
  (swap! app-state assoc :points init/points))

(defn flipped-card [index]
  (get-in @app-state [:flipped-cards index]))

(defn card-flipped? [index]
  (not (every? nil? (flipped-card index))))

(defn remove-card! [[x y]]
  (swap! app-state assoc-in [:board y x] nil))

(defn add-points! []
  (swap! app-state assoc :points
         (+ (game-points) init/points-increase)))

(defn game-won? []
  (every? nil? (flatten (get @app-state :board))))

(defn show-increase []
  (:show-increase @app-state))

(defn show-decrease []
  (:show-decrease @app-state))

(defn set-increase [new-value]
  (swap! app-state assoc :show-increase new-value))

(defn set-decrease [new-value]
  (swap! app-state assoc :show-decrease new-value))

(defn success []
  (add-points!)
  (remove-card! (flipped-card 0))
  (remove-card! (flipped-card 1))
  (reset-flipped-cards!)
  (set-increase true))

(defn card-rank
  ([x y] (get-in @app-state [:board y x]))
  ([[x y]] (get-in @app-state [:board y x])))

(defn found-pair? []
  (= (card-rank (flipped-card 0))
     (card-rank (flipped-card 1))))

(defn mistake []
  (reduce-points!)
  (set-decrease true)
  (reset-flipped-cards!))

(defn check-for-pair []
  (js/setTimeout #(if (found-pair?)
                    (success)
                    (mistake)) init/cards-visible-time))

(defn second-card-flipped! [x y]
  (swap! app-state assoc-in [:flipped-cards 1] [x y])
  (check-for-pair))

(defn first-card-flipped! [x y]
  (set-increase false)
  (set-decrease false)
  (swap! app-state assoc-in [:flipped-cards 0] [x y]))

(defn set-flipped-card [x y]
  (if (card-flipped? 0)
    (second-card-flipped! x y)
    (first-card-flipped! x y)))

(defn both-cards-flipped? []
  (and (= (card-flipped? 0)
          (card-flipped? 1)
          true)))

(defn flipped-card? [x y]
  (or (= [x y] (flipped-card 0))
      (= [x y] (flipped-card 1))))

(defn card-icon [x y]
  (get-in @app-state [:icons (card-rank x y)] ))

(defn card-exists? [x y]
  (not (nil? (card-rank x y))))

(defn set-cards-per-row! [new-per-row]
  (swap! app-state assoc :cards-per-row new-per-row))

(defn set-cards-per-column! [new-per-column]
  (swap! app-state assoc :cards-per-column new-per-column))

(defn set-board-dimensions [new-dimensions]
  (let [[new-per-row new-per-column] (str/split new-dimensions "x")]
    (set-cards-per-row! (int new-per-row))
    (set-cards-per-column! (int new-per-column))))

(defn reset-game []
  (reset-flipped-cards!)
  (reset-board!)
  (reset-points!)
  (reset-icons!)
  (reset-show-increase!))

(defn new-game-click []
  (reset-game))

(defn board-view []
  [:div
   (into
    [:div.board]
    (for [x (range (cards-per-row))
          y (range (cards-per-column))]
      (card/card x
                 y
                 init/container-width
                 (cards-per-row)
                 (flipped-card? x y)
                 (card-exists? x y)
                 set-flipped-card
                 (both-cards-flipped?)
                 (card-icon x y))))
   [:p "Points: "
    [:span.victory__points (game-points)]]
   [:div.points
    [:p.increase {:style {:animation (if (show-increase)
                              "fadeOutUp 0.7s forwards")}} (str "+" init/points-increase)]
    [:p.decrease {:style {:animation (if (show-decrease)
                              "fadeOutUp 0.7s forwards")}} (str "-" init/points-decrease)]]])

(defn difficulty-change [x]
  (let [selected-dimensions (.. x -target -value)]
    (set-board-dimensions selected-dimensions)
    (reset-game)))

(defn find-the-pair []
  [:div.container {:style {:max-width init/container-width}}
   [:h1 "Find the pair!"]
   (dropdown/difficulty-dropdown difficulty-change)
   (if (game-won?)
     (victory-view/victory-view new-game-click (game-points))
     (board-view))])

(reagent/render-component [find-the-pair]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  )
