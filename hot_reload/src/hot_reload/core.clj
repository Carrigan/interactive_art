(ns hot-reload.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [hot-reload.dynamic :as dynamic]))

(q/defsketch hot-reload
  :title "You spin my circle right round"
  :size [500 500]
  :setup dynamic/setup
  :update dynamic/update-state
  :draw dynamic/draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
