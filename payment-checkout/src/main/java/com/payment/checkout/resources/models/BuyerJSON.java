package com.payment.checkout.resources.models;

@JSONRequiredFieldsAnnotation
public class BuyerJSON {

    private String name;    
    private String cpf;    
    private String email;

    public BuyerJSON() {}

    public BuyerJSON(String name, String cpf, String email) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
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
}