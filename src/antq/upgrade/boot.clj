(ns antq.upgrade.boot
  (:require
   [antq.upgrade :as upgrade]
   [antq.util.dep :as u.dep]
   [antq.util.zip :as u.zip]
   [rewrite-clj.zip :as z]))

(defn- in-dependencies?
  [loc]
  (loop [loc (-> loc z/up z/up)]
    (case (z/tag loc)
      :meta (recur (z/up loc))
      :vector (= :dependencies (-> loc z/up z/left z/sexpr))
      false)))

(defn upgrade-dep
  [loc version-checked-dep]
  (let [name-set (u.dep/name-candidates (:name version-checked-dep))]
    (loop [loc loc]
      (if-let [loc (z/find-value loc z/next name-set)]
        (recur (if (in-dependencies? loc)
                 (-> loc z/right (z/replace (:latest-version version-checked-dep)))
                 (z/next loc)))
        (u.zip/move-to-root loc)))))

(defmethod upgrade/upgrader :boot
  [version-checked-dep]
  (-> (z/of-file (:file version-checked-dep))
      (upgrade-dep version-checked-dep)
      (z/root-string)))
