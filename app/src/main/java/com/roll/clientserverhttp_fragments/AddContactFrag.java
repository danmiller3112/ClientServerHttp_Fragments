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

import java.io.IOException;

public class AddContactFrag extends Fragment {

    private EditText inputName, inputEmail, inputPhone, inputDesc;
    private String token, phone;
    private String jsonUser;
    private MenuItem addConItem;
    private ProgressBar progressBarSave;
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
        View view = inflater.inflate(R.layout.frag_add_contact, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        inputName = (EditText) view.findViewById(R.id.input_name);
        inputEmail = (EditText) view.findViewById(R.id.input_email);
        inputPhone = (EditText) view.findViewById(R.id.input_phone);
        inputDesc = (EditText) view.findViewById(R.id.input_desc);
        progressBarSave = (ProgressBar) view.findViewById(R.id.progress_save);
        context = getActivity().getApplicationContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add, menu);
        addConItem = menu.findItem(R.id.item_add_contact);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_add_contact) {
            phone = String.valueOf(inputPhone.getText());
            String email = String.valueOf(inputEmail.getText());
            String desc = String.valueOf(inputDesc.getText());
            String name = String.valueOf(inputName.getText());
            if ("".equals(phone) || "".equals(name)) {
                Toast.makeText(context, "Name or phone is empty", Toast.LENGTH_SHORT).show();
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
            try {
                String jsonResponse = HttpProvider.getInstance().add(token, jsonUser);
            } catch (IOException e) {
                e.printStackTrace();
                result = "Connection ERROR!";
            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
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
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            if ("Add OK!".equals(s)) {
                listener.sameAction("ADD_OK");
            }
        }
    }
}
