package com.payment.checkout.exceptions;

import com.payment.checkout.HTTPUtils;

public class HTTPNotFoundRequestException extends HTTPRequestException {

    private static final long serialVersionUID = 1L;

    public HTTPNotFoundRequestException() {
        super(HTTPUtils.HTTP_STATUS_NOT_FOUND);
    }

}