(ns find-the-pair.style
  (:require [find-the-pair.init :as init]))

(def container
  {:max-width init/container-width
   :margin "0 auto"})

(defn card [card-side]
  {:width (str card-side "px")
   :height (str card-side "px")
   :position "relative"
   :-webkit-perspective "800px"
   :perspective "800px"
   :float "left"})

(defn card-sides [flipped?]
  (merge
    {:height "100%"
     :-webkit-transform-style "preserve-3d"
     :transform-style "preserve-3d"
     :transition "transform 0.5s"
     :cursor "pointer"}
    (if flipped? {:transform "rotateY(180deg)"})))

(def card-side
  {:color "#fff"
   :position "absolute"
   :width "100%"
   :height "100%"
   :-webkit-backface-visibility "hidden"
   :backface-visibility "hidden"
   :-webkit-display "flex"
   :display "flex"
   :-webkit-align-items "center"
   :align-items "center"
   :-webkit-justify-content "center"
   :justify-content "center"
   :border "3px solid #ecf0f1"})

(defn card-back []
  (merge card-side {:background "#bdc3c7"}))

(defn card-front []
  (merge card-side {:background "#1abc9c"
                    :transform "rotateY(180deg)"}))

(def board
  {:overflow "auto"
   :width "100%"})
