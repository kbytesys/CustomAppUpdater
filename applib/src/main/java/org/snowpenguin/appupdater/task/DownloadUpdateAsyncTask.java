package org.snowpenguin.appupdater.task;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import org.snowpenguin.appupdater.R;

import java.io.File;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DownloadUpdateAsyncTask extends CauAsyncTask {

    private final String url;
    private final String targetDir;
    private final Context context;
    private final boolean autoExecute;
    private DownloadManager dm;
    private long queueId;
    private Recevier recevier;
    final Lock lock = new ReentrantLock();
    private Condition waitCondition = lock.newCondition();

    public DownloadUpdateAsyncTask(Context context, IRequestObserver observer, String url, String targetDir, boolean autoExecute, boolean cleanOldApks, boolean notifyOnPostExecute) {
        super(observer, notifyOnPostExecute);

        this.url = url;
        this.targetDir = targetDir;
        this.context = context;
        this.autoExecute = autoExecute;

        if(cleanOldApks) {
            File backupDir = Environment.getExternalStoragePublicDirectory(targetDir);
            if(backupDir.exists()) {
                for (File file : backupDir.listFiles()) {
                    if(file.isFile() && file.getName().contains(".apk"))
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                }
            }
        }
    }

    private class Recevier extends BroadcastReceiver {

        RequestResult downloadResult;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                if(downloadId != queueId)
                    return;

                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(queueId);

                Cursor c = dm.query(q);
                if(c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    boolean success = status == DownloadManager.STATUS_SUCCESSFUL;

                    if(success) {
                        downloadResult = new RequestResult(RequestStatus.SUCCESS);
                        downloadResult.setAppUri(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
                    }
                    else {
                        downloadResult = new RequestResult(RequestStatus.DOWNLOAD_ERROR);
                        downloadResult.setMessage(String.valueOf(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))));
                    }
                }
                else {
                    downloadResult = new RequestResult(RequestStatus.ERROR, context.getResources().getString(R.string.download_not_found));
                }

                lock.lock();
                waitCondition.signal();
                lock.unlock();
            }
        }

        public RequestResult getDownloadResult() {
            return downloadResult;
        }
    }

    @Override
    protected RequestResult backgroundTask(Void... voids) {
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(url));
        } catch (IllegalArgumentException e) {
            return new RequestResult(RequestStatus.ERROR_INVALID_URL);
        }
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setDestinationInExternalPublicDir(targetDir, "update.apk");
        request.setVisibleInDownloadsUi(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        queueId = dm.enqueue(request);

        try {
            lock.lock();
            waitCondition.await();
            return recevier.getDownloadResult();
        } catch (InterruptedException e) {
            return new RequestResult(RequestStatus.ERROR, e.getMessage());
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        recevier = new Recevier();
        context.registerReceiver(recevier, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onPostExecute(RequestResult requestResult) {
        super.onPostExecute(requestResult);

        context.unregisterReceiver(recevier);

        if(autoExecute && requestResult.getStatus().equals(RequestStatus.SUCCESS)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(requestResult.getAppUri()), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
