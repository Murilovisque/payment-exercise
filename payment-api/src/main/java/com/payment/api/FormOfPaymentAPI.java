package com.payment.api;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Pattern;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.models.Boleto;
import com.payment.api.models.Card;
import com.payment.api.repositories.FormOfPaymentRepository;
import com.payment.api.repositories.ConnectionWrapper;

class FormOfPaymentAPI extends AbstractAPI {

    private static final Pattern CARD_NUMBER_REGEX = Pattern.compile("\\d{13,16}");

    FormOfPaymentAPI(APIClientProvider apiClientProvider, ConnectionWrapper connectionWrapper) {
        super(apiClientProvider, connectionWrapper);
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
        if (validSequence(number))//TODO: Validar bandeiras
            return false;
        return true;
    }

    public Optional<Card> getCardWithNumber(String number) throws PaymentException {
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getConnectionWrapper();
            return new FormOfPaymentRepository(connectionWrapper).getCardByNumber(number);
        } catch (SQLException e) {
            throw new PaymentException(e);
        } finally {
            close(connectionWrapper);
        }
    }

    public void save(Card card) throws PaymentException {
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getTransactionalConnectionWrapper();
            new FormOfPaymentRepository(connectionWrapper).insert(card);
            commit(connectionWrapper);
        } catch (SQLException e) {
            rollback(connectionWrapper);
            throw new PaymentException(e);
        } finally {
            close(connectionWrapper);
        }
    }

    // public Boleto generateBoleto() throws PaymentException {
        
    // }

    private static LocalDate getValidDateLimit() {
        return LocalDate.now().plusMonths(1).minusDays(1);
    }

    private boolean validSequence(String number) {
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