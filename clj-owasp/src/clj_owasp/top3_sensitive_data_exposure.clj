(ns clj_owasp.top3_sensitive_data_exposure)

;base de invocação recursiva
(defn continue [chain path parameters]
  (if chain
    (let [next-one (first chain)]
      (next-one (rest chain) path parameters))))

(defn log-layer [chain path parameters]
  (println path parameters) ; <------ PROBLEMA
  (continue chain path parameters))

(defn execution-layer [chain path parameters]
  (println "executing for path " path))

(defn do-upload [parameters]
  (println "uploading..."))

(defn upload-layer [chain path parameters]
  (if (:upload-file parameters)
    (do-upload parameters))
  (continue chain path parameters))

;estamos simulando o funcionamento de um framework web
(defn service [path parameters]
  (let [chain [log-layer upload-layer execution-layer]]
    (continue chain path parameters)))

(service "/upload" {:upload-file "hi.txt"})
(service "/login" {:password "123"})

;o problema esta na log-layer, pois estamos expondo dados sensíveis
;solucao eh nunca logar dados sensiveis

;segundo a LGPD dado sensivel é: Dado pessoal sobre origem racial ou
;étnica, convicção religiosa, opinião política, filiação a sindicato
;ou a organização de caráter religioso, filosófico ou político,
;dado referente à saúde ou à vida sexual, dado genético ou biométrico.