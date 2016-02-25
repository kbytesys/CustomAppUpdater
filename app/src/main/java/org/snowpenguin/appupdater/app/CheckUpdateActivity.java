package org.snowpenguin.appupdater.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.snowpenguin.appupdater.task.*;

public class CheckUpdateActivity extends AppCompatActivity {

    private ImageView statusImage;
    private Animation rotationAnimation;
    private TextView titleLabel;
    private Button updateButton;
    private Button retryButton;
    private TextView resultTextView;
    private RequestResult lastCheckResult;
    private RequestResult lastDownloadResult;

    public static final String INTENT_ACTION = "org.snowpenguin.appupdater.CHECK_UPDATE";
    private CheckUpdateNotifier checkObserver;
    private DownloadNotifier downloadNotifier = new DownloadNotifier();

    private String version;
    private String url;
    private String appUrl;

    class CheckUpdateNotifier implements IRequestObserver {

        private String version;

        public CheckUpdateNotifier(String version) {
            this.version = version;
        }

        @Override
        public void notifyStarted() {
            updateTitleLabel(R.string.update_check_inprogress);
            resultTextView.setText("");
            retryButton.setVisibility(View.GONE);
            showWaitingImage();
        }

        @Override
        public void notifyResult(RequestResult requestResult) {
            lastCheckResult = requestResult;

            switch (lastCheckResult.getStatus()) {
                case DIFFERENT_VERSION:
                    showOkImage();
                    updateButton.setVisibility(View.VISIBLE);
                    resultTextView.setText(getResultTemplate(R.string.update_new_version,
                                lastCheckResult.getAppName(),
                                version,
                                lastCheckResult.getVersion()
                    ));
                    break;
                case SAME_VERSION:
                    showOkImage();
                    resultTextView.setText(getResultTemplate(R.string.update_same_version));
                    break;
                case ERROR_INVALID_URL:
                    showFailImage();
                    updateTitleLabel(R.string.update_check_error);
                    resultTextView.setText(getResultTemplate(R.string.update_no_intent_bis));
                    break;
                default:
                    showFailImage();
                    updateTitleLabel(R.string.update_check_error);
                    resultTextView.setText(getResultTemplate(R.string.update_check_error_bis));
                    retryButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void notifyProgress(RequestProgress[] values) {

        }

        @Override
        public void notifyCancelled(RequestResult requestResult) {
            notifyCancelled();
        }

        @Override
        public void notifyCancelled() {
            resultTextView.setText(getResultTemplate(R.string.update_cencelled));
            showFailImage();
            retryButton.setVisibility(View.VISIBLE);
        }
    }

    class DownloadNotifier implements IRequestObserver {

        @Override
        public void notifyStarted() {
            updateTitleLabel(R.string.update_download_inprogress);
            resultTextView.setText("");
            retryButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.GONE);
            showWaitingImage();
        }

        @Override
        public void notifyResult(RequestResult requestResult) {
            lastDownloadResult = requestResult;

            switch (lastDownloadResult.getStatus()) {
                case SUCCESS:
                    showOkImage();
                    resultTextView.setText(R.string.update_success);
                    break;
                default:
                    showFailImage();
                    updateTitleLabel(R.string.update_download_error);
                    resultTextView.setText(getResultTemplate(R.string.update_check_error_bis));
                    retryButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void notifyProgress(RequestProgress[] values) {

        }

        @Override
        public void notifyCancelled(RequestResult requestResult) {
            notifyCancelled();
        }

        @Override
        public void notifyCancelled() {
            resultTextView.setText(getResultTemplate(R.string.update_cencelled));
            showFailImage();
            retryButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_update);

        statusImage = (ImageView)findViewById(R.id.statusImageView);
        statusImage.setScaleX(2);
        statusImage.setScaleY(2);
        titleLabel = (TextView)findViewById(R.id.titleLabel);
        updateButton = (Button)findViewById(R.id.updateButton);
        updateButton.setVisibility(View.GONE);
        retryButton = (Button)findViewById(R.id.retryButton);
        retryButton.setVisibility(View.GONE);
        rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.wait_anim);
        rotationAnimation.setDuration(1000);
        resultTextView = (TextView)findViewById(R.id.resultTextView);

        Intent intent = getIntent();

        if(intent == null || !intent.getAction().equals(INTENT_ACTION) ||
                intent.getExtras().getString("version", null) == null ||
                intent.getExtras().getString("url", null) == null) {
            updateTitleLabel(R.string.update_no_intent);
            resultTextView.setText(getResultTemplate(R.string.update_no_intent_bis));
            showFailImage();
            return;
        }

        version = intent.getExtras().getString("version");
        url = intent.getExtras().getString("url");

        checkObserver = new CheckUpdateNotifier(version);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(appUrl == null)
                    tryCheckTask();
                else
                    tryDownloadTask();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryDownloadTask();
            }
        });

        tryCheckTask();
    }

    private void tryCheckTask() {
        CheckUpdateAsyncTask task = new CheckUpdateAsyncTask(checkObserver, url, version, true);
        task.execute();
    }

    private void tryDownloadTask() {
        DownloadUpdateAsyncTask task = new DownloadUpdateAsyncTask(CheckUpdateActivity.this,
                downloadNotifier, lastCheckResult.getUrl(), "CustomAppUpdater", true, true, true);
        task.execute();
    }

    private void showWaitingImage() {
        statusImage.setImageResource(R.mipmap.ic_launcher);
        statusImage.startAnimation(rotationAnimation);
    }

    private void showOkImage() {
        statusImage.clearAnimation();
        statusImage.setImageResource(R.mipmap.ok_icon);
    }

    private void showFailImage() {
        statusImage.clearAnimation();
        statusImage.setImageResource(R.mipmap.fail_icon);
    }

    private void updateTitleLabel(int resId) {
        titleLabel.setText(resId);
    }

    private Spanned getResultTemplate(int resId) {
        return Html.fromHtml(this.getResources().getString(resId));
    }

    private Spanned getResultTemplate(int resId, Object... formatArgs) {
        return Html.fromHtml(String.format(this.getResources().getString(resId), formatArgs));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Function for test propose only
     * @return
     */
    RequestResult getLastCheckResult() {
        return lastCheckResult;
    }

    /**
     * Function for test propose only
     * @return
     */
    RequestResult getLastDownloadResult() {
        return lastDownloadResult;
    }
}
