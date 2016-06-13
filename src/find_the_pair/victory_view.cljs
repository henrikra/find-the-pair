(ns find-the-pair.victory-view
  (:require [find-the-pair.style :as style]
            [find-the-pair.state :refer [game-points
                                         reset-game]]))


(defn victory-view []
  [:div {:style style/victory}
   [:i {:style style/victory-icon
        :class (if (pos? (game-points))
                 "fa fa-thumbs-up"
                 "fa fa-thumbs-down")}]
   [:h2 "All pairs found!"]
   [:p "Points: "
    [:span {:style style/victory-points} (game-points)]]
   [:p
    [:button
     {:style style/victory-new-game
      :on-click reset-game}
     "New game"]]])
