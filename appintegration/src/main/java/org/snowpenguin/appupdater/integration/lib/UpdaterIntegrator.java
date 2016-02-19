package org.snowpenguin.appupdater.integration.lib;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class UpdaterIntegrator {

    private Context context;
    public static final String INTENT_ACTION = "org.snowpenguin.appupdater.CHECK_UPDATE";

    public UpdaterIntegrator(Context context) {
        this.context = context;
    }

    public void startCheckUpdate(String url, String version) {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra("url", url);
        intent.putExtra("version", version);

        if(intent.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivity(intent);
        }
        else {
            Toast.makeText(context, R.string.no_app_installed, Toast.LENGTH_LONG).show();
        }
    }
}
