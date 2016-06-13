(ns find-the-pair.difficulty-dropdown
  (:require [clojure.string :as str]
            [find-the-pair.style :as style]
            [find-the-pair.state :refer [reset-game
                                         set-board-dimensions!]]))

(defn difficulty-change [x]
  (let [selected-dimensions (.. x -target -value)
        [per-row per-column] (str/split selected-dimensions "x")]
    (set-board-dimensions! (int per-row) (int per-column))
    (reset-game)))

(defn difficulty-dropdown []
  [:form {:style style/difficulty-dropdown}
   [:label "Difficulty: "]
   [:select {:on-change difficulty-change}
    [:option {:value "2x2"} "Drunk"]
    [:option {:value "3x2"} "Supa easy"]
    [:option {:value "4x3"} "Easy"]
    [:option {:value "4x4" :selected true} "Medium"]
    [:option {:value "6x5"} "Hard"]
    [:option {:value "8x7"} "Nightmare"]
    [:option {:value "10x10"} "Hell"]]])
