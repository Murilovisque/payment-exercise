package com.payment.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.models.Card;
import com.payment.api.repositories.FormOfPaymentRepository;
import com.payment.api.repositories.PaymentRepository;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FormOfPaymentAPITest {

    private FormOfPaymentAPI cardAPI;

    @BeforeClass
    public static void setup() throws SQLException {
        TestUtils.deleteAllOfTable(PaymentRepository.TABLE_NAME, FormOfPaymentRepository.TABLE_NAME);
    }

    @Before
    public void before() {
        cardAPI = new FormOfPaymentAPI(TestUtils.getApiClientProvider(), TestUtils.getConnectionWrapper());
    }

    @Test
    public void shouldReturnInvalidCardWhenExpirationDateIsBeforeThanCurrentMonthAndYear() {
        Card card = new Card(TestUtils.CARD_HOLDER_NAME_1, TestUtils.CARD_NUMBER_1,
            LocalDate.now().minusMonths(1), TestUtils.CARD_CVV_1);
        assertFalse(cardAPI.isValidCard(card));
    }

    @Test
    public void shouldReturnInvalidCardWhenHolderNameIsInvalid() {
        Card card = new Card("", TestUtils.CARD_NUMBER_1,
            TestUtils.CARD_EXPIRATION_DATE_1, TestUtils.CARD_CVV_1);
        assertFalse(cardAPI.isValidCard(card));
        card = new Card(" ", TestUtils.CARD_NUMBER_1,
            TestUtils.CARD_EXPIRATION_DATE_1, TestUtils.CARD_CVV_1);
        assertFalse(cardAPI.isValidCard(card));
    }

    @Test
    public void shouldReturnInvalidCardWhenNumberIsInvalid() {
        Card cardAphanumeric = new Card(TestUtils.CARD_HOLDER_NAME_1, "H51444414241241",
            TestUtils.CARD_EXPIRATION_DATE_1, TestUtils.CARD_CVV_1);
        assertFalse(cardAPI.isValidCard(cardAphanumeric));
        Card cardWithLessThan13Chars = new Card(TestUtils.CARD_HOLDER_NAME_1, "441424124172",
            TestUtils.CARD_EXPIRATION_DATE_1, TestUtils.CARD_CVV_1);
        assertFalse(cardAPI.isValidCard(cardWithLessThan13Chars));
        Card cardWithMoreThan16Chars = new Card(TestUtils.CARD_HOLDER_NAME_1, "44142412451424121",
            TestUtils.CARD_EXPIRATION_DATE_1, TestUtils.CARD_CVV_1);
        assertFalse(cardAPI.isValidCard(cardWithMoreThan16Chars));
    }


    @Test
    public void test1_ShouldReturnFalseWhenCardNumberDoesNotExits() throws PaymentException {
        assertFalse(cardAPI.getCardWithNumber(TestUtils.CARD_NUMBER_1).isPresent());        
    }

    @Test
    public void test2_ShouldSaveCard() throws PaymentException {
        cardAPI.save(TestUtils.getCardOne());
    }

    @Test
    public void test3_ShouldReturnTrueWhenCardNumberExits() throws PaymentException {
        Optional<Card> card = cardAPI.getCardWithNumber(TestUtils.CARD_NUMBER_1);
        assertTrue(card.isPresent());
        TestUtils.assertAllFields(TestUtils.getCardOne(), card.get());
    }
}