package com.payment.checkout.exceptions;

public abstract class HTTPRequestException extends Exception {

    private static final long serialVersionUID = 1L;

    private int statusResponse;

    public HTTPRequestException(int statusResponse, String msg) {
        super(msg);
        this.statusResponse = statusResponse;
    }

    public HTTPRequestException(int statusResponse) {
        super("");
        this.statusResponse = statusResponse;
    }

    public int getStatusResponse() {
        return statusResponse;
    }

}