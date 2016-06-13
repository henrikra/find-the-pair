(ns find-the-pair.points-flash)


(defn points-flash [show? text className]
  [:p {:class className
       :style {:animation (if show?
                            "fadeOutUp 0.7s forwards")}}
   text])
