package com.payment.checkout;

import java.util.UUID;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.payment.api.exceptions.PaymentException;
import com.payment.api.search.SearchConditions;
import com.payment.checkout.exceptions.HTTPBadRequestException;
import com.payment.checkout.exceptions.HTTPRequestException;
import com.payment.checkout.resources.models.BoletoJSON;
import com.payment.checkout.resources.models.BuyerJSON;
import com.payment.checkout.resources.models.CardJSON;
import com.payment.checkout.resources.models.CreditCardPaymentJSON;
import com.payment.checkout.resources.models.JSONRequiredFieldsDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;

public class HTTPUtils {
    
    public static final int HTTP_STATUS_CREATED = 201;
    public static final int HTTP_STATUS_BAD_REQUEST = 400;
    public static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;
    public static final int HTTP_STATUS_UNAUTHORIZED = 401;
    public static final int HTTP_STATUS_NOT_FOUND = 404;
    public static final int SERVICE_UNAVAILABLE = 503;
    private static final Logger logger = LoggerFactory.getLogger(HTTPUtils.class);
    private static final Pattern REGEX_NUMBER = Pattern.compile("\\d+");
    private static final String LIMIT_QUERY_PARAM = "limit";

    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(BuyerJSON.class, new JSONRequiredFieldsDeserializer<BuyerJSON>())
        .registerTypeAdapter(CardJSON.class, new JSONRequiredFieldsDeserializer<CardJSON>())
        .registerTypeAdapter(BoletoJSON.class, new JSONRequiredFieldsDeserializer<BoletoJSON>())
        .registerTypeAdapter(CreditCardPaymentJSON.class, new JSONRequiredFieldsDeserializer<CreditCardPaymentJSON>())
        .create();

    public static String setResponse(Response res, int status, String body) {
        res.status(status);
        return body;
    }

    public static String setResponse(Response res, int status) {
        return setResponse(res, status, "");
    }

    public static <T> T parseJson(String json, Class<T> cls) throws HTTPBadRequestException {
        try {
            return gson.fromJson(json, cls);
        } catch (JsonParseException e) {
            throw new HTTPBadRequestException("Formato do corpo da requisição inválida");
        }
    }

    public static UUID parseUUID(String param, HTTPRequestException e) throws HTTPRequestException {
        try {
            return UUID.fromString(param);
        } catch (IllegalArgumentException ex) {
            throw e;
        }
    }

    public static SearchConditions buildSearchFromRequest(Request req) {
        SearchConditions search = new SearchConditions();
        for (String s : req.queryParams())
            if (!s.equals(LIMIT_QUERY_PARAM))                
                search.put(s, req.queryParams(s));
        return search;
    }

    public static String handleException(Exception e, Response res) {
        if (e instanceof PaymentException)
            return handleException((PaymentException) e, res);
        logger.error("Unknown exception", e);
        return setResponse(res, HTTP_STATUS_INTERNAL_SERVER_ERROR, "");
    }

    public static String handleException(PaymentException e, Response res) {
        logger.error("Internal error in Payment-API", e);
        return setResponse(res, HTTP_STATUS_INTERNAL_SERVER_ERROR, "");
    }

    public static int getLimitRetrieveData(Request req) {
        String limit = req.queryParams(LIMIT_QUERY_PARAM);
        if (limit != null && REGEX_NUMBER.matcher(limit).matches())
            return Integer.valueOf(limit);
        return PaymentCheckoutConfig.getDefaultLimitRetrieveData();
    }
}