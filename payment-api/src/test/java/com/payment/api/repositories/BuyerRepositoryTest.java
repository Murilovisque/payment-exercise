package com.payment.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Optional;

import com.payment.api.TestUtils;
import com.payment.api.models.Buyer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BuyerRepositoryTest {

    private ConnectionWrapper connectionWrapper;
    private BuyerRepository buyerRepository;

    @BeforeClass
    public static void setup() throws SQLException {
        TestUtils.deleteAllOfTable(BuyerRepository.TABLE_NAME);
    }

    @Before
    public void before() {
        connectionWrapper  = TestUtils.getConnectionWrapper();
        buyerRepository = new BuyerRepository(connectionWrapper);
    }

    @Test
    public void test1_ShouldReturnEmptyBuyer() throws SQLException {
        assertFalse(buyerRepository.getByCPF(TestUtils.BUYER_1_CPF).isPresent());
    }

    @Test
    public void test2_ShouldInsertBuyer() throws SQLException {
        connectionWrapper  = TestUtils.getTransactionConnectionWrapper();
        buyerRepository = new BuyerRepository(connectionWrapper);
        buyerRepository.insert(TestUtils.getBuyerOne());
        connectionWrapper.executeBatchStatements();
        connectionWrapper.getConnection().commit();
    }
    
    @Test
    public void test3_ShouldReturnBuyer() throws SQLException {
        Optional<Buyer> buy = buyerRepository.getByCPF(TestUtils.BUYER_1_CPF);
        assertTrue(buy.isPresent());
        assertEquals(TestUtils.getBuyerOne().getName(), buy.get().getName());
        assertEquals(TestUtils.getBuyerOne().getEmail(), buy.get().getEmail());
        assertEquals(TestUtils.getBuyerOne().getCpf(), buy.get().getCpf());
    }
}