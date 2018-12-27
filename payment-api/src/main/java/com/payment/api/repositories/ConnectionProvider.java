package com.payment.api.repositories;

import java.sql.Connection;

public interface ConnectionProvider {
    Connection getConnection();
}