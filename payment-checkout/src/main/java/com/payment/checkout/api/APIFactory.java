package com.payment.checkout.api;

import com.payment.api.BuyerAPI;
import com.payment.api.FormOfPaymentAPI;
import com.payment.api.PaymentAPI;
import com.payment.checkout.database.ConnectionPool;

public class APIFactory {

    public static PaymentAPI getPaymentAPI() {//TODO: FIX
        return new PaymentAPI(() -> 1L, ConnectionPool.getInstance());
    }

    public static BuyerAPI getBuyerAPI() {
        return new BuyerAPI(() -> 1L, ConnectionPool.getInstance());
    }

    public static FormOfPaymentAPI getFormOfPaymentAPI() {
        return new FormOfPaymentAPI(() -> 1L, ConnectionPool.getInstance());
    }
} 