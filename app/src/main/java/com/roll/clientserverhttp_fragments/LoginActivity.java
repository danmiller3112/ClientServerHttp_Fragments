package com.roll.clientserverhttp_fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputLogin, inputPass;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBarLogin;
    private String login, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputLogin = (EditText) findViewById(R.id.input_login);
        inputPass = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        progressBarLogin = (ProgressBar) findViewById(R.id.progress_login);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        init();
    }

    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", MODE_PRIVATE);
        login = sharedPreferences.getString("LOGIN", "");
        pass = sharedPreferences.getString("PASS", "");
        if ("".equals(login) || "".equals(pass)) {
            return;
        }

        inputLogin.setText(login);
        inputPass.setText(pass);
        new LoginAsyncTask().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (chekFields()) {
                    new LoginAsyncTask().execute();
                }
                break;
            case R.id.btn_register:
                if (chekFields()) {
                    new RegisterAsyncTask().execute();
                }
                break;
        }
    }

    private boolean chekFields() {
        if ("".equals(String.valueOf(inputLogin.getText()))) {
            inputLogin.setError("Login is empty!");
            return false;
        }
        if ("".equals(String.valueOf(inputPass.getText()))) {
            inputPass.setError("Password is Empty");
            return false;
        }
        return true;
    }

    private class LoginAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnLogin.setEnabled(false);
            btnRegister.setEnabled(false);
            inputLogin.setEnabled(false);
            inputPass.setEnabled(false);
            progressBarLogin.setVisibility(View.VISIBLE);
            login = String.valueOf(inputLogin.getText());
            pass = String.valueOf(inputPass.getText());
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "Login OK!";
            Gson gson = new Gson();
            Auth auth = new Auth(login, pass);
            String jsonBody = gson.toJson(auth);

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, jsonBody);

            Request request = new Request.Builder()
                    .url(HttpProvider.BASE_URL + "/login")
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.setReadTimeout(15, TimeUnit.SECONDS);
            client.setConnectTimeout(15, TimeUnit.SECONDS);

            try {
                Response response = client.newCall(request).execute();
                if (response.code() < 400) {
                    String jsonResponse = response.body().string();
                    Log.d("LOGIN", jsonResponse);
                    AuthResponse authResponse = gson.fromJson(jsonResponse, AuthResponse.class);
                    SharedPreferences sPref = getSharedPreferences("AUTH", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putString("TOKEN", authResponse.getToken());
                    editor.putString("LOGIN", login);
                    editor.putString("PASS", pass);
                    editor.commit();
                } else if (response.code() == 401) {
                    result = "Wrong login or password!";
                } else {
                    String jsonResponse = response.body().string();
                    Log.d("LOGIN ERROR", jsonResponse);
                    result = "Server ERROR!";
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = "Connection ERROR!";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            btnLogin.setEnabled(true);
            btnRegister.setEnabled(true);
            progressBarLogin.setVisibility(View.GONE);
            inputLogin.setEnabled(true);
            inputPass.setEnabled(true);
            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
            if (s.equals("Login OK!")) {
                startContactList();
                finish();
            }
        }
    }

    private class RegisterAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnLogin.setEnabled(false);
            btnRegister.setEnabled(false);
            inputLogin.setEnabled(false);
            inputPass.setEnabled(false);
            progressBarLogin.setVisibility(View.VISIBLE);
            login = String.valueOf(inputLogin.getText());
            pass = String.valueOf(inputPass.getText());
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "Registration OK!";
            Gson gson = new Gson();
            Auth auth = new Auth(login, pass);
            String jsonBody = gson.toJson(auth);

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, jsonBody);

            Request request = new Request.Builder()
                    .url(HttpProvider.BASE_URL + "/registration")
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.setReadTimeout(15, TimeUnit.SECONDS);
            client.setConnectTimeout(15, TimeUnit.SECONDS);

            try {
                Response response = client.newCall(request).execute();
                if (response.code() < 400) {
                    String jsonResponse = response.body().string();
                    Log.d("REGISTRATION", jsonResponse);
                    AuthResponse authResponse = gson.fromJson(jsonResponse, AuthResponse.class);
                    SharedPreferences sPref = getSharedPreferences("AUTH", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putString("TOKEN", authResponse.getToken());
                    editor.putString("LOGIN", login);
                    editor.putString("PASS", pass);
                    editor.commit();
                } else if (response.code() == 409) {
                    result = "User already exist!";
                } else {
                    String jsonResponse = response.body().string();
                    Log.d("REGISTRATION ERROR", jsonResponse);
                    result = "Server ERROR!";
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = "Connection ERROR!";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            btnLogin.setEnabled(true);
            btnRegister.setEnabled(true);
            inputLogin.setEnabled(true);
            inputPass.setEnabled(true);
            progressBarLogin.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
            if (s.equals("Registration OK!")) {
                startContactList();
                finish();
            }
        }
    }


    private void startContactList() {
        Intent intent = new Intent("contact.list.http.dan");
        startActivity(intent);
    }
}
