package org.snowpenguin.org.appupdater.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class UpdateService extends IntentService {
    public static final String ACTION_CHECK_NEW_RELEASE = "org.snowpenguin.org.appupdater.action.checknewrelease";
    public static final String EXTRA_URL = "org.snowpenguin.org.appupdater.extra.url";
    public static final String EXTRA_VERSION = "org.snowpenguin.org.appupdater.extra.version";
    private RequestResult result = new RequestResult(RequestStatus.IDLE, "");

    private final IBinder mBinder = new LocalBinder();
    private OkHttpClient client = new OkHttpClient();

    public class LocalBinder extends Binder {
        public UpdateService getService() {
            // Return this instance of LocalService so clients can call public methods
            return UpdateService.this;
        }
    }

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECK_NEW_RELEASE.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                final String version = intent.getStringExtra(EXTRA_VERSION);
                handleCheckNewRelease(url, version);
            }
        }
    }

    public void handleCheckNewRelease(String url, String version) {

        result.setStatus(RequestStatus.REQUEST);
        result.setMessage("");
        Request request = new Request.Builder()
        .url(url)
        .build();
        try {
            Response response = client.newCall(request).execute();
            response.body().string();
        } catch (IOException e) {
            result.setStatus(RequestStatus.ERROR);
            result.setMessage(e.getMessage());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public void checkUpdate(String url, String version) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                return null;
            }


        };
    }

}
