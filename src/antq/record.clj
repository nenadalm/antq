(ns antq.record)

(def ?repository
  [:map [:url 'string?]])

(def ?dependency
  [:map
   [:type 'keyword?]
   [:file 'string?]
   [:name 'string?]
   [:version 'string?]
   [:latest-version [:maybe 'string?]]
   [:repositories [:maybe [:map-of 'string? ?repository]]]
   [:project 'keyword?]
   [:changes-url [:maybe 'string?]]
   [:latest-name [:maybe 'string?]]
   [:only-newest-version? [:maybe boolean?]]])

(def ?dependencies
  [:sequential ?dependency])

(defrecord Dependency
  [;; Dependency type keyword
   ;; e.g. :java, :git-sha or :github-tag
   type
   ;; File path for project configuration file
   file
   ;; Dependency name
   ;; e.g. "org.clojure/clojure", "medley/medley"
   name
   ;; Current version string
   version
   ;; Latest version string (Nullable)
   latest-version
   ;; Additional Maven repositories (Nullable)
   ;; e.g. {"nexus-snapshots" {:url "http://localhost:8081/repository/maven-snapshots/"}}
   repositories
   ;; Project type keyword
   ;; e.g. :clojure, :leiningen, :shadow-cljs and so on.
   project
   ;; Changes URL for Version Control System (Nullable)
   changes-url
   ;; Latest dependency name (Nullable)
   ;; c.f. https://github.com/clojars/clojars-web/wiki/Verified-Group-Names
   latest-name
   ;; Keep only the newest version in the same file.
   only-newest-version?])
