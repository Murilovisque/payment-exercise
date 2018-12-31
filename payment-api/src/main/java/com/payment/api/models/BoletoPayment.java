package com.payment.api.models;

import java.math.BigDecimal;
import java.util.UUID;

public class BoletoPayment extends Payment {

    private Boleto boleto;

    public BoletoPayment(BigDecimal amount) {
        super(amount, Payment.Status.GENERATED);
    }

    public BoletoPayment(UUID id, BigDecimal amount, Status status, Boleto boleto) {
        super(id, amount, status);
        this.boleto = boleto;
    }

    @Override
    public Type getType() {
        return Type.BOLETO;
    }

    public Boleto getBoleto() {
        return boleto;
    }

}