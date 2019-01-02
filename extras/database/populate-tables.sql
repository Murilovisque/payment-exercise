\connect dev_payment_test;

INSERT INTO api_client(name) VALUES ('tester');

\disconnect;

\connect dev_payment;

INSERT INTO api_client(name) VALUES ('tester');

\disconnect;
