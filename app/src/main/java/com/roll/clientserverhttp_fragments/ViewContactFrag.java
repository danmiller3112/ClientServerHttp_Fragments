package com.roll.clientserverhttp_fragments;

import android.content.Intent;
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
import com.roll.clientserverhttp_fragments.entities.User;
import com.roll.clientserverhttp_fragments.model.HttpProvider;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ViewContactFrag extends AppCompatActivity {

    private EditText nameView, emailView, phoneView, descView;
    private ProgressBar progressView;
    private String userJson, token;
    private String name, email, phone, desc;
    private MenuItem editItem, saveItem;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_view_contact);

        nameView = (EditText) findViewById(R.id.view_name);
        emailView = (EditText) findViewById(R.id.view_email);
        phoneView = (EditText) findViewById(R.id.view_phone);
        descView = (EditText) findViewById(R.id.view_desc);
        progressView = (ProgressBar) findViewById(R.id.progress_view);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            userJson = intent.getExtras().getString("USER", "");
            token = intent.getExtras().getString("TOKEN", "");
        }

        Gson gson = new Gson();
        user = gson.fromJson(userJson, User.class);

        nameView.setText(user.getFullName());
        emailView.setText(user.getEmail());
        phoneView.setText(user.getPhoneNumber());
        descView.setText(user.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        editItem = menu.findItem(R.id.item_edit);
        saveItem = menu.findItem(R.id.item_save);
        saveItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_edit:
                editItem.setVisible(false);
                saveItem.setVisible(true);
                nameView.setEnabled(true);
                emailView.setEnabled(true);
                phoneView.setEnabled(true);
                descView.setEnabled(true);
                break;
            case R.id.item_save:
                phone = String.valueOf(phoneView.getText());
                if ("".equals(phone)) {
                    Toast.makeText(ViewContactFrag.this, "Phone number is EMPTY!!!", Toast.LENGTH_LONG).show();
                } else {
                    editItem.setVisible(true);
                    saveItem.setVisible(false);
                    name = String.valueOf(nameView.getText());
                    email = String.valueOf(emailView.getText());
                    desc = String.valueOf(descView.getText());
                    new SaveAsynkTask().execute();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class SaveAsynkTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressView.setVisibility(View.VISIBLE);
            nameView.setEnabled(false);
            emailView.setEnabled(false);
            phoneView.setEnabled(false);
            descView.setEnabled(false);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "Edit OK!";

            userJson = new Gson().toJson(new User(name, email, phone, desc, user.getContactId()));

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, userJson);

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
                    Log.d("EDIT", jsonResponse);

                } else if (response.code() == 401) {
                    result = "Wrong authorization! empty token!";
                } else {
                    String jsonResponse = response.body().string();
                    Log.d("EDIT ERROR", jsonResponse);
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
            progressView.setVisibility(View.INVISIBLE);
            Toast.makeText(ViewContactFrag.this, s, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
