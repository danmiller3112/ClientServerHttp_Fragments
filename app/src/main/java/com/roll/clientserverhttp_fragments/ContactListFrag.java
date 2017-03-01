package com.roll.clientserverhttp_fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class ContactListFrag extends Fragment implements ContactAdapter.ViewClickListener {

    private ListView listView;
    private String token;
    private Contacts contacts = new Contacts();
    private ContactAdapter adapter;
    private ProgressBar progressBarContact;
    private TextView txtEmpty;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_contact_list, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.list_contact);
        txtEmpty = (TextView) view.findViewById(R.id.txt_empty);
        progressBarContact = (ProgressBar) view.findViewById(R.id.progress_contacts);
        context = getActivity().getApplicationContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");

        initAdapter();
    }

    private void initAdapter() {
        adapter = new ContactAdapter(context, contacts.getContacts(), this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) adapter.getItem(position);
                Toast.makeText(context, "Was clicket position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void btnViewClick(View view, int position) {
        User user = (User) adapter.getItem(position);
        Intent intent = new Intent(context, ViewContactFrag.class);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_logout) {
            SharedPreferences sPref = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sPref.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(context, LoginFrag.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.item_add) {
            Intent intent = new Intent(context, AddContactFrag.class);
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
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            if ("Delete all contacts, OK!".equals(s)) {
                txtEmpty.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        }
    }
}

