package com.payment.api.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

abstract class AbstractRepository {

    private ConnectionWrapper connectionWrapper;

    public AbstractRepository(ConnectionWrapper connectionWrapper) {
        this.connectionWrapper = connectionWrapper;
    }


    protected Connection getConnection() throws SQLException {
        return connectionWrapper.getConnection();
    }

    protected void addBatchStatement(Statement stmt) {
        connectionWrapper.addBatchStatement(stmt);
    }
}