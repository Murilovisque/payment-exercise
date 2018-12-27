package com.payment.api;

import java.sql.SQLException;
import java.util.Optional;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.exceptions.UnauthorizedPaymentException;
import com.payment.api.models.Buyer;
import com.payment.api.models.Card;
import com.payment.api.models.CreditCardPayment;
import com.payment.api.models.Payment;
import com.payment.api.repositories.ConnectionProvider;
import com.payment.api.repositories.ConnectionWrapper;
import com.payment.api.repositories.PaymentRepository;

public class PaymentAPI extends AbstractAPI {

    public PaymentAPI(APIClientProvider apiClientProvider, ConnectionProvider connectionProvider) {
        super(apiClientProvider, connectionProvider);
    }

    public void processPayment(Buyer buyer, Payment pay) throws PaymentException {
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getTransactionalConnectionWrapper();
            FormOfPaymentAPI formOfPaymentAPI = new FormOfPaymentAPI(getApiClientProvider(), connectionWrapper);
            Long formOfPaymentId = null;
            if (pay.getType().equals(Payment.Type.CREDIT_CARD)) {
                CreditCardPayment payCreditCard = (CreditCardPayment) pay;
                Optional<Card> cardFromDB = formOfPaymentAPI.getCardWithNumber(payCreditCard.getCard().getNumber());
                if (cardFromDB.isPresent()) {
                    formOfPaymentId = cardFromDB.get().getId();
                } else if (formOfPaymentAPI.isValidCard(payCreditCard.getCard())) {
                    formOfPaymentAPI.save(payCreditCard.getCard());
                    formOfPaymentId = formOfPaymentAPI.getCardWithNumber(payCreditCard.getCard().getNumber()).get().getId();
                } else {
                    throw new UnauthorizedPaymentException("Cartão de crédito não autorizado");
                }
            } else {

            }
            
            BuyerAPI buyerAPI = new BuyerAPI(getApiClientProvider(), connectionWrapper);
            Optional<Buyer> buyerFromDB = buyerAPI.getBuyerWithCPF(buyer.getCpf());
            if (!buyerFromDB.isPresent()) {
                buyerAPI.save(buyer);
                buyerFromDB = buyerAPI.getBuyerWithCPF(buyer.getCpf());
            }            
            new PaymentRepository(connectionWrapper).insert(getApiClientProvider().getAPIClientId(), pay, formOfPaymentId, buyerFromDB.get().getId());
            
                   
            

        } catch (SQLException e) {
            rollback(connectionWrapper);
            throw new PaymentException(e);
        } finally {
            close(connectionWrapper);
        }
        
    }
}