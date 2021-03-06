package com.payment.checkout.resources.models;

import java.math.BigDecimal;

@JSONRequiredFieldsAnnotation
public class BoletoJSON {
    
    private BigDecimal amount;
    private BuyerJSON buyer;

    public BoletoJSON() {}

    public BoletoJSON(BigDecimal amount, BuyerJSON buyer) {
        this.amount = amount;
        this.buyer = buyer;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BuyerJSON getBuyer() {
        return buyer;
    }
}