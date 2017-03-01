package com.roll.clientserverhttp_fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class AddContactActivity extends AppCompatActivity {

    private EditText inputName, inputEmail, inputPhone, inputDesc;
    private String token, phone;
    private String jsonUser;
    private MenuItem addConItem;
    private ProgressBar progressBarSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputDesc = (EditText) findViewById(R.id.input_desc);
        progressBarSave = (ProgressBar) findViewById(R.id.progress_save);

        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        addConItem = (MenuItem) findViewById(R.id.item_add_contact);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_add_contact) {
            phone = String.valueOf(inputPhone.getText());
            String email = String.valueOf(inputEmail.getText());
            String desc = String.valueOf(inputDesc.getText());
            String name = String.valueOf(inputName.getText());
            if ("".equals(phone) || "".equals(name)) {
                Toast.makeText(AddContactActivity.this, "Name or phone is empty", Toast.LENGTH_SHORT).show();
            } else {
                Gson gson = new Gson();
                jsonUser = gson.toJson(new User(name, email, phone, desc, Long.valueOf(phone)));
                Log.d("jsonUser", jsonUser);
                new SaveContactAsyncTask().execute();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class SaveContactAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputDesc.setEnabled(false);
            inputEmail.setEnabled(false);
            inputName.setEnabled(false);
            inputPhone.setEnabled(false);
            progressBarSave.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "Add OK!";

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, jsonUser);

            Request request = new Request.Builder()
                    .header("Authorization", token)
                    .url(HttpProvider.BASE_URL + "/setContact")
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.setReadTimeout(15, TimeUnit.SECONDS);
            client.setConnectTimeout(15, TimeUnit.SECONDS);

            try {
                Response response = client.newCall(request).execute();
                if (response.code() < 400) {
                    String jsonResponse = response.body().string();
                    Log.d("ADD", jsonResponse);

                } else if (response.code() == 401) {
                    result = "Wrong authorization! empty token!";
                } else {
                    String jsonResponse = response.body().string();
                    Log.d("ADD ERROR", jsonResponse);
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
            progressBarSave.setVisibility(View.GONE);
            inputDesc.setEnabled(true);
            inputEmail.setEnabled(true);
            inputName.setEnabled(true);
            inputPhone.setEnabled(true);
            Toast.makeText(AddContactActivity.this, s, Toast.LENGTH_SHORT).show();
            if ("Add OK!".equals(s)) {
                finish();
            }
        }
    }
}
