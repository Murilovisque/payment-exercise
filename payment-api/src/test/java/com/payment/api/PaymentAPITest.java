package com.payment.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.models.Buyer;
import com.payment.api.models.Card;
import com.payment.api.models.CreditCardPayment;
import com.payment.api.models.Payment;
import com.payment.api.repositories.BuyerRepository;
import com.payment.api.repositories.FormOfPaymentRepository;
import com.payment.api.repositories.PaymentRepository;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PaymentAPITest {

    private PaymentAPI paymentAPI;
    private BuyerAPI buyerAPI;
    private FormOfPaymentAPI formOfPaymentAPI;

    @BeforeClass
    public static void setup() throws SQLException {
        TestUtils.deleteAllOfTable(PaymentRepository.TABLE_NAME, FormOfPaymentRepository.TABLE_NAME, BuyerRepository.TABLE_NAME);
    }

    @Before
    public void before() {
        paymentAPI = new PaymentAPI(TestUtils.getApiClientProvider(), TestUtils.getConnectionProvider());
        buyerAPI = new BuyerAPI(TestUtils.getApiClientProvider(), TestUtils.getConnectionProvider());
        formOfPaymentAPI = new FormOfPaymentAPI(TestUtils.getApiClientProvider(), TestUtils.getConnectionProvider());
    }

    @Test
    public void shouldInsertBuyerAndCardAndCreditCardPayment() throws PaymentException {
        Optional<Buyer> buyer = buyerAPI.getBuyerWithCPF(TestUtils.BUYER_1_CPF);
        assertFalse(buyer.isPresent());
        Optional<Card> card = formOfPaymentAPI.getCardWithNumber(TestUtils.CARD_NUMBER_1);
        assertFalse(card.isPresent());
        UUID id = paymentAPI.processPayment(TestUtils.getBuyerOne(), TestUtils.getPaymentOne(TestUtils.getCardOne()));
        buyer = buyerAPI.getBuyerWithCPF(TestUtils.BUYER_1_CPF);
        assertTrue(buyer.isPresent());
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buyer.get());
        card = formOfPaymentAPI.getCardWithNumber(TestUtils.CARD_NUMBER_1);
        assertTrue(card.isPresent());
        TestUtils.assertAllFields(TestUtils.getCardOne(), card.get());
        Optional<Payment> pay = paymentAPI.getPaymentWithId(id);
        assertTrue(pay.isPresent());
        CreditCardPayment payCreditCard = (CreditCardPayment) pay.get();
        TestUtils.assertAllFields(TestUtils.getPaymentOne(card.get()), payCreditCard);
        TestUtils.assertAllFields(TestUtils.getCardOne(), payCreditCard.getCard());
    }


    // @Test
    // public void test() throws Exception {
    //     Connection conn = TestUtils.getTransactionConnectionWrapper().getConnection();
    //     PreparedStatement stmt = conn.prepareStatement("insert into api_client (name) values (?)");
    //     stmt.setString(1, "mano");
    //     stmt.addBatch();
    //     ResultSet rs = conn.createStatement().executeQuery("select * from api_client");
    //     while (rs.next()) {
    //         String s = rs.getString("name");
    //         System.out.println(s);
    //     }
    // }
}