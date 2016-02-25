package org.snowpenguin.appupdater.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.snowpenguin.appupdater.task.CheckUpdateAsyncTask;
import org.snowpenguin.appupdater.task.IRequestObserver;
import org.snowpenguin.appupdater.task.RequestProgress;
import org.snowpenguin.appupdater.task.RequestResult;

public class CheckUpdateActivity extends AppCompatActivity {

    private ImageView statusImage;
    private Animation rotationAnimation;
    private TextView titleLabel;
    private Button updateButton;
    private TextView resultTextView;
    private RequestResult lastResult;

    public static final String INTENT_ACTION = "org.snowpenguin.appupdater.CHECK_UPDATE";

    class CheckUpdateNotifier implements IRequestObserver {

        private String version;

        public CheckUpdateNotifier(String version) {
            this.version = version;
        }

        @Override
        public void notifyStarted() {
            updateTitleLabel(R.string.update_check_inprogress);
            resultTextView.setText("");
            showWaitingImage();
        }

        @Override
        public void notifyResult(RequestResult requestResult) {
            lastResult = requestResult;

            switch (lastResult.getStatus()) {
                case DIFFERENT_VERSION:
                    showOkImage();
                    updateButton.setVisibility(View.VISIBLE);
                    resultTextView.setText(String.format(
                            getResultTemplate(R.string.update_new_version),
                                lastResult.getAppName(),
                                version,
                                lastResult.getVersion()
                    ));
                    break;
                case SAME_VERSION:
                    showOkImage();
                    resultTextView.setText(getResultTemplate(R.string.update_same_version));
                    break;
                default:
                    showFailImage();
                    resultTextView.setText(getResultTemplate(R.string.update_check_error));
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
        Animation rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.wait_anim);
        rotationAnimation.setDuration(1000);
        resultTextView = (TextView)findViewById(R.id.resultTextView);

        Intent intent = getIntent();

        if(intent == null || !intent.getAction().equals(INTENT_ACTION)) {
            updateTitleLabel(R.string.update_no_intent);
            resultTextView.setText(getResultTemplate(R.string.update_no_intent_bis));
            showFailImage();
            return;
        }

        String version = intent.getExtras().getString("version", "");
        String url = intent.getExtras().getString("url", "");

        CheckUpdateAsyncTask task = new CheckUpdateAsyncTask(new CheckUpdateNotifier(version), url, version, true);
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

    private String getResultTemplate(int resId) {
        return this.getResources().getString(resId);
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
}
