(ns clj_owasp.top8_insecure_deserialization)

(defn treat-dot-commas [palavra]
  (-> palavra
      (clojure.string/replace "." "")
      (clojure.string/replace "," ".")
      read-string)) ; <- esta funcao converte uma string em um objeto

;a saida desse codigo eh: 120.3
;pois deserializou string em numero
(println (treat-dot-commas "1.20,30"))

;mas se eu executo estes comandos:
(println (treat-dot-commas "#=(+ 2 2)"))
(println (treat-dot-commas "#=(println \"invadindo clojure\")"))

;vemos que há uma vulnerabilidade nessa serializacao
;pois estou nitidamente conseguindo executar codigo clojure

;solução: neste caso, nao utilizar o read/string
;utilizar o clojure.edn/read-string, pois este
;nao suporta o '#=', que possibilita execucao de codigo

(defn treat-dot-commas [palavra]
  (-> palavra
      (clojure.string/replace "." "")
      (clojure.string/replace "," ".")
      clojure.edn/read-string))

(println (treat-dot-commas "#=(+ 2 2)"))
(println (treat-dot-commas "#=(println \"invadindo clojure\")"))