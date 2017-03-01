package com.roll.clientserverhttp_fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
                transaction.replace(R.id.frag_container, new ContactListFrag(), "LIST");
                transaction.commit();
                break;
            case "LOGOUT":
                transaction.replace(R.id.frag_container, new LoginFrag(), "LOGIN");
                transaction.commit();
                break;
            case "VIEW":
                Log.e("sameAction", "VIEW");
                transaction.replace(R.id.frag_container, new ViewContactFrag(), "VIEW");
                transaction.commit();
                break;
        }

    }
}
