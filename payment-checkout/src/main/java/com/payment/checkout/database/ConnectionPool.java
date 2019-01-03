package com.payment.checkout.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.payment.api.repositories.ConnectionProvider;
import com.payment.checkout.exceptions.InitializationPaymenCheckoutException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPool implements ConnectionProvider {

    private static final ConnectionPool instance = new ConnectionPool();

    HikariDataSource dataSource;

    private ConnectionPool() {}

    public void build() throws InitializationPaymenCheckoutException {
        DatabaseConfig.load();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DatabaseConfig.getJdbcProperties().get(DatabaseConfig.ConfigProperties.JDBC_URL.getPropertyName()));
        config.setUsername(DatabaseConfig.getJdbcProperties().get(DatabaseConfig.ConfigProperties.USER.getPropertyName()));
        config.setPassword(DatabaseConfig.getJdbcProperties().get(DatabaseConfig.ConfigProperties.PASSWORD.getPropertyName()));
        DatabaseConfig.getDatasourceProperties().forEach((k, v) -> config.addDataSourceProperty(k, v));
        config.addDataSourceProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        dataSource = new HikariDataSource(config);
    }

    public static ConnectionPool getInstance() {
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        dataSource.close();
    }
}