package org.snowpenguin.appupdater;

import android.test.AndroidTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.snowpenguin.appupdater.task.DownloadUpdateAsyncTask;
import org.snowpenguin.appupdater.task.DummyTaskObserver;

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

    @Test
    public void testDownloadTask() throws ExecutionException, InterruptedException {
        DummyTaskObserver dummy = new DummyTaskObserver();
        DownloadUpdateAsyncTask task = new DownloadUpdateAsyncTask(getContext(),
                dummy, systemProperties.getProperty("testappurl"), "cautest", true, true);
        task.execute().get();
        Assert.assertTrue(true);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadCustomProperties();
    }
}
