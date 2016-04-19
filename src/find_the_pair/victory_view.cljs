(ns find-the-pair.victory-view)


(defn victory-view [on-click game-points]
  [:div.victory
   [:i {:class (if (pos? game-points)
                 "victory__icon fa fa-thumbs-up"
                 "victory__icon fa fa-thumbs-down")}]
   [:h2 "All pairs found!"]
   [:p "Points: "
    [:span.victory__points game-points]]
   [:p
    [:button.victory__new-game
     {:on-click on-click}
     "New game"]]])
