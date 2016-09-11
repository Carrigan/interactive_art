(ns modern-art.arduino
  (:require [serial-port :as ser]))

(defn on-receive-gen
  [received-atom]
  (fn [character]
    (swap! received-atom conj character)))

(defn initialize
  [port-name]
  (let [serial        (ser/open port-name)
        received-atom (atom [])]
    (ser/on-byte serial (on-receive-gen received-atom))
    (ser/write-int serial (int 0))
    { :received-atom received-atom
      :port serial
      :state { :hue 0 :sat 0 :bri 0 :next-button false }
    }))

(defn build-state
  [[hue sat bri buttons _]]
  { :hue hue
    :sat sat
    :bri bri
    :next-button false
  })

(defn update-state
  [state]
  (let [received-atom (:received-atom state)
        message       @received-atom]
    (if (>= (count message) 5)
      (do
        (println (build-state message))
        (reset! received-atom [])
        (assoc state :state (build-state message)))
      state)))
