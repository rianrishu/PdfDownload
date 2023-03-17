package com.example.filedownloadapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

//https://moccasin-ceciley-78.tiiny.site
//https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf
public class MainActivity extends AppCompatActivity {

    Button btn_download;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null && !notificationManager.areNotificationsEnabled()) {
                // Ask the user to grant permission to post notifications
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivityForResult(intent, 1);
            }
        }


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        btn_download = (Button) findViewById(R.id.btn_download);

        btn_download.setOnClickListener(view -> {
            new DownloadFile(apiInterface, getApplicationContext()).download();
            Toast.makeText(MainActivity.this, "file downloaded", Toast.LENGTH_SHORT).show();
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
                }
            }
        }
    }

}