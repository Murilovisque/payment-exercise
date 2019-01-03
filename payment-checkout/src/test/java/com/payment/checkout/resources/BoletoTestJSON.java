package com.payment.checkout.resources;

import java.util.UUID;

public class BoletoTestJSON {

    private UUID id;
    private String number;

    public BoletoTestJSON() {}
    
    public BoletoTestJSON(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public UUID getId() {
        return id;
    }
}