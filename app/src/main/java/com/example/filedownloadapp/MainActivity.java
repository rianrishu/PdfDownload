package com.example.filedownloadapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import retrofit2.Retrofit;

//https://moccasin-ceciley-78.tiiny.site
//https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf
public class MainActivity extends AppCompatActivity {

    Button btn_download;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/")
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        btn_download = (Button) findViewById(R.id.btn_download);

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadFile(apiInterface).execute();
                Toast.makeText(MainActivity.this, "file downloaded", Toast.LENGTH_SHORT).show();
            }
        });

    }
}