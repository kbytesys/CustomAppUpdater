package org.snowpenguin.org.appupdater.service.task;

import android.os.Bundle;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.snowpenguin.org.appupdater.service.IRequestObserver;
import org.snowpenguin.org.appupdater.service.RequestResult;
import org.snowpenguin.org.appupdater.service.RequestStatus;

import java.io.IOException;

/**
 * Created by kbyte on 16/02/2016.
 */
public class CheckUpdateAsyncTask extends ServiceAsyncTask {
    private final String url;
    private final String version;

    public CheckUpdateAsyncTask(IRequestObserver observer, String url, String version) {
        super(observer);
        this.url = url;
        this.version = version;
    }

    @Override
    protected RequestResult doInBackground(Void... params) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String text = response.body().string();
            return new RequestResult(RequestStatus.SUCCESS, text);
        } catch (IOException e) {
            return new RequestResult(RequestStatus.ERROR, e.getMessage());
        }
    }
}
