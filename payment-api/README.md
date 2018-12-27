# Payment API

## Preparar o ambiente de desenvolvimento

Todos os passos devem ser executados na pasta do projeto

### Subir o banco de dados

```
docker run -it --rm=true -e "POSTGRES_PASSWORD=teste001" -p 5432:5432 -v $(pwd)/extras/database/create-db.sh:/docker-entrypoint-initdb.d/1-create-db.sh --name payment_api_db postgres:11
```