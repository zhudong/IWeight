package com.axecom.iweight.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.axecom.iweight.R;
import com.axecom.iweight.ui.uiutils.UIUtils;
import com.axecom.iweight.utils.CommonUtils;
import com.axecom.iweight.utils.MD5Util;
import com.axecom.iweight.utils.WeakHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class AppDownloadTask {
    private static final String TAG = "AppDownloadTask";
    private static final int BUFFER_SIZE = 16 * 1024;
    private final NotificationManager notificationManager;
    private final Notification notification;

    private String mUrl;
    private DownloadInfo mDownloadInfo;
    private WeakHandler handler = new WeakHandler();

    public AppDownloadTask(String url, NotificationManager manager, Notification notification) {
        mUrl = url;
        mDownloadInfo = new DownloadInfo();
        notificationManager = manager;
        this.notification = notification;
    }

    public void downloadApk(final File fileDir) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                start(fileDir);
            }
        }).start();
    }

    boolean finished;

    private void start(File fileDir) {
        finished = false;
        HttpURLConnection urlConnection = null;
        BufferedOutputStream outputStream = null;
        try {
            URL url = new URL(mUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.connect();
            int contentLength = urlConnection.getContentLength();

            Log.d(TAG, "Start downloading " + urlConnection.getURL());
            Log.d(TAG, String.format("File size %.2f kb", (float) contentLength / 1024));

            String fileName = getFileName(urlConnection);
            mDownloadInfo.apkFile = new File(fileDir, fileName);
            outputStream = new BufferedOutputStream(new FileOutputStream(mDownloadInfo.apkFile));
            Log.d(TAG, "Downloading apk into " + mDownloadInfo.apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            int totalLength = 0;
            InputStream in = urlConnection.getInputStream();
            while (!finished && (length = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                totalLength += length;
                mDownloadInfo.progress = (totalLength == 0f) ? 0f : (float) totalLength / (float) contentLength;
                Log.d(TAG, "handler post ");
                handler.post(new Runnable() {
                    float progress;

                    @Override
                    public void run() {
                        if (progress == mDownloadInfo.progress || finished) return;
                        progress = mDownloadInfo.progress;
                        notification.contentView.setProgressBar(R.id.pb_update, 100, (int) (progress * 100), false);
                        notification.contentView.setTextViewText(R.id.tv_progress, (int) (progress * 100) + "%");
                        notificationManager.notify(UpdateManager.NOTIFI_ID, notification);
                        if (mDownloadInfo.progress == 1) {
                            Log.d(TAG, "Start download finshed ");
                            notificationManager.cancel(UpdateManager.NOTIFI_ID);
                            CommonUtils.install(UIUtils.getContext(), Uri.fromFile(mDownloadInfo.apkFile));
                            finished = true;
                        }
                    }
                });
            }
        } catch (IOException e) {
            Log.e(TAG, String.format("Download: %s, %s", mDownloadInfo.apkFile, mUrl), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private String getFileName(HttpURLConnection urlConnection) {
        String filename = "";
        String s = urlConnection.getURL().toString();
        if (!TextUtils.isEmpty(s)) {
            String[] split = s.split("/");
            if (split.length > 0) {
                filename = split[split.length - 1];
            }
        }
        if (TextUtils.isEmpty(filename)) {
            filename = MD5Util.getMD5(s) + ".apk";
        }
        return filename;
    }

    DownloadInfo getDownloadInfo() {
        return mDownloadInfo;
    }

    static class DownloadInfo {
        float progress;
        File apkFile;
    }
}

