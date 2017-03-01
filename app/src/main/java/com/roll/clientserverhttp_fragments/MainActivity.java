package com.roll.clientserverhttp_fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.roll.clientserverhttp_fragments.model.CallbackListener;

public class MainActivity extends AppCompatActivity implements CallbackListener {

    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.frag_container, new LoginFrag(), "LOGIN");
        transaction.commit();
    }


    @Override
    public void sameAction(String result) {
        transaction = getFragmentManager().beginTransaction();
        switch (result) {
            case "LOGIN_OK":
            case "SAVE_OK":
            case "ADD_OK":
                transaction.replace(R.id.frag_container, new ContactListFrag(), "LIST");
                break;
            case "LOGOUT":
                transaction.replace(R.id.frag_container, new LoginFrag(), "LOGIN");
                break;
            case "VIEW":
                transaction.replace(R.id.frag_container, new ViewContactFrag(), "VIEW");
                break;
            case "ADD":
                transaction.replace(R.id.frag_container, new AddContactFrag(), "ADD");
                break;
        }
        transaction.commit();
    }
}
