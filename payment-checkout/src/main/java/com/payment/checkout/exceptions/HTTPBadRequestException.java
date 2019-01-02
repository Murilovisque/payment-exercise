package com.payment.checkout.exceptions;

import com.payment.checkout.HTTPUtils;

public class HTTPBadRequestException extends HTTPRequestException {

    private static final long serialVersionUID = 1L;

    public HTTPBadRequestException(String msg) {
        super(HTTPUtils.HTTP_STATUS_BAD_REQUEST, msg);
    }

}