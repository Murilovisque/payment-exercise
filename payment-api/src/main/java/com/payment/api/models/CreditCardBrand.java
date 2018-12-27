package com.payment.api.models;

import java.util.Set;
import java.util.HashSet;

//TODO: Validar bandeiras
public enum CreditCardBrand {

    MASTERCARD(of("222100", "272099"), of(16)),
    AMERICAN_EXPRESS(new HashSet<>(), new HashSet<>()),
    VISA(new HashSet<>(), new HashSet<>()),
    MAESTRO(new HashSet<>(), new HashSet<>()),;

    CreditCardBrand(Set<String> IssuerIdentificationNumbers, Set<Integer> acceptLengh) {
        
    }

    private static <T> Set<T> of(T... values) {
        Set<T> newSet = new HashSet<>();
        for (T v : values)
            newSet.add(v);
        return newSet;
    }
}