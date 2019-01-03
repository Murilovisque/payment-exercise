package com.payment.api.exceptions;

public class InvalidArgumentPaymentException extends PaymentException {

    private static final long serialVersionUID = 1L;

    public InvalidArgumentPaymentException(Exception e) {
        super(e);
    }

    public InvalidArgumentPaymentException(String msg) {
        super(msg);
    }
}