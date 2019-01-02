package com.payment.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.exceptions.UnauthorizedPaymentException;
import com.payment.api.models.Buyer;
import com.payment.api.models.Card;
import com.payment.api.models.CreditCardPayment;
import com.payment.api.models.Payment;
import com.payment.api.repositories.ConnectionProvider;
import com.payment.api.repositories.ConnectionWrapper;
import com.payment.api.repositories.PaymentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentAPI extends AbstractAPI {

    private static final Logger logger = LoggerFactory.getLogger(PaymentAPI.class);

    public PaymentAPI(APIClientProvider apiClientProvider, ConnectionProvider connectionProvider) {
        super(apiClientProvider, connectionProvider);
    }

    public UUID processPayment(Buyer buyer, Payment pay) throws PaymentException {
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getTransactionalConnectionWrapper();
            FormOfPaymentAPI formOfPaymentAPI = new FormOfPaymentAPI(getApiClientProvider(), connectionWrapper);
            UUID formOfPaymentId = null;
            if (pay.getType().equals(Payment.Type.CREDIT_CARD)) {
                CreditCardPayment payCreditCard = (CreditCardPayment) pay;
                Optional<Card> cardFromDB = formOfPaymentAPI.getCardWithNumber(payCreditCard.getCard().getNumber());
                if (cardFromDB.isPresent()) {
                    formOfPaymentId = cardFromDB.get().getId();
                } else if (formOfPaymentAPI.isValidCard(payCreditCard.getCard())) {
                    formOfPaymentId = formOfPaymentAPI.save(payCreditCard.getCard());
                } else {
                    throw new UnauthorizedPaymentException("Cartão de crédito não autorizado");
                }
            } else {
                formOfPaymentId = formOfPaymentAPI.generateBoleto();
            }
            
            BuyerAPI buyerAPI = new BuyerAPI(getApiClientProvider(), connectionWrapper);
            Optional<Buyer> buyerFromDB = buyerAPI.getBuyerWithCPF(buyer.getCpf());
            UUID buyerId = buyerFromDB.isPresent() ? buyerFromDB.get().getId() : buyerAPI.save(buyer);
            UUID payId = new PaymentRepository(connectionWrapper).insert(getApiClientProvider().getAPIClientId(), pay, formOfPaymentId, buyerId);
            commit(connectionWrapper);
            logger.info("Payment saved");
            return payId;
        } catch (SQLException e) {
            rollback(connectionWrapper);
            throw new PaymentException(e);
        } finally {
            close(connectionWrapper);
        }       
    }

    public Optional<Payment> getPaymentWithId(UUID id) throws PaymentException {
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getConnectionWrapper();
            return new PaymentRepository(connectionWrapper).getPaymentWithId(id);
        } catch (SQLException e) {
            rollback(connectionWrapper);
            throw new PaymentException(e);
        } finally {
            close(connectionWrapper);
        }       
    }

    public List<Payment> getPayments(int limit) throws PaymentException {
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getConnectionWrapper();
            return new PaymentRepository(connectionWrapper).getPayments(limit);
        } catch (SQLException e) {
            rollback(connectionWrapper);
            throw new PaymentException(e);
        } finally {
            close(connectionWrapper);
        }       
    }

    public List<Payment> getPaymentsOfBuyer(UUID buyerId) throws PaymentException {
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getConnectionWrapper();
            return new PaymentRepository(connectionWrapper).getPaymentWithBuyerId(buyerId);
        } catch (SQLException e) {
            rollback(connectionWrapper);
            throw new PaymentException(e);
        } finally {
            close(connectionWrapper);
        }       
    }
}