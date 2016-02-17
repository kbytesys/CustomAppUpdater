package org.snowpenguin.org.appupdater;

import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import junit.framework.Assert;
import okhttp3.HttpUrl;
import org.junit.BeforeClass;
import org.junit.Rule;
import android.support.test.rule.ServiceTestRule;
import org.junit.Test;
import org.snowpenguin.org.appupdater.service.RequestResult;
import org.snowpenguin.org.appupdater.service.RequestStatus;
import org.snowpenguin.org.appupdater.service.UpdateService;
import org.snowpenguin.org.appupdater.service.task.CheckUpdateAsyncTask;
import org.snowpenguin.org.appupdater.service.task.ServiceAsyncTask;
import org.snowpenguin.org.appupdater.task.DummyCheckUpdateTask;
import org.snowpenguin.org.appupdater.task.DummyTaskObserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class UpdateServiceTests {
    Properties systemProperties = System.getProperties();

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    protected static InputStream getAsset(String name) {
        return UpdateServiceTests.class.getResourceAsStream("/assets/" + name);
    }

    protected static void loadCustomProperties() throws IOException {
        Properties systemProperties = System.getProperties();
        InputStream mainProperties = getAsset("test.properties");
        if (mainProperties != null) {
            systemProperties.load(new InputStreamReader(mainProperties, "UTF-8"));
            mainProperties.close();
        }
    }

    @BeforeClass
    public static void onceExecutedBeforeAll() throws IOException {
        loadCustomProperties();
    }


    @Test
    public void testWithBoundService() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent =
                new Intent(InstrumentationRegistry.getTargetContext(),
                        UpdateService.class);

        // Data can be passed to the service via the Intent.
        //serviceIntent.putExtra(UpdateService.SEED_KEY, 42L);

        serviceIntent.setAction(UpdateService.ACTION_CHECK_NEW_RELEASE);
        serviceIntent.putExtra(UpdateService.EXTRA_URL, "http://www.google.it");
        serviceIntent.putExtra(UpdateService.EXTRA_VERSION, "2");

        // Bind the service and grab a reference to the binder.
        IBinder binder = mServiceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        UpdateService service =
                ((UpdateService.LocalBinder) binder).getService();

        // Start the service with parameters
        // service.startService(serviceIntent);

        service.handleCheckNewRelease(systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"));

    }

    @Test
    public void testCheckUpdateTask() throws ExecutionException, InterruptedException {
        DummyTaskObserver dummy = new DummyTaskObserver();
        CheckUpdateAsyncTask task = new CheckUpdateAsyncTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"));
        task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.SAME_VERSION));
        Assert.assertNotNull(HttpUrl.parse(dummy.getResult().getUrl()));

        dummy = new DummyTaskObserver();
        task = new CheckUpdateAsyncTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversionfail"));
        task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.DIFFERENT_VERSION));
        Assert.assertNotNull(HttpUrl.parse(dummy.getResult().getUrl()));

        dummy = new DummyTaskObserver();
        task = new CheckUpdateAsyncTask(dummy, "invalidurl", systemProperties.getProperty("testversionfail"));
        task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.ERROR_INVALID_URL));
    }

    @Test
    public void textDummyCheckUpdateTask() throws ExecutionException, InterruptedException {
        DummyTaskObserver dummy = new DummyTaskObserver();
        DummyCheckUpdateTask task = new DummyCheckUpdateTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"));

        task.setIoError(true);
        task.execute().get();
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.ERROR));


        dummy = new DummyTaskObserver();
        task = new DummyCheckUpdateTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"));
        task.setJsonData("Some Invalid Data");
        task.execute().get();
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.ERROR));

        // Check incomplete data
        dummy = new DummyTaskObserver();
        task = new DummyCheckUpdateTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"));
        task.setJsonData("{}");
        task.execute().get();
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.ERROR));


        dummy = new DummyTaskObserver();
        task = new DummyCheckUpdateTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"));
        task.setJsonData("{\"version\": \"2\", \"name\": \"testapp\", \"url\": \"relative_url.apk\"}");
        task.execute().get();
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.SAME_VERSION));
        Assert.assertNotNull(HttpUrl.parse(dummy.getResult().getUrl()));
        Assert.assertEquals(dummy.getResult().getUrl(), systemProperties.getProperty("testurlbase") + "relative_url.apk");

        dummy = new DummyTaskObserver();
        task = new DummyCheckUpdateTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"));
        task.setJsonData("{\"version\": \"2\", \"name\": \"testapp\", \"url\": \"http://fakedomain/absolute_url.apk\"}");
        task.execute().get();
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.SAME_VERSION));
        Assert.assertNotNull(HttpUrl.parse(dummy.getResult().getUrl()));
        Assert.assertEquals(dummy.getResult().getUrl(), "http://fakedomain/absolute_url.apk");
    }
}
