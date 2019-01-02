package com.payment.api.models;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.gson.annotations.Expose;

public class Card {
    @Expose(serialize=false) //TODO: test it
    private UUID id;
    private String holderName;
    private String number;
    private LocalDate expirationDate;
    private String cvv;

    public Card(String holderName, String number, LocalDate expirationDate, String cvv) {
        this.holderName = holderName;
        this.number = number;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
    }

    public Card(UUID id, String holderName, String number, LocalDate expirationDate, String cvv) {
        this(holderName, number, expirationDate, cvv);
        this.id = id;
    }

    public void normalizeExpirationDate() {
        expirationDate = expirationDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public String getHolderName() {
        return holderName;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getCvv() {
        return cvv;
    }

    public UUID getId() {
        return id;
    }

    public enum Brand {
        MASTERCARD(toSet(concatenate(range(51, 55), range(222100, 272099))), of(16)),
        AMERICAN_EXPRESS(of("34", "37"), of(15)),
        VISA(of("4"), of(13, 16, 19)),
        MAESTRO(of("5018", "5020", "5038", "5893", "6304", "6759", "6761", "6762", "6763"), of(16, 17, 18, 19)),
        DISCOVER(toSet(concatenate(range(622126, 622925), new String[]{"644", "645", "646", "647", "648", "649", "65", "6011"})), of(16, 17, 18, 19));

        private Set<String> issuerIdentificationNumbers;
        private Set<Integer> acceptLengh;

        Brand(Set<String> issuerIdentificationNumbers, Set<Integer> acceptLengh) {
            this.issuerIdentificationNumbers = issuerIdentificationNumbers;
            this.acceptLengh = acceptLengh;
        }

        public Set<String> getIssuerIdentificationNumbers() {
            return issuerIdentificationNumbers;
        }

        public Set<Integer> getAcceptLengh() {
            return acceptLengh;
        }

        private static String[] range(int ini, int end) {
            ArrayList<String> range = new ArrayList<>();
            for (int i = ini; i <= end; i++)
                range.add(Integer.toString(i));
            return range.toArray(new String[]{});
        }

        @SuppressWarnings("unchecked")
        private static <T> Set<T> of(T... values) {
            Set<T> newSet = new HashSet<>();
            for (T v : values)
                newSet.add(v);
            return newSet;
        }

        private static <T> T[] concatenate(T[] a, T[] b) {
            int aLen = a.length;
            int bLen = b.length;

            @SuppressWarnings("unchecked")
            T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
            System.arraycopy(a, 0, c, 0, aLen);
            System.arraycopy(b, 0, c, aLen, bLen);        
            return c;
        }

        private static <T> Set<T> toSet(T[] arr) {
            return new HashSet<>(Arrays.asList(arr));
        }
    }
}