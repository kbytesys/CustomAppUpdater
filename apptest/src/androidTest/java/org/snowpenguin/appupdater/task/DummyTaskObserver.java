package org.snowpenguin.appupdater.task;

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
