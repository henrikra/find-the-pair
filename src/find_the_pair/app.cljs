(ns find-the-pair.app
  (:require [find-the-pair.init :as init]
            [find-the-pair.difficulty-dropdown :refer [difficulty-dropdown]]
            [find-the-pair.victory-view :refer [victory-view]]
            [find-the-pair.state :refer [game-won?]]
            [find-the-pair.board-view :refer [board-view]]))

(defn app []
  [:div.container {:style {:max-width init/container-width}}
   [:h1 "Find the pair!"]
   (difficulty-dropdown)
   (if (game-won?)
     (victory-view)
     (board-view))])
