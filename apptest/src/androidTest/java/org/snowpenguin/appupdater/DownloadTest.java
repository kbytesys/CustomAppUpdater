package org.snowpenguin.appupdater;

import android.test.AndroidTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.snowpenguin.appupdater.task.DownloadUpdateAsyncTask;
import org.snowpenguin.appupdater.task.DummyTaskObserver;
import org.snowpenguin.appupdater.task.RequestResult;
import org.snowpenguin.appupdater.task.RequestStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class DownloadTest extends AndroidTestCase {

    Properties systemProperties = System.getProperties();

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

    public void testDownloadInstallTask() throws ExecutionException, InterruptedException {
        DummyTaskObserver dummy = new DummyTaskObserver();
        DownloadUpdateAsyncTask task = new DownloadUpdateAsyncTask(getContext(),
                dummy, systemProperties.getProperty("testappurl"), "cautest", true, true, false);
        task.execute().get();
        Assert.assertTrue(true);
    }

    public void testDownloadTask() throws ExecutionException, InterruptedException {
        DummyTaskObserver dummy = new DummyTaskObserver();
        DownloadUpdateAsyncTask task = new DownloadUpdateAsyncTask(getContext(),
                dummy, systemProperties.getProperty("testappurl"), "cautest", true, false, false);
        RequestResult result = task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        assertSame(result, dummy.getResult());
        assertTrue(dummy.getResult() != null);
        assertEquals(dummy.getResult().getStatus(), RequestStatus.SUCCESS);
        assertNotNull(dummy.getResult().getAppUri());
    }

    public void testDownloadTaskPostExecute() throws ExecutionException, InterruptedException {
        DummyTaskObserver dummy = new DummyTaskObserver();
        DownloadUpdateAsyncTask task = new DownloadUpdateAsyncTask(getContext(),
                dummy, systemProperties.getProperty("testappurl"), "cautest", true, false, true);
        RequestResult result = task.execute().get();
        Thread.sleep(500);
        Assert.assertFalse(dummy.isCancelled());
        assertSame(result, dummy.getResult());
        assertTrue(dummy.getResult() != null);
        assertEquals(dummy.getResult().getStatus(), RequestStatus.SUCCESS);
        assertNotNull(dummy.getResult().getAppUri());
    }

    public void testDownloadTaskInvalid() throws ExecutionException, InterruptedException {
        DummyTaskObserver dummy = new DummyTaskObserver();
        DownloadUpdateAsyncTask task = new DownloadUpdateAsyncTask(getContext(),
                dummy, "invalidurl", "cautest", true, false, false);
        RequestResult result = task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        assertSame(result, dummy.getResult());
        assertTrue(dummy.getResult() != null);
        assertEquals(dummy.getResult().getStatus(), RequestStatus.ERROR_INVALID_URL);
    }

    public void testDownloadTask404() throws ExecutionException, InterruptedException {
        DummyTaskObserver dummy = new DummyTaskObserver();
        DownloadUpdateAsyncTask task = new DownloadUpdateAsyncTask(getContext(),
                dummy, systemProperties.getProperty("testappurl").replace(".apk", ".fake"), "cautest", true, false, false);
        RequestResult result = task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        assertSame(result, dummy.getResult());
        assertTrue(dummy.getResult() != null);
        assertEquals(dummy.getResult().getStatus(), RequestStatus.DOWNLOAD_ERROR);
        assertEquals(dummy.getResult().getMessage(), "404");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadCustomProperties();
    }
}
