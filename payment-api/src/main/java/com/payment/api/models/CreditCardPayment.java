package com.payment.api.models;

import java.math.BigDecimal;
import java.util.UUID;

public class CreditCardPayment extends Payment {

    private Card card;

    public CreditCardPayment(BigDecimal amount, Card card) {
        super(amount, Payment.Status.PAID);
        this.card = card;
    }

    public CreditCardPayment(UUID id, BigDecimal amount, Status status, Card card) {
        super(id, amount, status);
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    @Override
    public Type getType() {
        return Type.CREDIT_CARD;
    }

}