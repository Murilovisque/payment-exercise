package com.payment.api;

import java.sql.SQLException;
import java.util.Optional;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.models.Buyer;
import com.payment.api.repositories.BuyerRepository;
import com.payment.api.repositories.ConnectionProvider;
import com.payment.api.repositories.ConnectionWrapper;

class BuyerAPI extends AbstractAPI {

    BuyerAPI(APIClientProvider apiClientProvider, ConnectionProvider connectionProvider) {
        super(apiClientProvider, connectionProvider);
    }

    BuyerAPI(APIClientProvider apiClientProvider, ConnectionWrapper connectionWrapper) {
        super(apiClientProvider, connectionWrapper);
    }

    public void save(Buyer buyer) throws PaymentException {
        ConnectionWrapper connWrapper = null;
        try {
            connWrapper = getTransactionalConnectionWrapper();
            new BuyerRepository(connWrapper).insert(buyer);
            commit(connWrapper);
        } catch (SQLException e) {
            rollback(connWrapper);
            throw new PaymentException(e);
		} finally {
            close(connWrapper);
        }
    }    

    public Optional<Buyer> getBuyerWithCPF(String cpf) throws PaymentException {
        ConnectionWrapper connWrapper = null;
        try {
            connWrapper = getConnectionWrapper();
            return new BuyerRepository(connWrapper).getByCPF(cpf);
        } catch (SQLException e) {
            throw new PaymentException(e);
		} finally {
            close(connWrapper);
        }
    }
}