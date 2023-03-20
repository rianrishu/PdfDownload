package com.example.filedownloadapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import com.example.filedownloadapp.databinding.ActivityMainBinding;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

public class MainActivity extends AppCompatActivity {

    Button btn_download;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        activityMainBinding.btnDownload.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                if (notificationManager != null && !notificationManager.areNotificationsEnabled()) {
                    // Ask the user to grant permission to post notifications
                    Toast.makeText(this, "Please allow notifications to see the download notifications", Toast.LENGTH_SHORT).show();
                    Intent intent_to_notification_settings = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivityForResult(intent_to_notification_settings, 1);
                }
            }
            new DownloadFile(apiInterface, getApplicationContext()).download();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                if (notificationManager != null && notificationManager.areNotificationsEnabled()) {
                    // Notifications are enabled, you can now post notifications
                } else{
                    Toast.makeText(this, "File is saved to Download folder, notifications not allowed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}