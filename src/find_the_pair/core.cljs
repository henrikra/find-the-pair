(ns find-the-pair.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [find-the-pair.init :as init]))

(enable-console-print!)

(defonce app-state (atom init/app-state))

;; ===== UI =====

(defn reset-icons! []
  (swap! app-state assoc :icons (init/icons)))

(defn game-points []
  (:points @app-state))

(defn reduce-points! []
  (swap! app-state assoc :points
         (- (game-points) init/points-decrease)))

(defn reset-flipped-cards! []
  (swap! app-state assoc :flipped-cards init/flipped-cards))

(defn reset-show-increase! []
  (swap! app-state assoc :show-increase false))

(defn reset-show-decrease! []
  (swap! app-state assoc :show-decrease false))

(defn cards-per-row []
  (:cards-per-row @app-state))

(defn cards-per-column []
  (:cards-per-column @app-state))

(defn reset-board! []
  (swap! app-state assoc :board
         (init/board (cards-per-row) (cards-per-column))))

(defn reset-points! []
  (swap! app-state assoc :points init/points))

(defn flipped-card [index]
  (get-in @app-state [:flipped-cards index]))

(defn card-flipped? [index]
  (not (every? nil? (flipped-card index))))

(defn remove-card! [[x y]]
  (swap! app-state assoc-in [:board y x] nil))

(defn add-points! []
  (swap! app-state assoc :points
         (+ (game-points) init/points-increase)))

(defn game-won? []
  (every? nil? (flatten (get @app-state :board))))

(defn show-increase []
  (:show-increase @app-state))

(defn show-decrease []
  (:show-decrease @app-state))

(defn set-increase [new-value]
  (swap! app-state assoc :show-increase new-value))

(defn set-decrease [new-value]
  (swap! app-state assoc :show-decrease new-value))

(defn success []
  (add-points!)
  (remove-card! (flipped-card 0))
  (remove-card! (flipped-card 1))
  (reset-flipped-cards!)
  (set-increase true))

(defn card-rank
  ([x y] (get-in @app-state [:board y x]))
  ([[x y]] (get-in @app-state [:board y x])))

(defn found-pair? []
  (= (card-rank (flipped-card 0))
     (card-rank (flipped-card 1))))

(defn mistake []
  (reduce-points!)
  (set-decrease true)
  (reset-flipped-cards!))

(defn check-for-pair []
  (js/setTimeout #(if (found-pair?)
                    (success)
                    (mistake)) init/cards-visible-time))

(defn second-card-flipped! [x y]
  (swap! app-state assoc-in [:flipped-cards 1] [x y])
  (check-for-pair))

(defn first-card-flipped! [x y]
  (set-increase false)
  (set-decrease false)
  (swap! app-state assoc-in [:flipped-cards 0] [x y]))

(defn set-flipped-card [x y]
  (if (card-flipped? 0)
    (second-card-flipped! x y)
    (first-card-flipped! x y)))

(defn both-cards-flipped? []
  (and (= (card-flipped? 0)
          (card-flipped? 1)
          true)))

(defn flipped-card? [x y]
  (or (= [x y] (flipped-card 0))
      (= [x y] (flipped-card 1))))

(defn card-icon [x y]
  (get-in @app-state [:icons (card-rank x y)] ))

(defn card-exists? [x y]
  (not (nil? (card-rank x y))))

(defn card [x y]
  (let [card-side (/ init/container-width (cards-per-row))]
    [:div.card {:style {:width (str card-side "px")
                   :height (str card-side "px")}}
    [:div {:class (if (flipped-card? x y)
                    "card__sides card__sides--flipped"
                    "card__sides")
           :on-click
           (fn card-click [e]
             (if (and (card-exists? x y)
                      (not (flipped-card? x y))
                      (not (both-cards-flipped?)))
               (set-flipped-card x y)))
           :style (if (card-exists? x y)
                    {:cursor "pointer"})}
     [:div.card__side.card__back {:style (if (not (card-exists? x y))
                     {:background "#ecf0f1"})}]
     [:div.card__side.card__front
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
  (reset-icons!)
  (reset-show-increase!))

(defn victory-view []
  [:div.victory
   [:i {:class (if (pos? (game-points))
                 "victory__icon fa fa-thumbs-up"
                 "victory__icon fa fa-thumbs-down")}]
   [:h2 "All pairs found!"]
   [:p "Points: "
    [:span.victory__points (game-points)]]
   [:p
    [:button.victory__new-game {:on-click
              (fn new-game-click [e]
                (reset-game))}
     "New game"]]])

(defn board-view []
  [:div
   (into
    [:div.board]
    (for [x (range (cards-per-row))
          y (range (cards-per-column))]
      (card x y)))
   [:p "Points: "
    [:span.victory__points (game-points)]]
   [:div.points
    [:p.increase {:style {:animation (if (show-increase)
                              "fadeOutUp 0.7s forwards")}} (str "+" init/points-increase)]
    [:p.decrease {:style {:animation (if (show-decrease)
                              "fadeOutUp 0.7s forwards")}} (str "-" init/points-decrease)]]])

(defn difficulty-dropdown []
  [:form.difficulty-dropdown
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
  [:div.container {:style {:max-width init/container-width}}
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
