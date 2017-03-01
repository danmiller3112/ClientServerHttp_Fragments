package com.roll.clientserverhttp_fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoginFrag.LoginFragmentListener {

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
    public void loginOk(String result) {
        Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_container, new ContactListFrag(), "LIST");
        transaction.commit();
    }
}
