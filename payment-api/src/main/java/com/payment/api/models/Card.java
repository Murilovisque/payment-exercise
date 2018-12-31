package com.payment.api.models;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;

import com.google.gson.annotations.Expose;

public class Card {
    @Expose(serialize=false) //TODO: test it
    private UUID id;
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

    public Card(UUID id, String holderName, String number, LocalDate expirationDate, String cvv) {
        this(holderName, number, expirationDate, cvv);
        this.id = id;
    }

    public void normalizeExpirationDate() {
        expirationDate = expirationDate.with(TemporalAdjusters.lastDayOfMonth());
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

    public UUID getId() {
        return id;
    }
}