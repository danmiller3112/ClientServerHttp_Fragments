package com.roll.clientserverhttp_fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.roll.clientserverhttp_fragments.adapters.ContactAdapter;
import com.roll.clientserverhttp_fragments.entities.Contacts;
import com.roll.clientserverhttp_fragments.entities.User;
import com.roll.clientserverhttp_fragments.model.HttpProvider;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ContactListFrag extends AppCompatActivity implements ContactAdapter.ViewClickListener {

    private ListView listView;
    private String token;
    private Contacts contacts = new Contacts();
    private ContactAdapter adapter;
    private ProgressBar progressBarContact;
    private TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_contact_list);

        listView = (ListView) findViewById(R.id.list_contact);
        txtEmpty = (TextView) findViewById(R.id.txt_empty);
        progressBarContact = (ProgressBar) findViewById(R.id.progress_contacts);

        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");

        initAdapter();
    }

    private void initAdapter() {
        adapter = new ContactAdapter(this, contacts.getContacts(), this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) adapter.getItem(position);
                Toast.makeText(ContactListFrag.this, "Was clicket position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ContactsAsyncTask().execute();
    }

    @Override
    public void btnViewClick(View view, int position) {
        User user = (User) adapter.getItem(position);
        Intent intent = new Intent(ContactListFrag.this, ViewContactFrag.class);
        intent.putExtra("USER", new Gson().toJson(user));
        intent.putExtra("TOKEN", token);
        startActivity(intent);
    }

    private class ContactsAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (contacts.getContacts().size() == 0) {
                txtEmpty.setVisibility(View.VISIBLE);
            }
            progressBarContact.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "Get all contacts, OK!";

            Request request = new Request.Builder()
                    .header("Authorization", token)
                    .url(HttpProvider.BASE_URL + "/contactsarray")
                    .get()
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.setReadTimeout(15, TimeUnit.SECONDS);
            client.setConnectTimeout(15, TimeUnit.SECONDS);

            try {
                Response response = client.newCall(request).execute();
                if (response.code() < 400) {
                    String jsonResponse = response.body().string();
                    Log.d("Get all contacts", jsonResponse);
                    contacts = new Gson().fromJson(jsonResponse, Contacts.class);
                } else if (response.code() == 401) {
                    result = "Wrong authorization! empty token!";
                } else {
                    String jsonResponse = response.body().string();
                    Log.d("Get all contacts", jsonResponse);
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
            progressBarContact.setVisibility(View.GONE);
            if ("Get all contacts, OK!".equals(s)) {
                if (contacts.getContacts().size() != 0) {
                    txtEmpty.setVisibility(View.INVISIBLE);
                }
                adapter.updateList(contacts.getContacts());
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_logout) {
            SharedPreferences sPref = getSharedPreferences("AUTH", MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(this, LoginFrag.class);
            startActivity(intent);
            finish();
        }

        if (item.getItemId() == R.id.item_add) {
            Intent intent = new Intent(this, AddContactFrag.class);
            intent.putExtra("TOKEN", token);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.item_delete_all) {
            new DelContactsAsyncTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    private class DelContactsAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarContact.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "Delete all contacts, OK!";

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, "");

            Request request = new Request.Builder()
                    .header("Authorization", token)
                    .url(HttpProvider.BASE_URL + "/clearContactsList")
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.setReadTimeout(15, TimeUnit.SECONDS);
            client.setConnectTimeout(15, TimeUnit.SECONDS);

            try {
                Response response = client.newCall(request).execute();
                if (response.code() < 400) {
                    String jsonResponse = response.body().string();
                    Log.d("Del all contacts", jsonResponse);
                    contacts.getContacts().clear();
                } else if (response.code() == 401) {
                    result = "Wrong authorization! empty token!";
                } else {
                    String jsonResponse = response.body().string();
                    Log.d("Del all contacts", jsonResponse);
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
            progressBarContact.setVisibility(View.GONE);
            Toast.makeText(ContactListFrag.this, s, Toast.LENGTH_SHORT).show();
            if ("Delete all contacts, OK!".equals(s)) {
                txtEmpty.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        }
    }
}

