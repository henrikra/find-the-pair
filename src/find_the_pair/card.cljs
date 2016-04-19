(ns find-the-pair.card
  (:require [find-the-pair.init :as init]))

(defn card [x y container-width cards-per-row flipped-card card-exists set-flipped-card both-cards-flipped card-icon]
  (let [card-side (/ container-width cards-per-row)]
    [:div.card {:style {:width (str card-side "px")
                   :height (str card-side "px")}}
    [:div {:class (if flipped-card
                    "card__sides card__sides--flipped"
                    "card__sides")
           :on-click (fn []
                       (if (and card-exists
                                (not flipped-card)
                                (not both-cards-flipped))
                         (set-flipped-card x y)))
           :style (if card-exists
                    {:cursor "pointer"})}
     [:div.card__side.card__back {:style (if (not card-exists)
                     {:background "#ecf0f1"})}]
     [:div.card__side.card__front
      (if flipped-card
        [:i {:class (str "fa " card-icon)
             :style {:font-size (str (* card-side 0.4) "px")}}])]]]))
