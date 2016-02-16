package org.snowpenguin.org.appupdater.service;

import org.snowpenguin.org.appupdater.service.task.RequestProgress;

public interface IRequestObserver {
    void notifyResult(RequestResult requestResult);
    void notifyProgress(RequestProgress[] values);
    void notifyCancelled(RequestResult requestResult);
    void notifyCancelled();
}
