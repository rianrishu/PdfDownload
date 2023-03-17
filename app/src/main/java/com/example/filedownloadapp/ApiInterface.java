package com.example.filedownloadapp;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

public interface ApiInterface {

    @Streaming
    @GET("dummy.pdf")
    Observable<ResponseBody> downloadFile();
}

