package com.payment.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Optional;

import com.payment.api.exceptions.PaymentException;
import com.payment.api.models.Buyer;
import com.payment.api.repositories.BuyerRepository;

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
        TestUtils.deleteAllOfTable(BuyerRepository.TABLE_NAME);
    }

    @Before
    public void before() {
        buyerAPI = new BuyerAPI(TestUtils.getApiClientProvider(), TestUtils.getConnectionProvider());
    }
    
    @Test
    public void test1_ShouldFalseWhenBuyerDoesNotExists() throws PaymentException {
        assertFalse(buyerAPI.getBuyerWithCPF(TestUtils.BUYER_1_CPF).isPresent());
    }

    @Test
    public void test2_ShouldSaveBuyer() throws PaymentException {
        buyerAPI.save(TestUtils.getBuyerOne());
    }
    
    @Test
    public void test3_ShouldTrueWhenBuyerExists() throws PaymentException {
        Optional<Buyer> buyer = buyerAPI.getBuyerWithCPF(TestUtils.BUYER_1_CPF);
        assertTrue(buyer.isPresent());
        assertEquals(TestUtils.BUYER_1_NAME, buyer.get().getName());
        assertEquals(TestUtils.BUYER_1_EMAIL, buyer.get().getEmail());
        assertEquals(TestUtils.BUYER_1_CPF, buyer.get().getCpf());
    }
}