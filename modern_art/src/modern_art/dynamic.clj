(ns modern-art.dynamic
  (:require [quil.core :as q]
            [modern-art.lines-and-boxes :as frame]
            [modern-art.mock :as mock]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  {:mock (mock/init)
   :frames '()})

(defn slider-val [state key]
  (int (* 255 (mock/get-value (:mock state) key))))

(defn recurse-if-clicked [state clicked?]
  (if clicked?
    (let [cframe [(slider-val state :hue-slider) (slider-val state :sat-slider) (slider-val state :bri-slider)]
          frames (concat (:frames state) [cframe])]
      (assoc state :frames frames))
    state))

(defn update-state [state]
  (-> state
    (recurse-if-clicked (mock/clicked? (:mock state) :next-btn))
    (assoc :mock (mock/clear-clicks (:mock state)))))

(defn mouse-pressed [state event]
  (assoc state :mock (mock/check-click (:mock state) event)))

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
  (let [cframe [(slider-val state :hue-slider) (slider-val state :sat-slider) (slider-val state :bri-slider)]
        frames (concat (:frames state) [cframe])]
    (q/background 240)
    (recurse-frame frames [0 0] [(q/width) (q/height)])
    (mock/render (:mock state))))
