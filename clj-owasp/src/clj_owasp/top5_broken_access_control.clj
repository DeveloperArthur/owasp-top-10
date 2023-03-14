(ns clj_owasp.top5_broken_access_control)

(defn get-lyric [lyric-name]
  (->> lyric-name
      (str "resources/")
       slurp))

;isso executa normalmente
(println (get-lyric "proverbiox-fe"))
;o problema é se o usuário fizer isto:
(println (get-lyric "../src/clj_owasp/core.clj"))

;o controle de acesso está quebrado, o usuario
;consegue navegar pela estrutura de arquivos
;e ver arquivos que nao deveria

;solucao: nesse caso podemos criar um mapa em memoria
;contendo todas as musicas, o usuario ira passar
;o nome da musica, q no caso é a key
;e a gnt esta basicamente passando a key, pegando o valor
;e passando esse valor para o slurp, q vai puxar o conteudo do arquivo
;a implementacao ficaria assim:

(def lyrics { :proverbiox-fe "resources/proverbiox-fe"})

(defn get-lyric [lyric-name]
  (->> lyric-name
        keyword
       (get lyrics)
       slurp))

;esse funciona
(println (get-lyric "proverbiox-fe"))
;esse nao funciona
(println (get-lyric "../src/clj_owasp/core.clj"))