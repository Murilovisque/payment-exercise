package com.payment.api;

import java.sql.SQLException;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.repositories.ConnectionProvider;
import com.payment.api.repositories.ConnectionWrapper;
import com.payment.api.repositories.ConnectionWrapper.Type;

public class AbstractAPI {

    private APIClientProvider apiClientProvider;
    private ConnectionProvider connectionProvider;
    private ConnectionWrapper connectionWrapper;

    public AbstractAPI(APIClientProvider apiClientProvider, ConnectionProvider connectionProvider) {
        this.apiClientProvider = apiClientProvider;
        this.connectionProvider = connectionProvider;
    }

    protected AbstractAPI(APIClientProvider apiClientProvider, ConnectionWrapper connectionWrapper) {
        this.apiClientProvider = apiClientProvider;
        this.connectionWrapper = connectionWrapper;
    }

    protected final ConnectionWrapper getConnectionWrapper() {
        return getConnectionWrapper(ConnectionWrapper.Type.NORMAL);
    }

    protected final ConnectionWrapper getTransactionalConnectionWrapper() {
        return getConnectionWrapper(ConnectionWrapper.Type.TRANSACTIONAL);
    }

    private ConnectionWrapper getConnectionWrapper(ConnectionWrapper.Type type) {
        if (connectionWrapper == null)
            connectionWrapper = new ConnectionWrapper(connectionProvider, type);
        if (type.equals(Type.TRANSACTIONAL) && !connectionWrapper.getType().equals(type))
            connectionWrapper.setType(type);
        connectionWrapper.setApiStartedTransation(this);         
        return connectionWrapper;
    }

    protected final void commit(ConnectionWrapper conn) throws SQLException {
        if (connectionWrapper.isAPIStartedTransaction(this)) {
            conn.executeBatchStatements();
            conn.getConnection().commit();
        }        
    }

    protected final void rollback(ConnectionWrapper conn) throws PaymentException {
        try {
            if (conn != null && conn.getType().equals(ConnectionWrapper.Type.TRANSACTIONAL))            
                conn.getConnection().rollback();
        } catch (SQLException e) {
            throw new PaymentException(e);
        }
    }

    protected final void close(ConnectionWrapper conn) throws PaymentException {
        try {
            if (conn != null  && conn.isAPIStartedTransaction(this))            
                conn.getConnection().close();
        } catch (SQLException e) {
            throw new PaymentException(e);
        }
    }

    protected final APIClientProvider getApiClientProvider() {
        return apiClientProvider;
    }
}