package org.snowpenguin.org.appupdater;

import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import junit.framework.Assert;
import org.junit.Rule;
import android.support.test.rule.ServiceTestRule;
import org.junit.Test;
import org.snowpenguin.org.appupdater.service.RequestResult;
import org.snowpenguin.org.appupdater.service.RequestStatus;
import org.snowpenguin.org.appupdater.service.UpdateService;
import org.snowpenguin.org.appupdater.service.task.CheckUpdateAsyncTask;
import org.snowpenguin.org.appupdater.service.task.ServiceAsyncTask;
import org.snowpenguin.org.appupdater.task.DummyTaskObserver;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class UpdateServiceTests {
    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

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

        service.handleCheckNewRelease("http://www.google.it", "2");

    }

    @Test
    public void testCheckUpdateTask() throws ExecutionException, InterruptedException {
        DummyTaskObserver dummy = new DummyTaskObserver();
        CheckUpdateAsyncTask task = new CheckUpdateAsyncTask(dummy, "http://www.google.it", "2");
        task.execute().get();
        Assert.assertFalse(dummy.isCancelled());
        Assert.assertTrue(dummy.getResult() != null && dummy.getResult().getStatus().equals(RequestStatus.SUCCESS));

    }
}
