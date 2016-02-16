package org.snowpenguin.org.appupdater.service.task;

import android.os.AsyncTask;
import android.os.Bundle;
import org.snowpenguin.org.appupdater.service.IRequestObserver;
import org.snowpenguin.org.appupdater.service.RequestResult;

public abstract class ServiceAsyncTask extends AsyncTask<Void, RequestProgress, RequestResult> {
    private final IRequestObserver observer;

    public ServiceAsyncTask(IRequestObserver observer) {
        this.observer = observer;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(RequestResult requestResult) {
        super.onPostExecute(requestResult);

        observer.notifyResult(requestResult);
    }

    @Override
    protected void onProgressUpdate(RequestProgress... values) {
        super.onProgressUpdate(values);

        observer.notifyProgress(values);
    }

    @Override
    protected void onCancelled(RequestResult requestResult) {
        super.onCancelled(requestResult);

        observer.notifyCancelled(requestResult);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        observer.notifyCancelled();
    }


}
