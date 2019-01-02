package com.payment.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;

import com.payment.api.models.Boleto;
import com.payment.api.models.BoletoPayment;
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
    public static final String CARD_NUMBER_1 = "371449635398431";
    public static final LocalDate CARD_EXPIRATION_DATE_1 = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    public static final String CARD_CVV_1 = "123";

    public static final String BUYER_1_CPF = "31425444329";
    public static final String BUYER_1_NAME = "buy1";
    public static final String BUYER_1_EMAIL = "buy1@gmail.com";

    public static final String BUYER_2_CPF = "31425444321";
    public static final String BUYER_2_NAME = "buy2";
    public static final String BUYER_2_EMAIL = "buy2@gmail.com";

    private static final String DB_TEST = "dev_payment_test";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "teste001";

    public static final UUID PAYMENT_ID_1 = UUID.fromString("b8ebddbe-b58e-4780-aa39-d6bdbcb5c060");
    public static final Payment.Status PAYMENT_STATUS_1 = Payment.Status.PAID;
    public static final BigDecimal PAYMENT_AMOUNT_1 = BigDecimal.valueOf(10.00);
    public static final Payment.Type PAYMENT_TYPE_1 = Payment.Type.CREDIT_CARD;

    public static final String BOLETO_NUMBER = "123";
    public static final BigDecimal BOLETO_AMOUNT = BigDecimal.valueOf(5.00);

    public static final int QUERY_LIMIT = 20;

    public static final Collection<String> VALID_CARD_NUMBERS = Arrays.asList("4485754305288472", "4716150782634893",
        "4532541004732987488", "6011474431072074", "6011387714656829", "6011264101040283739", "5208311130206782",
        "5545231007548086", "5292476188447344", "372899969119234", "346489908712639", "344799617426624",
        "6763034861147224", "5018547920278560", "5893909634223086");

    public static final Collection<String> VALID_MASTER_CARD_NUMBER = Arrays.asList("5208311130206782",
        "5545231007548086","5292476188447344");
        
    public static final Collection<String> VALID_MAESTRO_NUMBER = Arrays.asList("6763034861147224",
        "5018547920278560", "5893909634223086");

    public static final Collection<String> VALID_AMERICAN_EXPRESS_NUMBER = Arrays.asList("372899969119234",
        "346489908712639", "344799617426624");

    public static final Collection<String> VALID_VISA_NUMBER = Arrays.asList("4485754305288472",
        "4716150782634893", "4532541004732987488");

    public static final Collection<String> VALID_DISCOVER_NUMBER = Arrays.asList("6011474431072074",
        "6011387714656829", "6011264101040283739");

    public static Connection getConnection() {
        try {
            String url = "jdbc:postgresql://postgres/" + DB_TEST;
            Properties props = new Properties();
            props.setProperty("user", DB_USER);
            props.setProperty("password", DB_PASSWORD);
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
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String t : tables)
                stmt.execute(String.format("delete from %s", t));
        }
    }

    public static Buyer getBuyerOne() {
        return new Buyer(BUYER_1_NAME, BUYER_1_CPF, BUYER_1_EMAIL);
    }

    public static Buyer getBuyerTwo() {
        return new Buyer(BUYER_2_NAME, BUYER_2_CPF, BUYER_2_EMAIL);
    }

    public static Card getCardOne() {
        return getCardOne(null);
    }

    public static Card getCardOne(UUID id) {
        return new Card(UUID.randomUUID(), CARD_HOLDER_NAME_1, CARD_NUMBER_1, CARD_EXPIRATION_DATE_1, CARD_CVV_1);
    }

    public static Payment getPaymentOne(Card card) {
        return new CreditCardPayment(PAYMENT_AMOUNT_1, card);
    }

    public static Boleto getBoletoOne() {
        return new Boleto(BOLETO_NUMBER);
    }

    public static BoletoPayment getBoletoPaymentOne() {
        return new BoletoPayment(null, BOLETO_AMOUNT, Payment.Status.GENERATED, null);
    }

    public static void assertAllFields(Card expected, Card test) {
        assertEquals(expected.getNumber(), test.getNumber());
        assertEquals(expected.getCvv(), test.getCvv());
        assertEquals(expected.getExpirationDate(), test.getExpirationDate());
        assertEquals(expected.getHolderName(), test.getHolderName());
    }

    public static void assertAllFields(Buyer expected, Buyer test) {
        assertEquals(expected.getName(), test.getName());
        assertEquals(expected.getEmail(), test.getEmail());
        assertEquals(expected.getCpf(), test.getCpf());
    }

    public static void assertAllFields(Payment expected, Payment test) {
        assertEquals(expected.getAmount(), test.getAmount());
        assertEquals(expected.getStatus(), test.getStatus());
    }

    public static void assertAllFields(Boleto expected, Boleto test) {
        assertEquals(expected.getNumber(), test.getNumber());
    }

    public static void assertAllFields(Collection<Buyer> expected, Collection<Buyer> test) {
        assertEquals(expected.size(), test.size());
        Iterator<Buyer> iterator = test.iterator();
        while (iterator.hasNext()) {
            Buyer buyerTest = iterator.next();
            for (Buyer buyerExpected : expected) {
                if (buyerTest.getName().equals(buyerExpected.getName())
                        && buyerTest.getCpf().equals(buyerExpected.getCpf())
                        && buyerTest.getEmail().equals(buyerExpected.getEmail())) {
                    iterator.remove();
                }
            }
        }
        assertTrue(test.isEmpty());
    }
}