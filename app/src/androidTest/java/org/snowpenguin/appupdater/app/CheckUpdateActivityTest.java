package org.snowpenguin.appupdater.app;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;
import org.snowpenguin.appupdater.task.RequestResult;
import org.snowpenguin.appupdater.task.RequestStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import static android.support.test.espresso.Espresso.onView;

public class CheckUpdateActivityTest extends ActivityInstrumentationTestCase2<CheckUpdateActivity> {

    Properties systemProperties = System.getProperties();

    protected static InputStream getAsset(String name) {
        return CheckUpdateActivityTest.class.getResourceAsStream("/assets/" + name);
    }

    protected static void loadCustomProperties() throws IOException {
        Properties systemProperties = System.getProperties();
        InputStream mainProperties = getAsset("test.properties");
        if (mainProperties != null) {
            systemProperties.load(new InputStreamReader(mainProperties, "UTF-8"));
            mainProperties.close();
        }
    }

    public CheckUpdateActivityTest(Class<CheckUpdateActivity> activityClass) {
        super(activityClass);
    }

    public CheckUpdateActivityTest() {
        super(CheckUpdateActivity.class);
        try {
            loadCustomProperties();
        } catch (IOException ignored) {
        }
    }

    public void testNoIntent() {
        CheckUpdateActivity activity = getActivity();
        assertEquals(getActivity().getResources().getString(R.string.update_no_intent),
                ((TextView)getActivity().findViewById(R.id.titleLabel)).getText());
    }

    public void testInvalidIntent() {
        Intent intent = new Intent();
        intent.setAction(CheckUpdateActivity.INTENT_ACTION);
        intent.putExtra("urls", "fake");
        intent.putExtra("sversion", "fake");
        setActivityIntent(intent);

        CheckUpdateActivity activity = getActivity();
        assertEquals(activity.getResources().getString(R.string.update_no_intent),
                ((TextView)activity.findViewById(R.id.titleLabel)).getText());
    }

    public void testInvalidUrl() {
        Intent intent = new Intent();
        intent.setAction(CheckUpdateActivity.INTENT_ACTION);
        intent.putExtra("url", "invalid://url");
        intent.putExtra("version", "fake");
        setActivityIntent(intent);

        CheckUpdateActivity activity = getActivity();

        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        }

        RequestResult lastResult = activity.getLastResult();
        assertNotNull(lastResult);
        assertEquals(RequestStatus.ERROR_INVALID_URL, lastResult.getStatus());
    }

    private Intent createTestIntent(boolean sameversion) {
        Intent result = new Intent();
        result.setAction(CheckUpdateActivity.INTENT_ACTION);
        result.putExtra("url", systemProperties.getProperty("testurl"));
        result.putExtra("version", sameversion ? systemProperties.getProperty("testversion") : systemProperties.getProperty("testversionfail"));
        return result;
    }

    public void testCheckUpdateDifferent() {
        setActivityIntent(createTestIntent(false));
        CheckUpdateActivity activity = getActivity();

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }

        RequestResult lastResult = activity.getLastResult();
        assertNotNull(lastResult);
        assertEquals(RequestStatus.DIFFERENT_VERSION, lastResult.getStatus());
        assertEquals(View.VISIBLE, activity.findViewById(R.id.updateButton).getVisibility());
        assertEquals(View.GONE, activity.findViewById(R.id.retryButton).getVisibility());
    }

    public void testCheckUpdateSame() {
        setActivityIntent(createTestIntent(true));
        CheckUpdateActivity activity = getActivity();

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }

        RequestResult lastResult = activity.getLastResult();
        assertNotNull(lastResult);
        assertEquals(RequestStatus.SAME_VERSION, lastResult.getStatus());
        assertEquals(View.GONE, activity.findViewById(R.id.updateButton).getVisibility());
        assertEquals(View.GONE, activity.findViewById(R.id.retryButton).getVisibility());
    }
}
