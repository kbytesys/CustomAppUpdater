package org.snowpenguin.appupdater.task;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CheckUpdateAsyncTask extends ServiceAsyncTask {
    private final String url;
    private final String version;

    public CheckUpdateAsyncTask(IRequestObserver observer, String url, String version) {
        super(observer);
        this.url = url;
        this.version = version;
    }

    protected JSONObject fetchData(String url) throws IOException, JSONException, IllegalArgumentException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return new JSONObject(response.body().string());
    }

    @Override
    protected RequestResult doInBackground(Void... params) {

        try {
            JSONObject apk_data = fetchData(url);

            RequestResult result;

            if(apk_data.getString("version").equals(version)) {
                result = new RequestResult(RequestStatus.SAME_VERSION);
            } else {
                result = new RequestResult(RequestStatus.DIFFERENT_VERSION);
            }

            result.setVersion(apk_data.getString("version"));
            result.setAppName(apk_data.getString("name"));

            String app_url = apk_data.getString("url");
            if(!(app_url.startsWith("http://") || app_url.startsWith("https://"))) {
                app_url = url.substring(0, url.lastIndexOf('/')+1) + app_url;
            }

            // Validate the url
            if(HttpUrl.parse(app_url) == null)
                return new RequestResult(RequestStatus.ERROR_INVALID_URL);

            result.setUrl(app_url);
            return result;

        } catch (IOException e) {
            return new RequestResult(RequestStatus.ERROR, e.getMessage());
        } catch (JSONException e) {
            return new RequestResult(RequestStatus.ERROR, e.getMessage());
        } catch (IllegalArgumentException e) {
            return new RequestResult(RequestStatus.ERROR_INVALID_URL);
        }
    }
}
