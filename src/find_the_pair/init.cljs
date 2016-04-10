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

(def board-width 4)
(def board-height 4)
(def flipped-cards [[nil nil] [nil nil]])
(def points 0)

(def app-state
  {:board (board board-width board-height)
   :flipped-cards flipped-cards
   :points points
   :board-width board-width
   :board-height board-height
   :icons (icons)})
