package org.snowpenguin.org.appupdater.service;

/**
 * Created by kbyte on 16/02/2016.
 */
public class RequestResult {
    private String message;
    private RequestStatus status;

    public RequestResult(RequestStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
