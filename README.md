# Payment-exercise

## Preparar o ambiente de desenvolvimento

Acessar a pasta raiz do repositório (payment-exercise/) e executar o comando abaixo para preparar e subir o ambiente

```
docker-compose up
```

## Acessar o container para executar os testes automatizador

Para executação dos testes é necessário ter o projeto rodando pois os mesmos são realizando usando o banco de dados do container do docker

```
docker-compose exec payment-checkout bash
mvn test -f payment-api/
mvn test -f payment-checkout/
```

## Debug remoto

**Debug da aplicação:** Durante a execução da aplicação, está disponível a porta *8000* para debug

**Debug dos testes automatizados:** pode-se se usar o comando abaixo para execução dos testes, o mesmo ficará aguardando até haver uma conexão na porta *5005*
```
mvn -Dmaven.surefire.debug test
```

## Configurações

### Aplicação

- **Localização:** *payment-checkout/src/main/resources/application.properties*
- **Descrição:** usado na execução da aplicação com dados para usos "gerais"

- **Localização:** *payment-exercise/payment-checkout/src/test/resources/application.properties*
- **Descrição:** usado na execução dos testes automatizados

### Banco de dados

- **Localização:** *payment-checkout/src/main/resources/database.properties*
- **Descrição:** usado na execução da aplicação para configurar o datasource de conexões

- **Localização:** *payment-checkout/src/test/resources/database.properties*
- **Descrição:** usado na execução dos testes automatizados

### Logs

- **Localização:** *payment-checkout/src/main/resources/logback.xml*
- **Descrição:** usado na configuração de logs na execução da aplicação

- **Localização:** *payment-checkout/src/test/resources/logback.xml*
- **Descrição:** usado na configuração de logs nos testes automatizados

## Testes

Os testes automatizados usam o banco de dados do container docker para execução, quando subimos o container são criados dois databases, um
para atender a aplicação e outro para os testes.

O projeto **payment-api** sempre limpa os dados da base de dados antes de executar os testes automatizados, já que a mesma é responsável
pelo acesso direto a base dados.

O projeto **payment-checkout** somente consome os serviços do **payment-api**, então a mesma sempre gera um conteúdo diferente para cada
execução dos testes pois não tem acesso direto ao a base de dados.

Quando subimos os containers docker a base de dados de testes é sempre limpa.

## Logs

É possível visualizar o log da execução da aplicação no terminal onde foi executado o comando **docker-compose up** e também na pasta abaixo dentro do container

```
tail -F /var/log/payment-checkout/payment-checkout-2019-01-03.log
```

## Mais informações

Na pasta *extras/postman* há um arquivo com recursos disponíveis para uso através do Postman

