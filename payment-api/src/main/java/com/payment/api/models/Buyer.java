package com.payment.api.models;

public class Buyer {
    private Long id;
    private String name;
    private String cpf;
    private String email;

    public Buyer(String name, String cpf, String email) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
	}

    public Buyer(Long id, String name, String cpf, String email) {
        this(name, cpf, email);
        this.id = id;
    }   

	public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }
}