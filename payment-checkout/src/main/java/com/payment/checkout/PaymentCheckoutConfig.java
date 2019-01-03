package com.payment.checkout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import com.payment.checkout.exceptions.InitializationPaymenCheckoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentCheckoutConfig {

    public static final String CONFIG_PATH_PROPERTY = "payment.checkout.config.path";
    private static final Logger logger = LoggerFactory.getLogger(PaymentCheckoutConfig.class);    
    private static Long apiClientId;
    private static Integer defaultLimitRetrieveData;
    private static Integer applicationPort;

    public static void load() throws InitializationPaymenCheckoutException {
        try {
            String file = System.getProperty(CONFIG_PATH_PROPERTY);
            logger.info("Trying to load the file {}", file);
            Properties properties = new Properties();
            properties.load(Files.newInputStream(Paths.get(file)));
            for (ConfigProperties config : ConfigProperties.values()) {
                String value = properties.getProperty(config.getPropertyName());
                if (value == null || !value.matches("\\d+"))
                    throw new InitializationPaymenCheckoutException(String.format("Property '%s' must be filled ", config.getPropertyName()));
            }
            apiClientId = Long.valueOf(properties.getProperty(ConfigProperties.API_CLIENT_ID.getPropertyName()));
            defaultLimitRetrieveData = Integer.valueOf(properties.getProperty(ConfigProperties.DEFAULT_LIMIT_RETRIEVE_DATA.getPropertyName()));
            applicationPort = Integer.valueOf(properties.getProperty(ConfigProperties.APPLICATION_PORT.getPropertyName()));
            logger.info("Loaded");
        } catch (IOException e) {
            throw new InitializationPaymenCheckoutException("Database configuration failed", e);
        }
    }

    public static Long getApiClientId() {
        return apiClientId;
    }

    public static Integer getDefaultLimitRetrieveData() {
        return defaultLimitRetrieveData;
    }

    public static Integer getApplicationPort() {
        return applicationPort;
    }

    public enum ConfigProperties {
        API_CLIENT_ID("apiClientId"),
        DEFAULT_LIMIT_RETRIEVE_DATA("defaultLimitRetrieveData"),
        APPLICATION_PORT("applicationPort");

        private String propertyName;

        ConfigProperties(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }
    }    
}