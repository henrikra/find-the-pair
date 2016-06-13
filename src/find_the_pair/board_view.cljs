(ns find-the-pair.board-view
  (:require [find-the-pair.init :as init]
            [find-the-pair.card :refer [card]]
            [find-the-pair.points-flash :refer [points-flash]]
            [find-the-pair.state :refer [game-points
                                         cards-per-row
                                         cards-per-column
                                         show-increase
                                         show-decrease]]))

(defn board-view []
  [:div
   (into
    [:div.board]
    (for [x (range (cards-per-row))
          y (range (cards-per-column))]
      (card x y)))
   [:p "Points: "
    [:span.victory__points (game-points)]]
   [:div.points
    (points-flash (show-increase) (str "+" init/points-increase) "increase")
    (points-flash (show-decrease) (str "-" init/points-decrease) "decrease")]])
