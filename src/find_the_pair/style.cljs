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
