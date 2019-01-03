package com.payment.checkout.resources.models;

import java.math.BigDecimal;

@JSONRequiredFieldsAnnotation
public class CreditCardPaymentJSON {

    private BigDecimal amount;  
    private BuyerJSON buyer;
    private CardJSON card;

    public CreditCardPaymentJSON() {}

    public CreditCardPaymentJSON(BigDecimal amount, BuyerJSON buyer, CardJSON card) {
        this.amount = amount;
        this.buyer = buyer;
        this.card = card;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BuyerJSON getBuyer() {
        return buyer;
    }

    public CardJSON getCard() {
        return card;
    }
}