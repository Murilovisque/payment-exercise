package com.payment.api;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

import com.payment.api.models.Buyer;
import com.payment.api.models.Card;
import com.payment.api.models.CreditCardPayment;
import com.payment.api.models.Payment;
import com.payment.api.repositories.ConnectionProvider;
import com.payment.api.repositories.ConnectionWrapper;
import com.payment.api.repositories.ConnectionWrapper.Type;

public class TestUtils {

    public static final Long API_CLIENT_ID_1 = 1L;

    public static final String CARD_HOLDER_NAME_1 = "Fulano";
    public static final String CARD_NUMBER_1 = "012850003580200";
    public static final LocalDate CARD_EXPIRATION_DATE_1 = LocalDate.now().plusMonths(1);
    public static final String CARD_CVV_1 = "123";
    
    public static final String BUYER_1_CPF = "31425444329";
    public static final String BUYER_1_NAME = "buy1";
    public static final String BUYER_1_EMAIL = "buy1@gmail.com";

    private static final String DB_TEST = "dev_payment_test";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "teste001";

    public static final UUID PAYMENT_ID_1 = UUID.fromString("b8ebddbe-b58e-4780-aa39-d6bdbcb5c060");
    public static final Payment.Status PAYMENT_STATUS_1 = Payment.Status.PAID;
    public static final BigDecimal PAYMENT_AMOUNT_1 = BigDecimal.valueOf(10.00);
    public static final Payment.Type PAYMENT_TYPE_1 = Payment.Type.CREDIT_CARD;
	

    public static Connection getConnection() {
        try {
            String url = "jdbc:postgresql://localhost/" + DB_TEST;
            Properties props = new Properties();
            props.setProperty("user",DB_USER);
            props.setProperty("password",DB_PASSWORD);        
            return DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConnectionProvider getConnectionProvider() {
        return () -> TestUtils.getConnection();
    }

    public static APIClientProvider getApiClientProvider() {
        return () -> TestUtils.API_CLIENT_ID_1;
    }

    public static ConnectionWrapper getConnectionWrapper() {
        return new ConnectionWrapper(() -> getConnection(), Type.NORMAL);
    }

    public static ConnectionWrapper getTransactionConnectionWrapper() {
        return new ConnectionWrapper(() -> getConnection(), ConnectionWrapper.Type.TRANSACTIONAL);
    }

    public static void deleteAllOfTable(String... tables) throws SQLException {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            for (String t : tables)
                stmt.execute(String.format("delete from %s", t));
        }
    }

    public static Buyer getBuyerOne() {
        return new Buyer(BUYER_1_NAME, BUYER_1_CPF, BUYER_1_EMAIL);
    }

    public static Card getCardOne() {
        return getCardOne(null);
    }
    
    public static Card getCardOne(Long id) {
        return new Card(id, CARD_HOLDER_NAME_1, CARD_NUMBER_1, CARD_EXPIRATION_DATE_1, CARD_CVV_1);
    }

    public static Payment getPaymentOne(Card card) {
        return new CreditCardPayment(PAYMENT_ID_1, PAYMENT_AMOUNT_1, PAYMENT_STATUS_1, card);
    }

}