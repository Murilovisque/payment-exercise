package com.payment.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Optional;

import com.payment.api.TestUtils;
import com.payment.api.models.Card;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FormOfPaymentRepositoryTest {

    private ConnectionWrapper connectionWrapper;
    private FormOfPaymentRepository formOfPaymentRepository;

    @BeforeClass
    public static void setup() throws SQLException {
        TestUtils.deleteAllOfTable(PaymentRepository.TABLE_NAME, FormOfPaymentRepository.TABLE_NAME);
    }

    @Before
    public void before() {
        connectionWrapper = TestUtils.getConnectionWrapper();
        formOfPaymentRepository = new FormOfPaymentRepository(connectionWrapper);
    }

    @Test
    public void test1_ShouldReturnEmptyCard() throws SQLException {
        assertFalse(formOfPaymentRepository.getCardByNumber(TestUtils.CARD_NUMBER_1).isPresent());
    }

    @Test
    public void test2_ShouldInsertCard() throws SQLException {
        connectionWrapper  = TestUtils.getTransactionConnectionWrapper();
        formOfPaymentRepository = new FormOfPaymentRepository(connectionWrapper);
        formOfPaymentRepository.insert(TestUtils.getCardOne());
        connectionWrapper.executeBatchStatements();
        connectionWrapper.getConnection().commit();
    }

    @Test
    public void test3_ShouldReturnOneCard() throws SQLException {
        Optional<Card> card = formOfPaymentRepository.getCardByNumber(TestUtils.CARD_NUMBER_1);
        assertTrue(card.isPresent());
        assertEquals(TestUtils.getCardOne().getHolderName(), card.get().getHolderName());
        assertEquals(TestUtils.getCardOne().getNumber(), card.get().getNumber());
        assertEquals(TestUtils.getCardOne().getExpirationDate(), card.get().getExpirationDate());
        assertEquals(TestUtils.getCardOne().getCvv(), card.get().getCvv());
    }
}