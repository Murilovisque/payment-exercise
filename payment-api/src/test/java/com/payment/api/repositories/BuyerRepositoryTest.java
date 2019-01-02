package com.payment.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.payment.api.TestUtils;
import com.payment.api.models.Buyer;
import com.payment.api.search.SearchConditions;

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
        TestUtils.deleteAllOfTable(PaymentRepository.TABLE_NAME, BuyerRepository.TABLE_NAME);
    }

    @Before
    public void before() {
        connectionWrapper  = TestUtils.getConnectionWrapper();
        buyerRepository = new BuyerRepository(connectionWrapper);
    }

    @Test
    public void test1_ShouldReturnEmptyBuyer() throws SQLException {
        assertTrue(buyerRepository.getBuyers(TestUtils.QUERY_LIMIT).isEmpty());
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
    public void test3_ShouldReturnOneBuyer() throws SQLException {
        List<Buyer> buyers = buyerRepository.getBuyers(TestUtils.QUERY_LIMIT);
        assertEquals(1, buyers.size());
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buyers.get(0));
    }
    
    @Test
    public void test3_ShouldReturnBuyerByCPFAndById() throws SQLException {
        Optional<Buyer> buy = buyerRepository.getWithCPF(TestUtils.BUYER_1_CPF);
        assertTrue(buy.isPresent());
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buy.get());
        buy = buyerRepository.getWithId(buy.get().getId());
        assertTrue(buy.isPresent());
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buy.get());
    }

    @Test
    public void test4_ShouldReturnBuyerSearchByName() throws SQLException {
        String partialName = TestUtils.BUYER_1_NAME.substring(0, TestUtils.BUYER_1_NAME.length() / 2);
        List<Buyer> buyers = buyerRepository.search(new SearchConditions().put(BuyerRepository.COLUMN_NAME, partialName), TestUtils.QUERY_LIMIT);
        assertEquals(1, buyers.size());
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buyers.get(0));
    }

    @Test
    public void test5_ShouldReturnBuyerSearchByCPF() throws SQLException {
        String partialName = TestUtils.BUYER_1_CPF.substring(0, TestUtils.BUYER_1_CPF.length() / 2);
        List<Buyer> buyers = buyerRepository.search(new SearchConditions().put(BuyerRepository.COLUMN_CPF, partialName), TestUtils.QUERY_LIMIT);
        assertEquals(1, buyers.size());
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buyers.get(0));
    }

    @Test
    public void test6_ShouldReturnBuyerSearchByEmail() throws SQLException {
        String partialName = TestUtils.BUYER_1_EMAIL.substring(0, TestUtils.BUYER_1_EMAIL.length() / 2);
        List<Buyer> buyers = buyerRepository.search(new SearchConditions().put(BuyerRepository.COLUMN_EMAIL, partialName), TestUtils.QUERY_LIMIT);
        assertEquals(1, buyers.size());
        TestUtils.assertAllFields(TestUtils.getBuyerOne(), buyers.get(0));
    }

    @Test
    public void test7_ShouldInsertBuyer() throws SQLException {
        connectionWrapper  = TestUtils.getTransactionConnectionWrapper();
        buyerRepository = new BuyerRepository(connectionWrapper);
        buyerRepository.insert(TestUtils.getBuyerTwo());
        connectionWrapper.executeBatchStatements();
        connectionWrapper.getConnection().commit();
    }

    @Test
    public void test8_ShoulReturnTwoBuyers() throws SQLException {
        List<Buyer> buyers = buyerRepository.getBuyers(TestUtils.QUERY_LIMIT);
        assertEquals(2, buyers.size());
        TestUtils.assertAllFields(Arrays.asList(TestUtils.getBuyerOne(), TestUtils.getBuyerTwo()), buyers);
    }

    @Test
    public void test9_ShouldReturnTwoBuyersSearchByName() throws SQLException {
        String partialName = TestUtils.BUYER_1_NAME.substring(0, TestUtils.BUYER_1_NAME.length() / 2);
        List<Buyer> buyers = buyerRepository.search(new SearchConditions().put(BuyerRepository.COLUMN_NAME, partialName), TestUtils.QUERY_LIMIT);
        assertEquals(2, buyers.size());
        TestUtils.assertAllFields(Arrays.asList(TestUtils.getBuyerOne(), TestUtils.getBuyerTwo()), buyers);
    }

    @Test
    public void test9_ShouldReturnTwoBuyersSearchByCPF() throws SQLException {
        String partialName = TestUtils.BUYER_1_CPF.substring(0, TestUtils.BUYER_1_CPF.length() / 2);
        List<Buyer> buyers = buyerRepository.search(new SearchConditions().put(BuyerRepository.COLUMN_CPF, partialName), TestUtils.QUERY_LIMIT);
        assertEquals(2, buyers.size());
        TestUtils.assertAllFields(Arrays.asList(TestUtils.getBuyerOne(), TestUtils.getBuyerTwo()), buyers);
    }

    @Test
    public void test9_ShouldReturnTwoBuyersSearchByEmail() throws SQLException {
        String partialName = TestUtils.BUYER_1_EMAIL.substring(0, 3);
        List<Buyer> buyers = buyerRepository.search(new SearchConditions().put(BuyerRepository.COLUMN_EMAIL, partialName), TestUtils.QUERY_LIMIT);
        assertEquals(2, buyers.size());
        TestUtils.assertAllFields(Arrays.asList(TestUtils.getBuyerOne(), TestUtils.getBuyerTwo()), buyers);
    }
}