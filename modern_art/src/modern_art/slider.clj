(ns modern-art.slider
  (:require [quil.core :as q]))

(defn init [x y width height value]
  [x y width height value])

(defn check-click [[sx sy width height value] {x :x y :y}]
  (let [interpolated (/ (- x sx) width)]
    (if (and (>= interpolated 0) (<= interpolated 1) (>= y sy) (<= y (+ sy height)))
      [sx sy width height interpolated]
      [sx sy width height value])))

(defn value [state]
  (last state))

(defn render [[x y width height value]]
  (q/stroke 0)
  (q/fill 0)
  (q/rect x y width height)
  (q/fill 255)
  (q/rect (inc x) (inc y) (* (- width 2) value) (- height 2)))
