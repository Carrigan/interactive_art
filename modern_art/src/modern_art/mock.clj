(ns modern-art.mock
  (:require [quil.core :as q]
            [modern-art.slider :as slider]
            [modern-art.button :as button]))

; TODO: Implement a contract here
(defn mock-click [[type state] event]
  (cond (= type :slider) [type (slider/check-click state event)]
        (= type :button) [type (button/check-click state event)]))

(defn render-widget [[type state]]
  (cond (= type :slider) (slider/render state)
        (= type :button) (button/render state)))

(defn init []
  {:hue-slider [:slider (slider/init 10 10 100 20 0.5)]
   :sat-slider [:slider (slider/init 10 30 100 20 0.5)]
   :bri-slider [:slider (slider/init 10 50 100 20 0.5)]
   :next-btn   [:button (button/init 10 70 50 20)]})

(defn clicked? [state key]
  (-> state (get key) (last) (button/value)))

(defn get-value [state key]
  (-> state (get key) (last) (slider/value)))

(defn check-click [state event]
  (reduce (fn [r [k v]] (assoc r k (mock-click v event))) {} state))

(defn clear-click [[type state]]
  (if (= type :button) [type (button/clear state)] [type state]))

(defn clear-clicks [state]
  (reduce (fn [r [k v]] (assoc r k (clear-click v))) {} state))

(defn render [state]
  (doseq [[_ widget-state] state] (render-widget widget-state)))
