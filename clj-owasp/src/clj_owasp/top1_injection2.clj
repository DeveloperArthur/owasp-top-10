(ns clj-owasp.top1-injection2)

(defn login [username password]
  (let [sql (str "select * from Users where username = '" username "' and password = '" password "'")]
    ;executaria o sql: select * from Users where username = 'arthur' and password = 'senha'
    (println sql)))

(login "arthur" "senha")

;O problema aqui eh se o atacante fizesse isso
(login "' or admin=true#" "qualquercoisa")
;sql executado:
;select * from Users where username = '' or admin=true#' and password = 'qualquercoisa'
;e conseguirá entrar no site pois a query retornaria um usuário admin

;A solucao seria utilizar uma lib que faz a sanitizacao dos dados