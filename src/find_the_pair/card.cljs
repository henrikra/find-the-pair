(ns find-the-pair.card
  (:require [find-the-pair.init :as init]
            [find-the-pair.state :refer [cards-per-row
                                         flipped-card?
                                         both-cards-flipped?
                                         set-flipped-card
                                         card-exists?
                                         card-icon]]))

(defn card [x y]
  (let [card-side (/ init/container-width (cards-per-row))]
    [:div.card {:style {:width (str card-side "px")
                        :height (str card-side "px")}}
     [:div {:class (if (flipped-card? x y)
                     "card__sides card__sides--flipped"
                     "card__sides")
            :on-click (fn []
                        (if (and (card-exists? x y)
                                 (not (flipped-card? x y))
                                 (not (both-cards-flipped?)))
                          (set-flipped-card x y)))
            :style (if (card-exists? x y)
                     {:cursor "pointer"})}
      [:div.card__side.card__back {:style (if (not (card-exists? x y))
                                            {:background "#ecf0f1"})}]
      [:div.card__side.card__front
       (if (flipped-card? x y)
         [:i {:class (card-icon x y)
              :style {:font-size (str (* card-side 0.4) "px")}}])]]]))