package com.payment.api.models;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;

public class Card {
    @Expose(serialize=false) //TODO: test it
    private Long id;
    private String holderName;
    private String number;
    private LocalDate expirationDate;
    private String cvv;

    public Card(String holderName, String number, LocalDate expirationDate, String cvv) {
        this.holderName = holderName;
        this.number = number;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
    }

    public Card(Long id, String holderName, String number, LocalDate expirationDate, String cvv) {
        this(holderName, number, expirationDate, cvv);
        this.id = id;
    }

    public void normalizeExpirationDate() {
        expirationDate = expirationDate.plusMonths(1).minusDays(1);
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

    public Long getId() {
        return id;
    }
}