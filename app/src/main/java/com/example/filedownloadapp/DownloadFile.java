package com.example.filedownloadapp;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class DownloadFile {
    private String fileName = "dummy.pdf";
    private ApiInterface apiInterface;
    private Context context;

    DownloadFile(ApiInterface apiInterface, Context context) {
        this.apiInterface = apiInterface;
        this.context = context;
    }


    public void download() {
        String TAG = "downloadFile";
        apiInterface.downloadFile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    boolean success = saveToDownloadFolder(responseBody, fileName);
                    if (success) {
                        Log.d(TAG, "File saved to Download folder");
                        showNotification(fileName);
                    } else {
                        Log.e(TAG, "Failed to save file to Download folder");
                    }
                }, e -> {
                    Log.e(TAG, "Download failed", e);
                });
    }

    private boolean saveToDownloadFolder(ResponseBody body, String fileName) {
        String TAG = "saveToDownloadFolder";

        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(downloadFolder, fileName);
        try (InputStream inputStream = body.byteStream(); OutputStream outputStream = new FileOutputStream(file);) {
            byte[] fileReader = new byte[5000];
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to save file to Download folder", e);
            return false;
        }
    }

    private void showNotification(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        Uri fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "download_channel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Download complete")
                .setContentText("File downloaded: " + fileName)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "download_channel",
                    "Download Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for file downloads");
            notificationManager.createNotificationChannel(channel);
        }

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        notificationManager.notify(123, builder.build());
    }
}
