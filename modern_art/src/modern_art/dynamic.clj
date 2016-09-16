(ns modern-art.dynamic
  (:require [quil.core :as q]
            [modern-art.lines-and-boxes :as frame]
            [modern-art.arduino :as arduino]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  {:frames '()
   :arduino (arduino/initialize "COM3")})

(defn frame-from-arduino [state]
  [ (get-in state [:arduino :state :hue])
    (get-in state [:arduino :state :sat])
    (get-in state [:arduino :state :bri])
  ])

(defn recurse-if-clicked [state clicked?]
  (if clicked?
    (let [current-frame (frame-from-arduino state)
          all-frames (concat (:frames state) [current-frame])]
      (assoc state :frames all-frames))
    state))

(defn update-state [state]
  (-> state
    (update :arduino arduino/update-state)
    (recurse-if-clicked (get-in state [:arduino :state :next-button]))))

(defn mouse-pressed [state event]
  (assoc state :arduino (arduino/mouse-pressed (:arduino state) event)))

(defn recurse-frame [colors [cx cy mx my] [x y]]
  (when-not (empty? colors)
    (let [size-x (- mx cx)
          size-y (- my cy)]
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
    (recurse-frame frames [0 0 (q/width) (q/height)] [(q/width) (q/height)])
    (arduino/render (:arduino state))))
