package com.roll.clientserverhttp_fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoginFrag.LoginFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frag_container, new LoginFrag(), "LOGIN");
        transaction.commit();
    }


    @Override
    public void loginOk(String result) {
        Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
    }
}
