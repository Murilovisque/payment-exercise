package com.payment.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.models.Buyer;
import com.payment.api.repositories.BuyerRepository;
import com.payment.api.repositories.PaymentRepository;
import com.payment.api.search.SearchConditions;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BuyerAPITest {

    private BuyerAPI buyerAPI;

    @BeforeClass
    public static void setup() throws SQLException {
        TestUtils.deleteAllOfTable(PaymentRepository.TABLE_NAME, BuyerRepository.TABLE_NAME);
    }

    @Before
    public void before() {
        buyerAPI = new BuyerAPI(TestUtils.getApiClientProvider(), TestUtils.getConnectionProvider());
    }
    
    @Test
    public void test1_ShouldFalseWhenBuyerDoesNotExists() throws PaymentException {
        assertTrue(buyerAPI.getBuyers(TestUtils.QUERY_LIMIT).isEmpty());
    }

    @Test
    public void test2_ShouldSaveBuyer() throws PaymentException {
        buyerAPI.save(TestUtils.getBuyerOne());
    }
    
    @Test
    public void test3_ShouldBuyer() throws PaymentException {
        Optional<Buyer> buyer = buyerAPI.getBuyerWithCPF(TestUtils.BUYER_1_CPF);
        assertTrue(buyer.isPresent());
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buyer.get());
    }

    @Test
    public void test4_ShouldTrueBuyerByName() throws PaymentException {
        String partialName = TestUtils.BUYER_1_NAME.substring(0, TestUtils.BUYER_1_NAME.length() / 2);
        List<Buyer> buyers = buyerAPI.search(new SearchConditions().put(BuyerRepository.COLUMN_NAME, partialName), TestUtils.QUERY_LIMIT);
        assertEquals(1, buyers.size());
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buyers.get(0));
    }

    @Test
    public void test4_ShouldTrueBuyerByCPF() throws PaymentException {
        String partialCPF = TestUtils.BUYER_1_CPF.substring(0, TestUtils.BUYER_1_CPF.length() / 2);
        List<Buyer> buyers = buyerAPI.search(new SearchConditions().put(BuyerRepository.COLUMN_CPF, partialCPF), TestUtils.QUERY_LIMIT);
        assertEquals(1, buyers.size());
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buyers.get(0));
    }

    @Test
    public void test4_ShouldTrueBuyerByEmail() throws PaymentException {
        String partialEmail = TestUtils.BUYER_1_EMAIL.substring(0, TestUtils.BUYER_1_EMAIL.length() / 2);
        List<Buyer> buyers = buyerAPI.search(new SearchConditions().put(BuyerRepository.COLUMN_EMAIL, partialEmail), TestUtils.QUERY_LIMIT);
        assertEquals(1, buyers.size());
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buyers.get(0));
    }
}