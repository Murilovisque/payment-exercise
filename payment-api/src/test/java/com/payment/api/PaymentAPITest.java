package com.payment.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.models.BoletoPayment;
import com.payment.api.models.Buyer;
import com.payment.api.models.Card;
import com.payment.api.models.CreditCardPayment;
import com.payment.api.models.Payment;
import com.payment.api.repositories.BuyerRepository;
import com.payment.api.repositories.FormOfPaymentRepository;
import com.payment.api.repositories.PaymentRepository;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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

    public void test1_ShouldReturnEmptyBuyerAndCardAndCreditCardPayment() throws PaymentException {
        Optional<Buyer> buyer = buyerAPI.getBuyerWithCPF(TestUtils.BUYER_1_CPF);
        assertFalse(buyer.isPresent());
        Optional<Card> card = formOfPaymentAPI.getCardWithNumber(TestUtils.CARD_NUMBER_1);
        assertFalse(card.isPresent());
        assertTrue(paymentAPI.getPayments(TestUtils.QUERY_LIMIT).isEmpty());
    }

    @Test
    public void test2_shouldInsertBuyerAndCardAndCreditCardPayment() throws PaymentException {
        UUID id = paymentAPI.processPayment(TestUtils.getBuyerOne(), TestUtils.getPaymentOne(TestUtils.getCardOne()));
        assertEquals(1, paymentAPI.getPayments(TestUtils.QUERY_LIMIT).size());
        List<Buyer> buyers = buyerAPI.getBuyers(TestUtils.QUERY_LIMIT);
        assertEquals(1, buyers.size());        
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buyers.get(0));
        Optional<Card> card = formOfPaymentAPI.getCardWithNumber(TestUtils.CARD_NUMBER_1);
        assertTrue(card.isPresent());
        TestUtils.assertAllFields(TestUtils.getCardOne(), card.get());
        Optional<Payment> pay = paymentAPI.getPaymentWithId(id);
        assertTrue(pay.isPresent());
        CreditCardPayment payCreditCard = (CreditCardPayment) pay.get();
        TestUtils.assertAllFields(TestUtils.getPaymentOne(card.get()), payCreditCard);
        TestUtils.assertAllFields(TestUtils.getCardOne(), payCreditCard.getCard());
    }

    @Test
    public void test3_shouldBoletoPayment() throws PaymentException {
        UUID id = paymentAPI.processPayment(TestUtils.getBuyerOne(), TestUtils.getBoletoPaymentOne());
        assertEquals(2, paymentAPI.getPayments(TestUtils.QUERY_LIMIT).size());
        List<Buyer> buyers = buyerAPI.getBuyers(TestUtils.QUERY_LIMIT);
        assertEquals(1, buyers.size());
        Optional<Payment> pay = paymentAPI.getPaymentWithId(id);
        assertTrue(pay.isPresent());
        BoletoPayment payBoleto = (BoletoPayment) pay.get();
        TestUtils.assertAllFields(TestUtils.getBoletoPaymentOne(), payBoleto);    
        assertTrue(payBoleto.getBoleto() != null && payBoleto.getBoleto().getNumber() != null);
    }

    @Test
    public void test4_shouldReturnPaymentOfBuyer() throws PaymentException {
        Buyer buyer = buyerAPI.getBuyerWithCPF(TestUtils.BUYER_1_CPF).get();
        assertEquals(2, paymentAPI.getPaymentsOfBuyer(buyer.getId()).size());
    }
}