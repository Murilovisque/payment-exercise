package com.payment.checkout.resources;

import java.time.LocalDate;
import java.util.UUID;

public class CardTestJSON {
    private UUID id;
    private String holderName;
    private String number;
    private LocalDate expirationDate;
    private String cvv;

    public CardTestJSON() {}

    public CardTestJSON(String holderName, String number, LocalDate expirationDate, String cvv) {
        this.holderName = holderName;
        this.number = number;
        this.expirationDate = expirationDate;
        this.cvv =  cvv;
    }

    public UUID getId() {
        return id;
    }

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