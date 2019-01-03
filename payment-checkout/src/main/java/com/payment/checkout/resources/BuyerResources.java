package com.payment.checkout.resources;

import static com.payment.checkout.HTTPUtils.HTTP_STATUS_NOT_FOUND;
import static com.payment.checkout.HTTPUtils.buildSearchFromRequest;
import static com.payment.checkout.HTTPUtils.handleException;
import static com.payment.checkout.HTTPUtils.parseUUID;
import static com.payment.checkout.HTTPUtils.setResponse;
import static com.payment.checkout.HTTPUtils.getLimitRetrieveData;
import static spark.Spark.get;
import static spark.Spark.path;

import java.util.Optional;
import java.util.UUID;

import com.google.gson.Gson;
import com.payment.api.models.Buyer;
import com.payment.checkout.api.APIFactory;
import com.payment.checkout.exceptions.HTTPNotFoundRequestException;

public class BuyerResources {

    private static final Gson gson = new Gson();
    private static final String BUYER_ID_RESOURCE_PARAM = "buyer_id";

    public static void releaseHttpResources() {
        path("/payment-checkout/api/v1/buyers", () -> {
            get("", (req, res) -> {
                try {
                    return gson.toJson(APIFactory.getBuyerAPI().search(buildSearchFromRequest(req), getLimitRetrieveData(req)));
                } catch (Exception e) {
                    return handleException(e, res);
                }
            });
            path("/:buyer_id", () -> {
                get("", (req, res) -> {
                    try {
                        UUID id = parseUUID(req.params(BUYER_ID_RESOURCE_PARAM), new HTTPNotFoundRequestException());
                        Optional<Buyer> buyer = APIFactory.getBuyerAPI().getBuyerWithId(id);
                        return buyer.isPresent() ? gson.toJson(buyer.get()) : setResponse(res, HTTP_STATUS_NOT_FOUND);
                    } catch (Exception e) {
                        return handleException(e, res);
                    }
                });
                get("/payments", (req, res) -> {
                    try {
                        UUID id = parseUUID(req.params(BUYER_ID_RESOURCE_PARAM), new HTTPNotFoundRequestException());
                        return gson.toJson(APIFactory.getPaymentAPI().getPaymentsOfBuyer(id));
                    } catch (Exception e) {
                        return handleException(e, res);
                    }
                });
            });
            
        });
    }
}