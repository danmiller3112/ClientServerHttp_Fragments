package com.roll.clientserverhttp_fragments.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.roll.clientserverhttp_fragments.entities.AuthResponse;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by RDL on 26/02/2017.
 */

public class HttpProvider {
    public static final String BASE_URL = "https://telranstudentsproject.appspot.com/_ah/api/contactsApi/v1";

    private static HttpProvider ourInstance = new HttpProvider();

    public HttpProvider() {
    }

    public static HttpProvider getInstance() {
        return ourInstance;
    }

    public String registration(String jsonBody) throws Exception {
        String result = "";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url(HttpProvider.BASE_URL + "/registration")
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(15, TimeUnit.SECONDS);
        client.setConnectTimeout(15, TimeUnit.SECONDS);
        Response response = client.newCall(request).execute();
        if (response.code() < 400) {
            result = response.body().string();
            Log.d("REGISTRATION", result);
        } else if (response.code() == 409) {
            throw new Exception("User already exist!");
        } else {
            String error = response.body().string();
            Log.d("REGISTRATION ERROR", error);
            throw new Exception("Server ERROR!");
        }
        return result;
    }
}
