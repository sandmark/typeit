(defproject typeit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [duct/core "0.7.0"]
                 [duct/module.logging "0.4.0"]
                 [com.google.auth/google-auth-library-oauth2-http "0.18.0"]
                 [com.google.cloud/google-cloud-speech "1.21.0"]
                 [com.google.cloud/google-cloud-storage "1.99.0"]]

  :plugins [[duct/lein-duct "0.12.1"]]

  :main ^:skip-aot typeit.main

  :resource-paths ["resources" "target" "target/resources"]
  :clean-targets ^{:protect false} ["target/public"]

  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:dev          [:project/dev :profiles/dev]
   :repl         {:prep-tasks   ^:replace ["javac" "compile"]
                  :repl-options {:init-ns user}}
   :uberjar      {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.3.1"]
                                   [eftest "0.5.9"]
                                   [com.bhauman/figwheel-main "0.2.3"]
                                   [com.bhauman/rebel-readline-cljs "0.1.4"]]}}

  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]})
