# kafka-ecommerce
kafka-ecommerce

## Projeto de Mensageria Kafka com Spring Boot Java, usando docker para os servidores Zookeper e Kafka. Também tem um banco de dados SQLite e o Servidor Jetty para Servlet.

##É um projeto simple de mensageria com Kafka rodando em Cluster (ou seja, pode desligar um dos servidores que o outro assume a mensageria)

##Abaixo segue o link do arquivo do docker-comsose para você subir após ter instalado e configurado o docker em seu computador. O Arquivo pode ser colocado em qualquer lugar do seu computador e para você rodar deve executar o seguinte comando:

### `docker-compose -f c:\pasta-do-arquivo\docker-compose-cluster.yml up`

##Assim que você executar o docker irá baixar as imagens e irá subir um servior do Zookeper e dois servidores do Kafka no seu docker

##Para você poder ver as mensagens, tópicos e configuração do kafka você pode baixar o aplicativo kafka tools no link abaixo:

[https://www.kafkatool.com/download.html]

##Após instalar e iniciar os servires Zookeper e Kafka você deve configurar os servidores kafka e zookeper no aplicativo da seguinte forma:

##Clique em File>Add New Connection

Na aba properties
Em Cluster name coloque o nome que quiser
EmKafka Cluster Version selecione 3.2
Em Zookeperhost coloque localhost
Em Zookeper port coloque 2181
Em chroot path coloque /

Vá até a aba advanced
Em Boostrap servers
Coloque localhost:9092,localhost9093
Clique em Add

##O Banco de dados é o SQLite que está no caminho src\bd\users_database.sqlite, é um banco de dados leve para desenvolvimento e testes. O tipo de conexão utilizada é a JDBC.  

Para rodar os consumers você executar os métodos main dos 3 arquivos abaixo:
- /log/logService
- /fraudDetector/FraudDetectionService
- /email/EmailService

E para rodar o único producer execute o método main do arquivo abaixo:
- http/EcommerceServiceHttp

Após rodar a classe acima, vá no navegador e acesse o seguinte link:

No Path Param inserir o e-mail e o amount que é o valor de compra, a regra usada para praud é simples somente para teste, se o amount for maior do que 4500 o sistema considera uma fraude e dispara a mensageria de fraude. Se o email já está no banco ele não cria um usuário novo, se não estiver ele cria o usuario no banco e sempre disparando as mensagens.

[http://localhost:8080/newOrderServlet?email=bruno.araujo5@gmail.com&amount=3922]

Segue meu LinkedIn: [https://www.linkedin.com/in/bruno-araujo-oficial/]
