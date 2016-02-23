package org.snowpenguin.appupdater;

import junit.framework.Assert;
import okhttp3.HttpUrl;
import org.junit.BeforeClass;
import org.junit.Rule;
import android.support.test.rule.ServiceTestRule;
import org.junit.Test;
import org.snowpenguin.appupdater.task.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class CheckUpdateTests {
    Properties systemProperties = System.getProperties();

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    protected static InputStream getAsset(String name) {
        return CheckUpdateTests.class.getResourceAsStream("/assets/" + name);
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
    public void testCheckUpdateTask() throws ExecutionException, InterruptedException {
        DummyTaskObserver dummy = new DummyTaskObserver();
        CheckUpdateAsyncTask task = new CheckUpdateAsyncTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"), false);
        RequestResult result = task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertSame(result, dummy.getResult());
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getStartNotified());
        Assert.assertTrue(dummy.getResult().getStatus().toString(), dummy.getResult().getStatus().equals(RequestStatus.SAME_VERSION));
        Assert.assertNotNull(HttpUrl.parse(dummy.getResult().getUrl()));

        dummy = new DummyTaskObserver();
        task = new CheckUpdateAsyncTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversionfail"), false);
        result = task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertSame(result, dummy.getResult());
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertTrue(dummy.getStartNotified());
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().toString(), dummy.getResult().getStatus().equals(RequestStatus.DIFFERENT_VERSION));
        Assert.assertNotNull(HttpUrl.parse(dummy.getResult().getUrl()));

        dummy = new DummyTaskObserver();
        task = new CheckUpdateAsyncTask(dummy, "invalidurl", systemProperties.getProperty("testversionfail"), false);
        result = task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertSame(result, dummy.getResult());
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertTrue(dummy.getStartNotified());
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.ERROR_INVALID_URL));
    }

    @Test
    public void textDummyCheckUpdateTask() throws ExecutionException, InterruptedException {
        DummyTaskObserver dummy = new DummyTaskObserver();
        DummyCheckUpdateTask task = new DummyCheckUpdateTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"), false);

        task.setIoError(true);
        RequestResult result = task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertSame(result, dummy.getResult());
        Assert.assertTrue(dummy.getStartNotified());
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.ERROR));


        dummy = new DummyTaskObserver();
        task = new DummyCheckUpdateTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"), false);
        task.setJsonData("Some Invalid Data");
        result = task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertSame(result, dummy.getResult());
        Assert.assertTrue(dummy.getStartNotified());
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.ERROR));

        // Check incomplete data
        dummy = new DummyTaskObserver();
        task = new DummyCheckUpdateTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"), false);
        task.setJsonData("{}");
        result = task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertSame(result, dummy.getResult());
        Assert.assertTrue(dummy.getStartNotified());
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.ERROR));


        dummy = new DummyTaskObserver();
        task = new DummyCheckUpdateTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"), false);
        task.setJsonData("{\"version\": \"2\", \"name\": \"testapp\", \"url\": \"relative_url.apk\"}");
        result = task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertSame(result, dummy.getResult());
        Assert.assertTrue(dummy.getStartNotified());
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.SAME_VERSION));
        Assert.assertNotNull(HttpUrl.parse(dummy.getResult().getUrl()));
        Assert.assertEquals(dummy.getResult().getUrl(), systemProperties.getProperty("testurlbase") + "relative_url.apk");

        dummy = new DummyTaskObserver();
        task = new DummyCheckUpdateTask(dummy, systemProperties.getProperty("testurl"), systemProperties.getProperty("testversion"), false);
        task.setJsonData("{\"version\": \"2\", \"name\": \"testapp\", \"url\": \"http://fakedomain/absolute_url.apk\"}");
        result = task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertSame(result, dummy.getResult());
        Assert.assertTrue(dummy.getStartNotified());
        Assert.assertTrue(dummy.getResult() != null);
        Assert.assertTrue(dummy.getResult().getStatus().equals(RequestStatus.SAME_VERSION));
        Assert.assertNotNull(HttpUrl.parse(dummy.getResult().getUrl()));
        Assert.assertEquals(dummy.getResult().getUrl(), "http://fakedomain/absolute_url.apk");
    }
}
