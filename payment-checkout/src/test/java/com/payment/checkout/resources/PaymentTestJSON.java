package com.payment.checkout.resources;

import java.math.BigDecimal;
import java.util.UUID;

import com.payment.api.models.Payment;

public class PaymentTestJSON {

    private BoletoTestJSON boleto;
    private UUID id;
    private BigDecimal amount;
    private Payment.Status status;
    private CardTestJSON card;

    public PaymentTestJSON() {}

    public static PaymentTestJSON newAsBoleto(BigDecimal amount, BoletoTestJSON boleto) {
        PaymentTestJSON paymentBoleto = new PaymentTestJSON();
        paymentBoleto.amount = amount;
        paymentBoleto.boleto = boleto;
        paymentBoleto.status = Payment.Status.GENERATED;
        return paymentBoleto;
    }

    public static PaymentTestJSON newAsCard(BigDecimal amount, CardTestJSON card) {
        PaymentTestJSON paymentBoleto = new PaymentTestJSON();
        paymentBoleto.amount = amount;
        paymentBoleto.card = card;
        paymentBoleto.status = Payment.Status.GENERATED;
        return paymentBoleto;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BoletoTestJSON getBoleto() {
        return boleto;
    }

    public UUID getId() {
        return id;
    }

    public Payment.Status getStatus() {
        return status;
    }

    public CardTestJSON getCard() {
        return card;
    }

    public boolean isBoleto() {
        return boleto != null;
    }

    public boolean isCard() {
        return card != null;
    }
}