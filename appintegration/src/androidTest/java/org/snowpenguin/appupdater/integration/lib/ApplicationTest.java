package org.snowpenguin.appupdater.integration.lib;

import android.app.Application;
import android.test.ApplicationTestCase;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    Properties systemProperties = System.getProperties();

    protected static InputStream getAsset(String name) {
        return ApplicationTest.class.getResourceAsStream("/assets/" + name);
    }

    protected static void loadCustomProperties() throws IOException {
        Properties systemProperties = System.getProperties();
        InputStream mainProperties = getAsset("test.properties");
        if (mainProperties != null) {
            systemProperties.load(new InputStreamReader(mainProperties, "UTF-8"));
            mainProperties.close();
        }
    }

    public ApplicationTest() {
        super(Application.class);
        try {
            loadCustomProperties();
        } catch (IOException ignored) {
        }
    }

    public void testIntegrator()
    {
        Exception e = null;
        try {
            UpdaterIntegrator ui = new UpdaterIntegrator(getContext());
            ui.startCheckUpdate("http://fakeaddress.invalid", "2");
        }
        catch (Exception ex) {
            e = ex;
        }

        assertNull(e);
    }

    public void testIntegratorValidUrl()
    {
        Exception e = null;
        try {
            UpdaterIntegrator ui = new UpdaterIntegrator(getContext());
            ui.startCheckUpdate(systemProperties.getProperty("testurl"), systemProperties.getProperty("testversionfail"));
        }
        catch (Exception ex) {
            e = ex;
        }

        assertNull(e);
    }
}