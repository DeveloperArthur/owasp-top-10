(ns clj_owasp.top2-broken_authenticator
  (:require [crypto.password.bcrypt :as password]))

(def database (atom {}))

(defn add [table document]
  (swap! database update-in [table] conj document))

(defn register-new-user! [username password]
  (add :users {:username username, :password password}))

;aqui o problema eh que a senha esta armazenada em texto puro
;tem que criptografar!
(println (register-new-user! "arthur.tatay" "banana"))


;solucao: podemos utilizar uma lib do clojure para criptografia
;criar uma funcao, e sempre criptografar a senha antes de salvar no banco
(defn encrypted-password [password]
  (password/encrypt password))

(defn register-new-user! [username password]
  (add :users {:username username, :password (encrypted-password password)}))

(println (register-new-user! "arthur.tatay" "banana"))


;validando a criptografia
(println (password/check "banana" "$2a$11$iUGuc0TEJG/RaGadwnxle.zo30SXBph1CIhJJ0p.g99ufiBHaWYo6"))