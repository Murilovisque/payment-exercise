package com.payment.checkout.resources.models;

import java.time.LocalDate;

@JSONRequiredFieldsAnnotation
public class CardJSON {

    private String holderName;
    private String number;
    private LocalDate expirationDate;
    private String cvv;

    public String getHolderName() {
        return holderName;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getCvv() {
        return cvv;
    }
}