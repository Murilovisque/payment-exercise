package com.payment.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;

public class PaymentAPITest {

    @Test
    public void test() throws Exception {
        Connection conn = TestUtils.getTransactionConnectionWrapper().getConnection();
        PreparedStatement stmt = conn.prepareStatement("insert into api_client (name) values (?)");
        stmt.setString(1, "mano");
        stmt.addBatch();
        ResultSet rs = conn.createStatement().executeQuery("select * from api_client");
        while (rs.next()) {
            String s = rs.getString("name");
            System.out.println(s);
        }
    }
}