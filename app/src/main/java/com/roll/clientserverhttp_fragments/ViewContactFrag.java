package com.roll.clientserverhttp_fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.roll.clientserverhttp_fragments.entities.User;
import com.roll.clientserverhttp_fragments.model.CallbackListener;
import com.roll.clientserverhttp_fragments.model.HttpProvider;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ViewContactFrag extends Fragment {

    private EditText nameView, emailView, phoneView, descView;
    private ProgressBar progressView;
    private String userJson, token;
    private String name, email, phone, desc;
    private MenuItem editItem, saveItem;
    private User user;
    private Context context;
    private CallbackListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (CallbackListener) activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CallbackListener) {
            listener = (CallbackListener) context;
        } else {
            throw new RuntimeException("Context must implements CallbackListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_view_contact, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameView = (EditText) view.findViewById(R.id.view_name);
        emailView = (EditText) view.findViewById(R.id.view_email);
        phoneView = (EditText) view.findViewById(R.id.view_phone);
        descView = (EditText) view.findViewById(R.id.view_desc);
        progressView = (ProgressBar) view.findViewById(R.id.progress_view);

        context = getActivity().getApplicationContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        userJson = sharedPreferences.getString("USER", "");

        Gson gson = new Gson();
        user = gson.fromJson(userJson, User.class);

        nameView.setText(user.getFullName());
        emailView.setText(user.getEmail());
        phoneView.setText(user.getPhoneNumber());
        descView.setText(user.getDescription());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_view, menu);
        editItem = menu.findItem(R.id.item_edit);
        saveItem = menu.findItem(R.id.item_save);
        saveItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
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
                    Toast.makeText(context, "Phone number is EMPTY!!!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            if ("Edit OK!".equals(s)) {
                listener.sameAction("SAVE_OK");
            }
        }
    }

}
