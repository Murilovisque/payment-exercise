package com.payment.api;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.models.Boleto;
import com.payment.api.models.Card;
import com.payment.api.repositories.FormOfPaymentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.payment.api.repositories.ConnectionProvider;
import com.payment.api.repositories.ConnectionWrapper;

class FormOfPaymentAPI extends AbstractAPI {

    private static final Logger logger = LoggerFactory.getLogger(FormOfPaymentAPI.class);
    private static final Pattern CARD_NUMBER_REGEX = Pattern.compile("\\d{13,16}");

    public FormOfPaymentAPI(APIClientProvider apiClientProvider, ConnectionWrapper connectionWrapper) {
        super(apiClientProvider, connectionWrapper);
    }

    public FormOfPaymentAPI(APIClientProvider apiClientProvider, ConnectionProvider connectionProvider) {
        super(apiClientProvider, connectionProvider);
    }

    public boolean isValidCard(Card card) {
        card.normalizeExpirationDate();
        if (card.getExpirationDate().isBefore(getValidDateLimit()))
            return false;
        if (card.getHolderName().trim().isEmpty())
            return false;
        String number = card.getNumber();
        if (!CARD_NUMBER_REGEX.matcher(number).matches())
            return false;
        // if (validSequence(number))//TODO: Validar bandeiras
        //     return false;
        return true;
    }

    public Optional<Card> getCardWithNumber(String number) throws PaymentException {
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getConnectionWrapper();
            return new FormOfPaymentRepository(connectionWrapper).getCardWithNumber(number);
        } catch (SQLException e) {
            throw new PaymentException(e);
        } finally {
            close(connectionWrapper);
        }
    }

    public UUID save(Card card) throws PaymentException {
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getTransactionalConnectionWrapper();
            UUID id = new FormOfPaymentRepository(connectionWrapper).insert(card);
            commit(connectionWrapper);
            logger.info("Card saved");
            return id;
        } catch (SQLException e) {
            rollback(connectionWrapper);
            throw new PaymentException(e);
        } finally {
            close(connectionWrapper);
        }
    }

    public UUID generateBoleto() throws PaymentException {
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getTransactionalConnectionWrapper();
            UUID id = new FormOfPaymentRepository(connectionWrapper).insert(new Boleto(Integer.toString(new Random().nextInt())));
            commit(connectionWrapper);
            logger.info("Boleto saved");
            return id;
        } catch (SQLException e) {
            rollback(connectionWrapper);
            throw new PaymentException(e);
        } finally {
            close(connectionWrapper);
        }
    }

    private static LocalDate getValidDateLimit() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    private boolean validSequence(String number) { //TODO: check
        int[] ints = new int[number.length()];
		for (int i = 0; i < number.length(); i++) {
			ints[i] = Integer.parseInt(number.substring(i, i + 1));
		}
		for (int i = ints.length - 2; i >= 0; i = i - 2) {
			int j = ints[i];
			j = j * 2;
			if (j > 9) {
				j = j % 10 + 1;
			}
			ints[i] = j;
		}
		int sum = 0;
		for (int i = 0; i < ints.length; i++)
			sum += ints[i];
		return sum % 10 == 0;
    }
}