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
