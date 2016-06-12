(ns find-the-pair.core
  (:require [reagent.core :as reagent]
            [find-the-pair.app :refer [app]]))

(enable-console-print!)

(reagent/render-component [app]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  )
