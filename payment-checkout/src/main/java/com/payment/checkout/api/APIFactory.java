package com.payment.checkout.api;

import com.payment.api.BuyerAPI;
import com.payment.api.FormOfPaymentAPI;
import com.payment.api.PaymentAPI;
import com.payment.checkout.PaymentCheckoutConfig;
import com.payment.checkout.database.ConnectionPool;

public class APIFactory {

    public static PaymentAPI getPaymentAPI() {//TODO: FIX
        return new PaymentAPI(() -> PaymentCheckoutConfig.getApiClientId(), ConnectionPool.getInstance());
    }

    public static BuyerAPI getBuyerAPI() {
        return new BuyerAPI(() -> PaymentCheckoutConfig.getApiClientId(), ConnectionPool.getInstance());
    }

    public static FormOfPaymentAPI getFormOfPaymentAPI() {
        return new FormOfPaymentAPI(() -> PaymentCheckoutConfig.getApiClientId(), ConnectionPool.getInstance());
    }
} 