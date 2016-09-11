(ns modern-art.dynamic
  (:require [quil.core :as q]
            [modern-art.lines-and-boxes :as frame]
            [modern-art.mock :as mock]
            [modern-art.arduino :as arduino]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  {:mock (mock/init)
   :frames '()
   :arduino (arduino/initialize "COM3")})

(defn slider-val [state key]
  (int (* 255 (mock/get-value (:mock state) key))))

(defn frame-from-mock [state]
  [(slider-val state :hue-slider) (slider-val state :sat-slider) (slider-val state :bri-slider)])

(defn frame-from-arduino [state]
  [(get-in state [:arduino :state :hue]) (get-in state [:arduino :state :sat]) (get-in state [:arduino :state :bri])])

(defn recurse-if-clicked [state clicked?]
  (if clicked?
    (let [cframe (frame-from-arduino state)
          frames (concat (:frames state) [cframe])]
      (assoc state :frames frames))
    state))

(defn update-state [state]
  (-> state
    (recurse-if-clicked (mock/clicked? (:mock state) :next-btn))
    (assoc :mock (mock/clear-clicks (:mock state)))
    (update-in [:arduino] arduino/update-state)))

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
  (let [cframe (frame-from-arduino state)
        frames (concat (:frames state) [cframe])]
    (q/background 240)
    (recurse-frame frames [0 0] [(q/width) (q/height)])
    (mock/render (:mock state))))
