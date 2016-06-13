(ns find-the-pair.points-flash)


(defn points-flash [show? text style]
  [:p {:style (merge {:animation (if show? "fadeOutUp 0.7s forwards")}
                     style)}
   text])
