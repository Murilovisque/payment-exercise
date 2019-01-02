# payment-exercise

## Preparar o ambiente de desenvolvimento

Todos os passos devem ser executados na pasta do projeto

### Subir o banco de dados

Rodar a aplicação via docker-compose

```
docker-compose up
docker-compose exec payment-checkout bash
docker-compose down
```


"vmArgs": "-Dlogback.configurationFile=${workspaceFolder}/src/main/resouces/logback.xml"


docker build -t payment-exercise extras/docker/

docker volume create --name mavendata


docker run -it --rm=true -v $(pwd):/payment-exercise -v mavendata:/maven-data/.m2/repository --net=host --name payment-exercise payment-exercises