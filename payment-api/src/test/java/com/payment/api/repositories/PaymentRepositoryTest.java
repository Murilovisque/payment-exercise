package com.payment.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.payment.api.TestUtils;
import com.payment.api.models.Buyer;
import com.payment.api.models.Card;
import com.payment.api.models.Payment;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentRepositoryTest {

    private ConnectionWrapper connectionWrapper;
    private PaymentRepository paymentRepository;

    @BeforeClass
    public static void setup() throws SQLException {
        TestUtils.deleteAllOfTable(PaymentRepository.TABLE_NAME, FormOfPaymentRepository.TABLE_NAME, BuyerRepository.TABLE_NAME);
    }

    @Before
    public void before() {
        connectionWrapper = TestUtils.getConnectionWrapper();
        paymentRepository = new PaymentRepository(connectionWrapper);
    }

    @Test
    public void test1_ShouldReturnEmptyPayment() throws SQLException {
        assertFalse(paymentRepository.getPaymentById(TestUtils.PAYMENT_ID_1).isPresent());
    }

    @Test
    public void test2_ShouldInsertCard() throws SQLException {        
        new FormOfPaymentRepositoryTest().test2_ShouldInsertCard();
        new BuyerRepositoryTest().test2_ShouldInsertBuyer();
        connectionWrapper = TestUtils.getTransactionConnectionWrapper();
        Card card = new FormOfPaymentRepository(connectionWrapper).getCardByNumber(TestUtils.CARD_NUMBER_1).get();
        Buyer buyer = new BuyerRepository(connectionWrapper).getByCPF(TestUtils.BUYER_1_CPF).get();
        
        paymentRepository = new PaymentRepository(connectionWrapper);
        paymentRepository.insert(TestUtils.API_CLIENT_ID_1, TestUtils.getPaymentOne(card), card.getId(), buyer.getId());
        connectionWrapper.executeBatchStatements();
        connectionWrapper.getConnection().commit();
    }

    @Test
    public void test3_ShouldReturnOneCreditCardPayment() throws SQLException {
        Buyer buyer = new BuyerRepository(connectionWrapper).getByCPF(TestUtils.BUYER_1_CPF).get();
        List<Payment> pays = paymentRepository.getPaymentByBuyerId(buyer.getId());
        assertEquals(1, pays.size());
        Optional<Payment> pay = paymentRepository.getPaymentById(pays.get(0).getId());
        assertTrue(pay.isPresent());

        for (Payment p : Arrays.asList(pay.get(), pays.get(0))) {
            assertEquals(TestUtils.PAYMENT_AMOUNT_1, p.getAmount());
            assertEquals(TestUtils.PAYMENT_STATUS_1, p.getStatus());
            assertEquals(TestUtils.PAYMENT_TYPE_1, p.getType());
        }
    }
    

}