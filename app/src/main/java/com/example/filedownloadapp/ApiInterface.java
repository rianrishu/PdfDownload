package com.example.filedownloadapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

public interface ApiInterface {

    @Streaming
    @GET("dummy.pdf")
    Call<ResponseBody> downloadFile();
}

