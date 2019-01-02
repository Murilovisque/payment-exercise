#!/bin/bash

set -e

for IDBS in "dev_payment" "dev_payment_test"; do

CREATE_DB_SCHEMA=$(cat <<EOF
    CREATE DATABASE $IDBS
        WITH OWNER = postgres
            ENCODING = 'UTF8'
            TABLESPACE = pg_default
            LC_COLLATE = 'en_US.utf8'
            LC_CTYPE = 'en_US.utf8'
            CONNECTION LIMIT = -1;

    \connect $IDBS;

    CREATE TABLE buyer (
        id_buyer UUID PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        email VARCHAR(64) NOT NULL,
        cpf VARCHAR(11) UNIQUE NOT NULL
    );    

    CREATE TABLE api_client (
        id_api_client SERIAL PRIMARY KEY,
        name VARCHAR(100)
    );

    CREATE TYPE PAYMENT_TYPE AS ENUM('BOLETO', 'CREDIT_CARD');

    CREATE TABLE form_of_payment (
        id_form_of_payment UUID PRIMARY KEY,
        type PAYMENT_TYPE NOT NULL,
        data JSONB NOT NULL
    );
    
    CREATE TYPE PAYMENT_STATUS AS ENUM('GENERATED', 'PAID');   

    CREATE TABLE payment (
        id_payment UUID PRIMARY KEY,
        amount float(2),       
        status PAYMENT_STATUS NOT NULL,
        id_form_of_payment UUID NOT NULL,        
        id_buyer UUID NOT NULL,
        id_api_client INTEGER NOT NULL,
        FOREIGN KEY (id_form_of_payment) REFERENCES form_of_payment(id_form_of_payment),
        FOREIGN KEY (id_api_client) REFERENCES api_client(id_api_client),
        FOREIGN KEY (id_buyer) REFERENCES buyer(id_buyer)
    );

    \disconnect;
EOF
)

RUN_PSQL="psql -v ON_ERROR_STOP=1 --username $POSTGRES_USER --dbname $POSTGRES_DB"
$RUN_PSQL <<SQL;
    $CREATE_DB_SCHEMA
SQL

done;
