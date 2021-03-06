(ns modern-art.core
  (:require [quil.middleware :as m]
            [modern-art.dynamic :as dynamic]
            [quil.core :as q]))

(q/defsketch modern-art
  :title "Synthestesia"
  :size [800 800]
  :setup dynamic/setup
  :update dynamic/update-state
  :draw dynamic/draw-state
  :mouse-pressed dynamic/mouse-pressed
  :features [:keep-on-top]
  :middleware [m/fun-mode])
