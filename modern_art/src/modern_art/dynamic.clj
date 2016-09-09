(ns modern-art.dynamic
  (:require [quil.core :as q]))

(def size 50)

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  {})

(defn update-state [state] {})

(defn rotate-hue [hue]
  (let [new-hue (+ 50 hue)
        new-hue (if (> new-hue 255) (- new-hue 255) new-hue)]
    new-hue))

(defn dim [lightness]
  (max (- lightness 20) 0))

(defn render-frame [color]
  (q/fill 0)
  (q/stroke-weight 3)
  (q/line 0 size 500 size)
  (q/line 0 (* size 2) 500 (* size 2))
  (q/line size 0 size 500)
  (q/line (* size 2) 0 (* size 2) 500)
  (apply q/fill color)
  (q/rect size size size size)

  (let [[h s l]   color
        new-color [(rotate-hue h) s (dim l)]
        start     (* size 2)]
    (apply q/fill new-color)
    (q/rect start size (- 500 start) size)))

(defn draw-state [state]
  (q/background 240)
  (render-frame [90 200 200]))
