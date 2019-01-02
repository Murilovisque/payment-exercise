package com.payment.checkout.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.payment.checkout.exceptions.InitializationPaymenCheckoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DatabaseConfig {

    private static Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String CONFIG_PATH_PROPERTY = "database.config.path";
    private static Map<String, String> jdbcProperties = new HashMap<>();
    private static Map<String, String> datasourceProperties = new HashMap<>();

    public static void load() throws InitializationPaymenCheckoutException {
        try {
            String file = System.getProperty(CONFIG_PATH_PROPERTY);
            logger.info("Trying to load the file {}", file);
            Properties properties = new Properties();
            properties.load(Files.newInputStream(Paths.get(file)));
            for (ConfigProperties config : ConfigProperties.values()) {
                if (properties.getProperty(config.getPropertyName()) == null)
                    throw new InitializationPaymenCheckoutException(String.format("Property '%s' must be filled ", config.getPropertyName()));
                if (isJdbcProperty(config))
                    jdbcProperties.put(config.getPropertyName(), properties.getProperty(config.getPropertyName()));
                else
                    datasourceProperties.put(config.getPropertyName(), properties.getProperty(config.getPropertyName()));
            }

            logger.info("Loaded");
        } catch (IOException e) {
            throw new InitializationPaymenCheckoutException("Database configuration failed", e);
        }        
    }

    public static Map<String, String> getJdbcProperties() {
        return jdbcProperties;
    }

    public static Map<String, String> getDatasourceProperties() {
        return datasourceProperties;
    }

    public enum ConfigProperties {
        JDBC_URL("jdbcUrl"),
        USER("username"),
        PASSWORD("password"),
        CONNECTION_TIMEOUT("connectionTimeout"),
        MINIMUM_IDLE("minimumIdle"),
        MAXIMUM_POOL_SIZE("maximumPoolSize"),
        CACHE_PREP_STMTS("cachePrepStmts"),
        PREP_STMT_CACHE_SIZE("prepStmtCacheSize"),
        PREP_STMT_CACHE_SQLLIMIT("prepStmtCacheSqlLimit");

        private String propertyName;

        ConfigProperties(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }
    }

    private static boolean isJdbcProperty(ConfigProperties property) {
        return property.equals(ConfigProperties.JDBC_URL) || property.equals(ConfigProperties.PASSWORD) ||
            property.equals(ConfigProperties.USER);
    }

}