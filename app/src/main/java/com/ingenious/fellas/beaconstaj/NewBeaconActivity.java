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
    List<Beacon> beacons = new ArrayList<>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beacon);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new NewBeaconAdapter(beacons);
        //recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        final Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG,"BT run");
                                BTAdapter.startDiscovery();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        final IntentFilter bluetoothFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(bReciever, bluetoothFilter);

        t.start();

    }
}