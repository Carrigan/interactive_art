(ns modern-art.button
  (:require [quil.core :as q]))

(defn init [x y width height]
  [x y width height false])

(defn check-click [[sx sy width height _] {x :x y :y}]
  [sx sy width height (and (>= x sx) (<= x (+ sx width)) (>= y sy) (<= y (+ sy height)))])

(defn clear [[sx sy width height _]]
  [sx sy width height false])

(defn value [state]
  (last state))

(defn render [[x y width height clicked?]]
  (q/stroke 0)
  (q/fill 0)
  (q/rect x y width height)
  (q/fill 255)
  (if clicked? (q/rect (inc x) (inc y) (- width 2) (- height 2))))
