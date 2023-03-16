package com.example.filedownloadapp;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DownloadFile extends AsyncTask<Void, Void, Void> {
    private String fileName = "dummy.pdf";
    ApiInterface apiInterface;

    DownloadFile(ApiInterface apiInterface){
        this.apiInterface = apiInterface;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        Call<ResponseBody> responseBodyCall = this.apiInterface.downloadFile();
        String TAG = "doInBackground";
        try {
            Response<ResponseBody> response = responseBodyCall.execute();
            if (response.isSuccessful()) {
                // Save the file to the Download folder
                boolean success = saveToDownloadFolder(response.body(), fileName);
                if (success) {
                    Log.d(TAG, "File saved to Download folder");
                } else {
                    Log.e(TAG, "Failed to save file to Download folder");
                }
            } else {
                Log.e(TAG, "Download failed with error code " + response.code());
            }
        } catch (IOException e) {
            Log.e(TAG, "Download failed", e);
        }
        return null;
    }

    private boolean saveToDownloadFolder(ResponseBody body, String fileName) {
        String TAG = "saveToDownloadFolder";
        try {
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            File file = new File(downloadFolder, fileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[5000];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);
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
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to create file in Download folder", e);
            return false;
        }
    }
}
