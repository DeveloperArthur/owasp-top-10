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

;------------------------------------------------------------
;solucao: podemos utilizar uma lib do clojure para criptografia
;criar uma funcao, e sempre criptografar a senha antes de salvar no banco
(defn encrypted-password [password]
  (password/encrypt password))

(defn register-new-user! [username password]
  (add :users {:username username, :password (encrypted-password password)}))

(println (register-new-user! "wellingthon.almeida" "banana"))


;validando a criptografia
(println (password/check "banana" "$2a$11$iUGuc0TEJG/RaGadwnxle.zo30SXBph1CIhJJ0p.g99ufiBHaWYo6"))

;------------------------------------------------------------
;um outro problema é que estamos permitindo senhas faceis
(println (register-new-user! "raul.ferreira" "senha"))

;o ideal eh ter uma base de senhas comuns, baixar essa base(esta no src)
;e implementar um check pra cadastrar apenas senhas fortes
(defn read-file [filename]
  (-> filename
      slurp
      clojure.string/split-lines))

(def commom-passwords (read-file "resources/commom-passwords.txt"))
(println commom-passwords)

(defn is-commom? [password]
  ;percorre todos os commom-passwords, retorna true ou nil
  (some #(= password %) commom-passwords))

(defn register-new-user! [username password]
  (if (is-commom? password)
    (throw (Exception. "Senha muito fraca"))
    (add :users {:username username, :password password})))

(println (register-new-user! "gabriela.tatay" "gzxifgiuyewfvw"))
(println (register-new-user! "costelinha.tatay" "123456"))