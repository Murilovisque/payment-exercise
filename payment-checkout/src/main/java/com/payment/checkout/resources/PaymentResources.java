package com.payment.checkout.resources;

import static com.payment.checkout.HTTPUtils.HTTP_STATUS_CREATED;
import static com.payment.checkout.HTTPUtils.HTTP_STATUS_NOT_FOUND;
import static com.payment.checkout.HTTPUtils.HTTP_STATUS_UNAUTHORIZED;
import static com.payment.checkout.HTTPUtils.handleException;
import static com.payment.checkout.HTTPUtils.parseJson;
import static com.payment.checkout.HTTPUtils.parseUUID;
import static com.payment.checkout.HTTPUtils.setResponse;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;

import java.util.Optional;
import java.util.UUID;

import com.google.gson.Gson;
import com.payment.api.exceptions.PaymentException;
import com.payment.api.exceptions.UnauthorizedPaymentException;
import com.payment.api.models.BoletoPayment;
import com.payment.api.models.Buyer;
import com.payment.api.models.Card;
import com.payment.api.models.CreditCardPayment;
import com.payment.api.models.Payment;
import com.payment.checkout.api.APIFactory;
import com.payment.checkout.exceptions.HTTPNotFoundRequestException;
import com.payment.checkout.exceptions.HTTPRequestException;
import com.payment.checkout.resources.models.BoletoJSON;
import com.payment.checkout.resources.models.BuyerJSON;
import com.payment.checkout.resources.models.CardJSON;
import com.payment.checkout.resources.models.CreditCardPaymentJSON;

import spark.Request;
import spark.Response;

public class PaymentResources {

    private static final Gson gson = new Gson();
    private static final String PAYMENT_ID_RESOURCE_PARAM = "payment_id";

    public static void releaseHttpResources() {
        path("/payment-checkout/api/v1/payments", () -> {
            get("", (req, res) -> {
                try {
                    return gson.toJson(APIFactory.getPaymentAPI().getPayments(50));
                } catch (Exception e) {
                    return handleException(e, res);
                }
            });
            post("/credit-cards", (req, res) -> {
                try {
                    CreditCardPaymentJSON paymentJson = parseJson(req.body(), CreditCardPaymentJSON.class);
                    CardJSON cardJson = paymentJson.getCard();
                    BuyerJSON buyerJson = paymentJson.getBuyer();
                    Buyer buyer = new Buyer(buyerJson.getName(), buyerJson.getCpf(), buyerJson.getEmail());
                    CreditCardPayment payment = new CreditCardPayment(paymentJson.getAmount(),
                            new Card(cardJson.getHolderName(), cardJson.getNumber(), cardJson.getExpirationDate(),
                                    cardJson.getCvv()));
                    return processPayment(req, res, buyer, payment);
                } catch (HTTPRequestException ex) {
                    return setResponse(res, ex.getStatusResponse(), ex.getMessage());
                } catch (Exception e) {
                    return handleException(e, res);
                }
            });
            post("/boletos", (req, res) -> {
                try {
                    BoletoJSON paymentJson = parseJson(req.body(), BoletoJSON.class);
                    BuyerJSON buyerJson = paymentJson.getBuyer();
                    Buyer buyer = new Buyer(buyerJson.getName(), buyerJson.getCpf(), buyerJson.getEmail());
                    BoletoPayment payment = new BoletoPayment(paymentJson.getAmount());
                    return processPayment(req, res, buyer, payment);
                } catch (HTTPRequestException ex) {
                    return setResponse(res, ex.getStatusResponse(), ex.getMessage());
                } catch (Exception e) {
                    return handleException(e, res);
                }
            });
            get("/:payment_id", (req, res) -> {
                try {
                    UUID id = parseUUID(req.params(PAYMENT_ID_RESOURCE_PARAM), new HTTPNotFoundRequestException());
                    Optional<Payment> payment = APIFactory.getPaymentAPI().getPaymentWithId(id);
                    return payment.isPresent() ? gson.toJson(payment.get()) : setResponse(res, HTTP_STATUS_NOT_FOUND);
                } catch (HTTPRequestException ex) {
                    return setResponse(res, ex.getStatusResponse(), ex.getMessage());
                } catch (Exception e) {
                    return handleException(e, res);
                }                
            });
        });
    }

    private static String processPayment(Request req, Response res, Buyer buyer, Payment payment) {
        try {
            UUID id = APIFactory.getPaymentAPI().processPayment(buyer, payment);
            res.status(HTTP_STATUS_CREATED);
            return id.toString();
        } catch(UnauthorizedPaymentException e) {
            return setResponse(res, HTTP_STATUS_UNAUTHORIZED, e.getMessage());
        } catch (PaymentException e) {
            return handleException(e, res);
		} 
    }
}