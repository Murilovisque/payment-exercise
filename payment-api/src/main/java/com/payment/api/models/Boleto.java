package com.payment.api.models;

public class Boleto {

    private Long id;
    private String number;

    public Boleto(long id, String number) {
        this.id = id;
        this.number = number;
	}

	public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }
}