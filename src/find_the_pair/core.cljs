(ns find-the-pair.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [find-the-pair.init :as init]))

(enable-console-print!)

(defonce app-state (atom init/app-state))

;; ===== UI =====

(defn reset-icons! []
  (swap! app-state assoc :icons (init/icons)))

(defn reduce-points! []
  (swap! app-state assoc :points (dec (:points @app-state))))

(defn reset-flipped-cards! []
  (swap! app-state assoc :flipped-cards init/flipped-cards))

(defn cards-per-row []
  (:cards-per-row @app-state))

(defn cards-per-column []
  (:cards-per-column @app-state))

(defn reset-board! []
  (swap! app-state assoc :board (init/board (cards-per-row) (cards-per-column))))

(defn reset-points! []
  (swap! app-state assoc :points init/points))

(defn flipped-card [index]
  (get-in @app-state [:flipped-cards index]))

(defn card-flipped? [index]
  (not (every? nil? (flipped-card index))))

(defn remove-card! [[x y]]
  (swap! app-state assoc-in [:board y x] nil))

(defn add-points! []
  (swap! app-state assoc :points (+ (:points @app-state) 3)))

(defn game-won? []
  (every? nil? (flatten (get @app-state :board))))

(defn success []
  (add-points!)
  (remove-card! (flipped-card 0))
  (remove-card! (flipped-card 1))
  (reset-flipped-cards!))

(defn card-rank
  ([x y] (get-in @app-state [:board y x]))
  ([[x y]] (get-in @app-state [:board y x])))

(defn found-pair? []
  (= (card-rank (flipped-card 0))
     (card-rank (flipped-card 1))))

(defn mistake []
  (reduce-points!)
  (reset-flipped-cards!))

(defn check-for-pair []
  (js/setTimeout #(if (found-pair?)
                    (success)
                    (mistake)) init/cards-visible-time))

(defn second-card-flipped [x y]
  (swap! app-state assoc-in [:flipped-cards 1] [x y])
  (check-for-pair))

(defn set-flipped-card! [x y]
  (if (card-flipped? 0)
    (second-card-flipped x y)
    (swap! app-state assoc-in [:flipped-cards 0] [x y])))

(defn both-cards-flipped? []
  (and (= (card-flipped? 0)
          (card-flipped? 1)
          true)))

(defn set-flipped-cards [x y]
  (if (not (both-cards-flipped?))
    (set-flipped-card! x y)))

(defn flipped-card? [x y]
  (or (= [x y] (flipped-card 0))
      (= [x y] (flipped-card 1))))

(defn card-icon [x y]
  (get-in @app-state [:icons (card-rank x y)] ))

(defn card-exists? [x y]
  (not (nil? (card-rank x y))))

(defn card [x y]
  (let [card-side (/ init/container-width (cards-per-row))]
    [:div {:class "card"
           :style {:width (str card-side "px")
                   :height (str card-side "px")}}
    [:div {:class (if (flipped-card? x y)
                     "card__sides card__sides--flipped"
                     "card__sides")
           :on-click
           (fn card-click [e]
             (if (and (card-exists? x y)
                      (not (flipped-card? x y)))
               (set-flipped-cards x y)))
           :style (if (card-exists? x y)
                    {:cursor "pointer"})}
     [:div {:class "card__side card__back"
               :style (if (not (card-exists? x y))
                        {:background "#ecf0f1"})}]
     [:div {:class "card__side card__front"}
      (if (flipped-card? x y)
        [:i {:class (str "fa " (card-icon x y))
             :style {:font-size (str (* card-side 0.4) "px")}}])]]]))

(defn set-cards-per-row! [new-per-row]
  (swap! app-state assoc :cards-per-row new-per-row))

(defn set-cards-per-column! [new-per-column]
  (swap! app-state assoc :cards-per-column new-per-column))

(defn set-board-dimensions [new-dimensions]
  (let [[new-per-row new-per-column] (str/split new-dimensions "x")]
    (set-cards-per-row! (int new-per-row))
    (set-cards-per-column! (int new-per-column))))

(defn reset-game []
  (reset-flipped-cards!)
  (reset-board!)
  (reset-points!)
  (reset-icons!))

(defn points-positive? []
  (> 0 (:points @app-state)))

(defn victory-view []
  [:div {:class "victory"}
   (if (points-positive?)
     [:i {:class "victory__icon fa fa-thumbs-down"}]
     [:i {:class "victory__icon fa fa-thumbs-up"}])
   [:h2 "All pairs found!"]
   [:p "Points: "
    [:span {:class "victory__points"} (:points @app-state)]]
   [:p
    [:button {:class "victory__new-game"
              :on-click
              (fn new-game-click [e]
                (reset-game))}
     "New game"]]])

(defn board-view []
  (into
    [:div {:class "board"}]
    (for [x (range (cards-per-row))
          y (range (cards-per-column))]
      (card x y))))

(defn difficulty-dropdown []
  [:form {:class "difficulty-dropdown"}
   [:label "Difficulty: "]
   [:select {:on-change
             (fn difficulty-change [x]
               (let [selected-dimensions (.. x -target -value)]
                 (set-board-dimensions selected-dimensions)
                 (reset-game)))}
    [:option {:value "2x2"} "Drunk"]
    [:option {:value "3x2"} "Supa easy"]
    [:option {:value "4x3"} "Easy"]
    [:option {:value "4x4" :selected true} "Medium"]
    [:option {:value "6x5"} "Hard"]
    [:option {:value "8x7"} "Nightmare"]
    [:option {:value "10x10"} "Hell"]]])

(defn find-the-pair []
  [:div {:class "container"
         :style {:max-width init/container-width}}
   [:h1 "Find the pair!"]
   (difficulty-dropdown)
   (if (game-won?)
     (victory-view)
     (board-view))])

(reagent/render-component [find-the-pair]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  )
