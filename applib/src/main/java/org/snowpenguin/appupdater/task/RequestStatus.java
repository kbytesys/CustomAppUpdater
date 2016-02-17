package org.snowpenguin.appupdater.task;

public enum RequestStatus {
    REQUEST,
    ERROR,
    ERROR_INVALID_URL,
    IDLE, SUCCESS,
    SAME_VERSION,
    DIFFERENT_VERSION
}
