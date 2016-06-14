package com.ingenious.fellas.beaconstaj.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Globals.initialize(getApplicationContext());
        if(Globals.username.equals("nullUser"))
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, BeaconListActivity.class);
            startActivity(intent);
        }
    }
}
