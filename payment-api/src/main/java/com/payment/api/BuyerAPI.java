package com.payment.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.models.Buyer;
import com.payment.api.repositories.BuyerRepository;
import com.payment.api.repositories.ConnectionProvider;
import com.payment.api.repositories.ConnectionWrapper;
import com.payment.api.repositories.search.SearchConditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuyerAPI extends AbstractAPI {

    private static final Logger logger = LoggerFactory.getLogger(BuyerAPI.class);

    public BuyerAPI(APIClientProvider apiClientProvider, ConnectionProvider connectionProvider) {
        super(apiClientProvider, connectionProvider);
    }

    BuyerAPI(APIClientProvider apiClientProvider, ConnectionWrapper connectionWrapper) {
        super(apiClientProvider, connectionWrapper);
    }

    UUID save(Buyer buyer) throws PaymentException {
        ConnectionWrapper connWrapper = null;
        try {
            connWrapper = getTransactionalConnectionWrapper();
            UUID id = new BuyerRepository(connWrapper).insert(buyer);
            commit(connWrapper);
            logger.info("Buyer saved");
            return id;
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
            return new BuyerRepository(connWrapper).getWithCPF(cpf);
        } catch (SQLException e) {
            throw new PaymentException(e);
		} finally {
            close(connWrapper);
        }
    }

    public List<Buyer> getBuyers(int limit) throws PaymentException {
        ConnectionWrapper connWrapper = null;
        try {
            connWrapper = getConnectionWrapper();
            return new BuyerRepository(connWrapper).getBuyers(limit);
        } catch (SQLException e) {
            throw new PaymentException(e);
		} finally {
            close(connWrapper);
        }
    }

    public List<Buyer> search(SearchConditions searchConditions, int limit) throws PaymentException {
        ConnectionWrapper connWrapper = null;
        try {
            connWrapper = getConnectionWrapper();
            return new BuyerRepository(connWrapper).search(searchConditions, limit);
        } catch (SQLException e) {
            throw new PaymentException(e);
		} finally {
            close(connWrapper);
        }
    }
}