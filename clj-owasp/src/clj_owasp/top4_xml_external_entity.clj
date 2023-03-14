(ns clj-owasp.top4_xml_external_entity
  (:require [clojure.xml :as xml]))

(defn parse-document [xml-document]
  (xml/parse xml-document))

(defn get-document [uri]
  (-> uri
      slurp
      (.getBytes "UTF-8")
      java.io.ByteArrayInputStream.
      parse-document))

(println (get-document "src/nasty.xml"))

;------------------------------------------------------------
;explicação da vulnerabilidade:
;https://github.com/DeveloperArthur/owasp-top-10#a4---xml-external-entities-xee

;uma das soluções é utilizar biblioteca de SAX parser para ler o XML
;pois com ele você consegue desativar declaracoes no DOCTYPE
;e teria uma funcao mais ou menos assim:
(defn startparse-sax-no-doctype [s ch]
  (..
    (doto (javax.xml.parsers.SAXParserFactory/newInstance)
      (.setFeature javax.xml.XMLConstants/FEATURE_SECURE_PROCESSING true)
      (.setFeature "http://apache.org/xml/features/disallow-doctype-decl" true))
    (newSAXParser)
    (parse s ch)))

;e a funcao parse-document iria ficar assim:
(defn parse-document [xml-document]
  (xml/parse xml-document startparse-sax-no-doctype))