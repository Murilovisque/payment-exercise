package com.payment.checkout.exceptions;

public class InitializationPaymenCheckoutException extends Exception {

    private static final long serialVersionUID = 1L;

    public InitializationPaymenCheckoutException(String msg) {
        super(msg);
    }

    public InitializationPaymenCheckoutException(String msg, Exception exception) {
        super(msg, exception);
    }
}