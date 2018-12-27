package com.payment.api.exceptions;

public class UnauthorizedPaymentException extends PaymentException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedPaymentException(Exception e) {
        super(e);
    }

    public UnauthorizedPaymentException(String msg) {
        super(msg);
    }
}