package com.roll.clientserverhttp_fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.roll.clientserverhttp_fragments.model.CallbackListener;
import com.roll.clientserverhttp_fragments.model.HttpProvider;

import java.io.IOException;

public class ContactListFrag extends Fragment implements ContactAdapter.ViewClickListener {

    private ListView listView;
    private String token;
    private Contacts contacts = new Contacts();
    private ContactAdapter adapter;
    private ProgressBar progressBarContact;
    private TextView txtEmpty;
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
        new ContactsAsyncTask().execute();
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
        SharedPreferences sharedPreferences = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER", new Gson().toJson(user));
        editor.commit();
        listener.sameAction("VIEW");
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
            try {
                String jsonResponse = HttpProvider.getInstance().getAll(token);
                contacts = new Gson().fromJson(jsonResponse, Contacts.class);
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

            listener.sameAction("LOGOUT");
        }

        if (item.getItemId() == R.id.item_add) {
            listener.sameAction("ADD");
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
            try {
                String jsonResponse = HttpProvider.getInstance().deleteAll(token);
                contacts.getContacts().clear();
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
            progressBarContact.setVisibility(View.GONE);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            if ("Delete all contacts, OK!".equals(s)) {
                txtEmpty.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        }
    }
}

