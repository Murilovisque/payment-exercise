package com.payment.api.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.payment.api.AbstractAPI;

public class ConnectionWrapper {

    private ConnectionProvider connectionProvider;
    private Connection connection;
    private Type type;
    private List<Statement> stmts = new ArrayList<>();
    private AbstractAPI apiStartedTransation;

    public ConnectionWrapper(ConnectionProvider connectionProvider, Type type) {
        this.connectionProvider = connectionProvider;
        this.type = type;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null)
            connection = connectionProvider.getConnection();                    
        if (getType().equals(ConnectionWrapper.Type.TRANSACTIONAL))
            connection.setAutoCommit(false);
        return connection;
    }

    public Type getType() {
        return type;
    }

	public void setType(Type type) {
        this.type = type;
	}

    public void addBatchStatement(Statement stmt) {
        stmts.add(stmt);
    }

    public void executeBatchStatements() throws SQLException {
        for (Statement stmt : stmts) {
            stmt.executeBatch();
            stmt.close();
        }            
    }
    
    public void setApiStartedTransation(AbstractAPI apiStartedTransation) {
        if (this.apiStartedTransation == null)
            this.apiStartedTransation = apiStartedTransation;
    }

    public boolean isAPIStartedTransaction(AbstractAPI api) {
        return apiStartedTransation.equals(api);
    }

    public enum Type {
        NORMAL,
        TRANSACTIONAL
    }
}