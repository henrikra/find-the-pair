(ns find-the-pair.app
  (:require [find-the-pair.init :as init]
            [find-the-pair.difficulty-dropdown :as dropdown]
            [find-the-pair.card :as card]
            [clojure.string :as str]
            [find-the-pair.victory-view :refer [victory-view]]
            [find-the-pair.state :refer [game-points
                                         cards-per-row
                                         cards-per-column
                                         game-won?
                                         show-increase
                                         show-decrease
                                         set-board-dimensions!
                                         reset-game]]))

(defn board-view []
  [:div
   (into
    [:div.board]
    (for [x (range (cards-per-row))
          y (range (cards-per-column))]
      (card/card x y)))
   [:p "Points: "
    [:span.victory__points (game-points)]]
   [:div.points
    [:p.increase {:style {:animation (if (show-increase)
                              "fadeOutUp 0.7s forwards")}} (str "+" init/points-increase)]
    [:p.decrease {:style {:animation (if (show-decrease)
                              "fadeOutUp 0.7s forwards")}} (str "-" init/points-decrease)]]])

(defn difficulty-change [x]
  (let [selected-dimensions (.. x -target -value)
        [per-row per-column] (str/split selected-dimensions "x")]
    (set-board-dimensions! (int per-row) (int per-column))
    (reset-game)))

(defn app []
  [:div.container {:style {:max-width init/container-width}}
   [:h1 "Find the pair!"]
   (dropdown/difficulty-dropdown difficulty-change)
   (if (game-won?)
     (victory-view)
     (board-view))])
