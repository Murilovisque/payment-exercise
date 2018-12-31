package com.payment.api.models;

import java.util.UUID;

public class Boleto {

    private UUID id;
    private String number;

    public Boleto(String number) {
        this.number = number;
    }

    public Boleto(UUID id, String number) {
        this(number);
        this.id = id;
	}

	public UUID getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }
}