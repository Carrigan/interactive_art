(ns modern-art.lines-and-boxes
  (:require [quil.core :as q]))

(def size 0.10)

(defn clip-8-bit [value]
  (cond (> value 255) (- value 255)
        (< value 0) (+ value 255)
        :else value))

(defn rotate-hue [hue distance]
  (-> hue (+ distance) (clip-8-bit)))

(defn dim [lightness]
  (max (- lightness 20) 0))

(defn render [color]
  (let [size (* (q/width) size)]
    (q/fill 0)
    (q/stroke-weight 3)
    (q/line 0 size (q/width) size)
    (q/line 0 (* size 2) (q/width) (* size 2))
    (q/line size 0 size (q/height))
    (q/line (* size 2) 0 (* size 2) (q/height))
    (apply q/fill color)
    (q/rect size size size size)

    (let [[h s l]   color
          new-color [(rotate-hue h 150) s (dim l)]
          start     (* size 2)]
      (apply q/fill new-color)
      (q/rect start size (- (q/width) start) size)
      (q/rect 0 size size size))

    (let [[h s l]   color
          new-color [(rotate-hue h -150) s (dim l)]
          start     (* size 2)]
      (apply q/fill new-color)
      (q/rect size start size (- (q/height) start))
      (q/rect size 0 size size))

    [(* size 2) (* size 2) (q/width) (q/height)]))
