package com.payment.api.exceptions;

public class PaymentException extends Exception {

    private static final long serialVersionUID = 1L;

    public PaymentException(Exception e) {
        super(e);
    }

    public PaymentException(String msg) {
        super(msg);
    }
}