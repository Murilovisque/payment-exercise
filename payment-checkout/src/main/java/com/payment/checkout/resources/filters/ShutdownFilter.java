package com.payment.checkout.resources.filters;

import com.payment.checkout.HTTPUtils;
import com.payment.checkout.PaymentCheckout;

import spark.Spark;

public class ShutdownFilter {

	public static void registerBeforeFilter() {
		Spark.before((request, response) -> {
			if(PaymentCheckout.getInstance().isShutdownSignalReceived())
				Spark.halt(HTTPUtils.SERVICE_UNAVAILABLE, "Service unavailable, try again later!");
        });
	}

}