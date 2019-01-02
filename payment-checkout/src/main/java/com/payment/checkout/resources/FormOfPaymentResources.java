package com.payment.checkout.resources;

import static com.payment.checkout.HTTPUtils.HTTP_STATUS_NOT_FOUND;
import static com.payment.checkout.HTTPUtils.handleException;
import static com.payment.checkout.HTTPUtils.setResponse;
import static spark.Spark.get;
import static spark.Spark.path;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.payment.api.models.Card.Brand;
import com.payment.checkout.api.APIFactory;

public class FormOfPaymentResources {

    private static final String CARD_NUMBER_RESOURCE_PARAM = "card_number";
    private static final String BRAND_JSON_FIELD = "brand";
    private static final Gson gson = new Gson();

    public static void releaseHttpResources() {
        path("/payment-checkout/api/v1/cards", () -> {
            get("/brands/:card_number", (req, res) -> {
                try {
                    Optional<Brand> brand = APIFactory.getFormOfPaymentAPI()
                            .validAndGetBrand(req.params(CARD_NUMBER_RESOURCE_PARAM));
                    if (brand.isPresent()) {
                        JsonObject jsonResponse = new JsonObject();
                        jsonResponse.addProperty(BRAND_JSON_FIELD, brand.get().name());
                        return gson.toJson(jsonResponse);
                    } else {
                        return setResponse(res, HTTP_STATUS_NOT_FOUND);
                    }
                } catch (Exception e) {
                    return handleException(e, res);
                }
            });
        });
    }
}