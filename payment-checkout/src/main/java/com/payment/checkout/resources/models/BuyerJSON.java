package com.payment.checkout.resources.models;

@JSONRequiredFieldsAnnotation
public class BuyerJSON {

    private String name;    
    private String cpf;    
    private String email;

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