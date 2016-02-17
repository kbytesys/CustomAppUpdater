package org.snowpenguin.org.appupdater.service;

public class RequestResult {
    private String message;
    private RequestStatus status;
    private String version;
    private String url;
    private String appName;

    public RequestResult(RequestStatus status) {
        this.status = status;
    }

    public RequestResult(RequestStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppName() {
        return appName;
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

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
