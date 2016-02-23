package org.snowpenguin.appupdater.task;

public interface IRequestObserver {
    void notifyStarted();
    void notifyResult(RequestResult requestResult);
    void notifyProgress(RequestProgress[] values);
    void notifyCancelled(RequestResult requestResult);
    void notifyCancelled();
}
