package org.snowpenguin.org.appupdater.task;

import org.json.JSONException;
import org.json.JSONObject;
import org.snowpenguin.org.appupdater.service.IRequestObserver;
import org.snowpenguin.org.appupdater.service.task.CheckUpdateAsyncTask;

import java.io.IOException;

public class DummyCheckUpdateTask extends CheckUpdateAsyncTask {
    private boolean ioerror;
    private String jsondata;

    public DummyCheckUpdateTask(IRequestObserver observer, String url, String version) {
        super(observer, url, version);
    }

    @Override
    protected JSONObject fetchData(String url) throws IOException, JSONException {
        if(ioerror)
            throw new IOException("Fake IO Error");

        return new JSONObject(jsondata);
    }

    public void setIoError(boolean value)
    {
        ioerror = value;
    }
    
    public void setJsonData(String data) {
        jsondata = data;
    }
}