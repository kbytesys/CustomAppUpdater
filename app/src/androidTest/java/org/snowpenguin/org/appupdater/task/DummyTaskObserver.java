package org.snowpenguin.org.appupdater.task;

import org.snowpenguin.org.appupdater.service.IRequestObserver;
import org.snowpenguin.org.appupdater.service.RequestResult;
import org.snowpenguin.org.appupdater.service.task.RequestProgress;

/**
 * Created by kbyte on 16/02/2016.
 */
public class DummyTaskObserver implements IRequestObserver {

    private RequestResult result = null;
    private boolean cancelled = false;

    @Override
    public void notifyResult(RequestResult requestResult) {
        this.result = requestResult;
    }

    @Override
    public void notifyProgress(RequestProgress[] values) {
        for (RequestProgress rq : values) {
            System.out.print(rq.toString());
        }
    }

    @Override
    public void notifyCancelled(RequestResult requestResult) {
        this.result = requestResult;
        cancelled = true;
    }

    @Override
    public void notifyCancelled() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public RequestResult getResult()
    {
        return result;
    }
}
