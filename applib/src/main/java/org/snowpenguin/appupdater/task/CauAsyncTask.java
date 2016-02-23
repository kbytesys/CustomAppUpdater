package org.snowpenguin.appupdater.task;

import android.os.AsyncTask;

public abstract class CauAsyncTask extends AsyncTask<Void, RequestProgress, RequestResult> {
    private final IRequestObserver observer;
    private final boolean notifyOnPostExecute;

    public CauAsyncTask(IRequestObserver observer, boolean notifyOnPostExecute) {
        this.observer = observer;
        this.notifyOnPostExecute = notifyOnPostExecute;
    }

    @Override
    protected void onPreExecute() {
        observer.notifyStarted();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(RequestResult requestResult) {
        if(notifyOnPostExecute) {
            observer.notifyResult(requestResult);
        }
        super.onPostExecute(requestResult);
    }

    @Override
    protected void onProgressUpdate(RequestProgress... values) {
        observer.notifyProgress(values);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(RequestResult requestResult) {
        observer.notifyCancelled(requestResult);
        super.onCancelled(requestResult);
    }

    @Override
    protected void onCancelled() {
        observer.notifyCancelled();
        super.onCancelled();
    }

    protected abstract RequestResult backgroundTask(Void... params);

    protected RequestResult doInBackground(Void... params) {
        RequestResult result = backgroundTask(params);
        if(!notifyOnPostExecute)
            observer.notifyResult(result);
        return result;
    }


}
