(ns modern-art.arduino
  (:require [serial-port :as ser]
            [modern-art.mock :as mock]))

(defn on-receive-gen
  [received-atom]
  (fn [character]
    (if (= character 255)
      (reset! received-atom [])
      (swap! received-atom conj character))))

(defn connect [port-name]
  (try
    (ser/open port-name)
    (catch Exception _ nil)))

(defn initialize-port [serial]
  (let [received-atom (atom [])]
    (ser/on-byte serial (on-receive-gen received-atom))
    (ser/write-int serial (int 0))
    { :is-mock false
      :received-atom received-atom
      :port serial
      :state { :hue 0 :sat 0 :bri 0 :next-button false }
    }))

(defn initialize-mock []
  { :is-mock true
    :mock (mock/init)
    :state { :hue 0 :sat 0 :bri 0 :next-button false }})

(defn initialize
  [port-name]
  (let [port (connect port-name)]
    (if port (initialize-port port) (initialize-mock))))

(defn build-state
  [[hue sat bri buttons _]]
  { :hue hue
    :sat sat
    :bri bri
    :next-button false
  })

(defn update-serial
  [state]
  (let [received-atom (:received-atom state)
        message       @received-atom]
    (if (>= (count message) 5)
      (do
        (reset! received-atom [])
        (assoc state :state (build-state message)))
      state)))

(defn mouse-pressed [state event]
  (let [mock-state (:mock state)]
    (assoc state :mock (mock/check-click mock-state event))))

(defn slider-val [state key]
  (int (* 255 (mock/get-value state key))))

(defn update-mock
  [state]
  (let [mock-state (:mock state)
        hue (slider-val mock-state :hue-slider)
        sat (slider-val mock-state :sat-slider)
        bri (slider-val mock-state :bri-slider)
        next-button (mock/clicked? mock-state :next-btn)]
    (-> state
        (assoc :state { :hue hue :sat sat :bri bri :next-button next-button})
        (update :mock mock/clear-clicks))))

(defn update-state
  [state]
  (if (:is-mock state)
    (update-mock state)
    (update-serial state)))

(defn render
  [state]
  (when (:is-mock state)
    (mock/render (:mock state))))
