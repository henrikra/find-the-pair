(ns find-the-pair.init
  (:require [find-the-pair.misc :as misc]))

(defn generate-board [cards width]
  (vec (map
         #(vec %)
         (partition width (shuffle cards)))))

(defn max-card [width height]
  (+ (/ (* width height) 2) 1))

(defn board [width height]
  (let [possible-cards (range 1 (max-card width height))
        all-cards (flatten (repeat 2 possible-cards))]
    (generate-board all-cards width)))

(defn icons []
  (shuffle misc/icons))

(def cards-per-row 4)
(def cards-per-column 4)
(def flipped-cards [[nil nil] [nil nil]])
(def points 0)
(def cards-visible-time 1000)
(def container-width 500)

(def app-state
  {:board (board cards-per-row cards-per-column)
   :flipped-cards flipped-cards
   :points points
   :cards-per-row cards-per-row
   :cards-per-column cards-per-column
   :icons (icons)})
