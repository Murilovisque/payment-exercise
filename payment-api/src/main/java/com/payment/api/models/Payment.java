package com.payment.api.models;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class Payment {

    private UUID id;
    private BigDecimal amount;
    private Status status;

    public Payment(BigDecimal amount, Status status) {
        this.amount = normalizeAmount(amount);
        this.status = status;
    }

    public Payment(UUID id, BigDecimal amount, Status status) {
        this(amount, status);
        this.id = id;        
    }

    public abstract Type getType();

    public BigDecimal getAmount() {
        return amount;
    }

    public UUID getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public enum Type {
        BOLETO,
        CREDIT_CARD;

        public static Type get(String type) {
            for (Type t : values())
                if (t.name().equals(type))
                    return t;
            return null;
        }
    }

    public enum Status {
        GENERATED,
        PAID;

        public static Status get(String type) {
            for (Status s : values())
                if (s.name().equals(type))
                    return s;
            return null;
        }
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }
}