(ns find-the-pair.app
  (:require [find-the-pair.difficulty-dropdown :refer [difficulty-dropdown]]
            [find-the-pair.victory-view :refer [victory-view]]
            [find-the-pair.state :refer [game-won?]]
            [find-the-pair.board-view :refer [board-view]]
            [find-the-pair.style :as style]))

(defn app []
  [:div.container {:style style/container}
   [:h1 "Find the pair!"]
   (difficulty-dropdown)
   (if (game-won?)
     (victory-view)
     (board-view))])
