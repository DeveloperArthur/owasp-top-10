(ns clj-owasp.owasp1)

(use '[clojure.java.shell :only [sh]])

(defn run-cluster [config-file]
    (let [command (str "/bin/kafka " config-file)]
        (println command)
        ;(sh "bash" "-c" command)
        ))

(run-cluster "server.properties")