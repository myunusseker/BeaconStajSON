package com.ingenious.fellas.beaconstaj.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Classes.NewBeaconAdapter;
import com.ingenious.fellas.beaconstaj.R;

public class NewBeaconActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    public static  NewBeaconAdapter mAdapter;

    @Override
    protected void onStart() {
        Globals.whichActivity = 1;
        super.onStart();
    }

    @Override
    protected void onStop() {
        Globals.whichActivity = 3;
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beacon);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Available Beacons");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new NewBeaconAdapter(getSupportFragmentManager(),Globals.beaconsAround);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onBackPressed() {
        Globals.whichActivity = 3;
        super.onBackPressed();
    }
}