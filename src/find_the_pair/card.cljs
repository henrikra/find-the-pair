(ns find-the-pair.card
  (:require [find-the-pair.init :as init]
            [find-the-pair.style :as style]
            [find-the-pair.state :refer [cards-per-row
                                         flipped-card?
                                         both-cards-flipped?
                                         set-flipped-card
                                         card-exists?
                                         card-icon]]))

(defn card [x y]
  (let [card-side (/ init/container-width (cards-per-row))]
    [:div {:style (style/card card-side)}
     (if (card-exists? x y)
       [:div {:style (style/card-sides (flipped-card? x y))}
        [:div {:style (style/card-back)
               :on-click #(if (not (both-cards-flipped?))
                            (set-flipped-card x y))}]
        [:div {:style (style/card-front)}
         (if (flipped-card? x y)
           [:i {:class (card-icon x y)
                :style {:font-size (str (* card-side 0.4) "px")}}])]])]))
