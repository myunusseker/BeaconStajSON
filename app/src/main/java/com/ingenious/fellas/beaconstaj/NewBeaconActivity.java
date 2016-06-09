package com.ingenious.fellas.beaconstaj;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.NewBeaconAdapter;

import java.util.ArrayList;
import java.util.List;

public class NewBeaconActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewBeaconAdapter mAdapter;

    static boolean startBeaconSearch = true;
    private BluetoothAdapter BTAdapter;
    public static List<Beacon> beacons = new ArrayList<>();

    private static final String TAG = "MEHMET";

    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.i(TAG, "Bluetooth device found\n");
                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Beacon newDevice = new Beacon(device.getName(), device.getAddress(),rssi);
                Log.i(TAG, newDevice.getAddress());

                boolean beaconExist = false;
                for (Beacon beacon : beacons) {
                    if(beacon.getAddress().equalsIgnoreCase(newDevice.getAddress())){
                        beacon.setRssi(newDevice.getRssi());
                        beaconExist = true;
                        mAdapter.notifyDataSetChanged();
                    }
                }

                if(!beaconExist){
                    Log.i(TAG, "Add yapmadan once");
                    beacons.add(newDevice);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beacon);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new NewBeaconAdapter(beacons);
        //recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        final IntentFilter bluetoothFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(bReciever, bluetoothFilter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startBeaconSearch) {
                    fab.setImageResource(android.R.drawable.ic_media_pause);
                    registerReceiver(bReciever, bluetoothFilter);
                    BTAdapter.startDiscovery();
                }
                else {
                    fab.setImageResource(android.R.drawable.ic_media_play);
                    unregisterReceiver(bReciever);
                    BTAdapter.cancelDiscovery();
                }
                startBeaconSearch = !startBeaconSearch;
            }
        });
    }
}