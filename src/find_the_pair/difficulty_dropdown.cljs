(ns find-the-pair.difficulty-dropdown)

(defn difficulty-dropdown [on-change]
  [:form.difficulty-dropdown
   [:label "Difficulty: "]
   [:select {:on-change on-change}
    [:option {:value "2x2"} "Drunk"]
    [:option {:value "3x2"} "Supa easy"]
    [:option {:value "4x3"} "Easy"]
    [:option {:value "4x4" :selected true} "Medium"]
    [:option {:value "6x5"} "Hard"]
    [:option {:value "8x7"} "Nightmare"]
    [:option {:value "10x10"} "Hell"]]])
