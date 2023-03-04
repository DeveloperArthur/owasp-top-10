(ns clj-owasp.injection)

;Aqui temos uma função que roda um cluster de kafka
;Entao, vc passa pra funcao qual eh o arquivo de confg
;E ele vai levantar esse cluster
(use '[clojure.java.shell :only [sh]])

(defn run-cluster [config-file]
    (let [command (str "/bin/kafka " config-file)]
        (sh "bash" "-c" command)))
        ;O sh executaria isso: bash -c/bin/kafka server.properties

(run-cluster "server.properties")


;Mas se passarmos a string abaixo para a funcao
(run-cluster "server.properties; ls /")
;No terminal seria executado :bash -c/bin/kafka server.properties; ls /
;Ou seja, permitindo injection


;O problema aqui eh o bash -c, pois ele suporta varios comandos com o ';'
;A solucao para este ataque em especifico (pode estar vulneravel a outros ataques)
;Seria nao utilizar o bash -c, utilizar somente o sh, pois o sh vai executar somente um programa
;E ignorar o resto
;Funcao corrigida:
(defn run-cluster [config-file]
    (sh "/bin/kafka" config-file))

