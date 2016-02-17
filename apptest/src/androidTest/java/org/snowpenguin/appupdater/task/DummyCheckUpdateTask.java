package org.snowpenguin.appupdater.task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DummyCheckUpdateTask extends CheckUpdateAsyncTask {
    private boolean ioerror;
    private String jsondata;

    public DummyCheckUpdateTask(IRequestObserver observer, String url, String version) {
        super(observer, url, version);
    }

    @Override
    protected JSONObject fetchData(String url) throws IOException, JSONException, IllegalArgumentException {
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
