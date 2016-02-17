package org.snowpenguin.appupdater.task;

public enum RequestStatus {
    ERROR,
    ERROR_INVALID_URL,
    SUCCESS,
    SAME_VERSION,
    DIFFERENT_VERSION,
    DOWNLOAD_ERROR
}
