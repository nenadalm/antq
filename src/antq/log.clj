(ns antq.log
  (:require
   [clojure.core.async :as async]))

(defonce logger-ch nil)

(def ^:dynamic *verbose* false)

(defn stop-async-logger!
  [logger-end-ch]
  (when logger-ch
    (async/>!! logger-ch ::eol)
    (async/<!! logger-end-ch)
    (async/close! logger-ch)
    (async/close! logger-end-ch)
    (alter-var-root #'logger-ch (fn [_] nil))))

(defn start-async-logger!
  []
  (let [end-ch (async/chan)]
    (alter-var-root #'logger-ch (fn [_] (async/chan)))
    (async/go-loop []
      (let [v (async/<! logger-ch)]
        (when (string? v)
          (print v)
          (flush))
        (if (= ::eol v)
          (async/>! end-ch ::end)
          (recur))))
    end-ch))

(defn info
  [s]
  (println s))

(defn warning
  [s]
  (when *verbose*
    (binding [*out* *err*]
      (println s))))

(defn error
  [s]
  (binding [*out* *err*]
    (println s)))

(defn async-print
  [s]
  (async/>!! logger-ch s))
