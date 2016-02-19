package org.snowpenguin.appupdater.integration.lib;

import android.app.Application;
import android.test.ApplicationTestCase;
import org.junit.Test;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
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
}