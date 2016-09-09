(ns modern-art.mock
  (:require [quil.core :as q]
            [modern-art.slider :as slider]))

(defn init []
  {:hue-slider (slider/init 10 10 100 20 0.5)
   :sat-slider (slider/init 10 30 100 20 0.5)
   :bri-slider (slider/init 10 50 100 20 0.5) })

(defn get-value [state key]
  (slider/value (get state key)))

(defn check-click [state event]
  (reduce (fn [r [k v]] (assoc r k (slider/check-click v event))) {} state))

(defn render [state]
  (doseq [[_ widget] state] (slider/render widget)))
