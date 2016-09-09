(ns modern-art.dynamic
  (:require [quil.core :as q]
            [modern-art.lines-and-boxes :as frame]))

(defn setup []
  (q/frame-rate 1)
  (q/color-mode :hsb)
  {:frames '([90 200 255]
             [100 100 100]
             [200 30 230]
             [0 255 100]
             [140 50 150]
             [200 90 90])})

(defn update-state [state] state)

(defn recurse-frame [colors [cx cy] [x y]]
  (when-not (empty? colors)
    (let [size-x (- (q/width) cx)
          size-y (- (q/height) cy)]
        (q/push-matrix)
        (q/translate cx cy)
        (q/scale (/ size-x x) (/ size-y y))
        (let [next-location (-> colors (first) (frame/render))]
          (recurse-frame (rest colors) next-location [x y])
          (q/pop-matrix)))))

(defn draw-state [state]
  (q/background 240)
  (recurse-frame (:frames state) [0 0] [(q/width) (q/height)]))
