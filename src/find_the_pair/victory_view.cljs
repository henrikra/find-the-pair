(ns find-the-pair.victory-view
  (:require [find-the-pair.state :refer [game-points]]))


(defn victory-view [on-click]
  [:div.victory
   [:i {:class (if (pos? (game-points))
                 "victory__icon fa fa-thumbs-up"
                 "victory__icon fa fa-thumbs-down")}]
   [:h2 "All pairs found!"]
   [:p "Points: "
    [:span.victory__points (game-points)]]
   [:p
    [:button.victory__new-game
     {:on-click on-click}
     "New game"]]])
