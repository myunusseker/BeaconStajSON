package com.ingenious.fellas.beaconstaj.Activities;

import android.app.ActionBar;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
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
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Classes.NewBeaconAdapter;
import com.ingenious.fellas.beaconstaj.R;

import java.util.ArrayList;
import java.util.List;

public class NewBeaconActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewBeaconAdapter mAdapter;

    volatile Thread t;
    private BluetoothAdapter BTAdapter;
    List<Beacon> beacons = new ArrayList<>();
    static boolean interrupt = false;

    private static final String TAG = "MEHMET";

    private ScanCallback scanCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beacon);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Available Beacons");

        //------------Deneme

        BluetoothLeScanner bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        //

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new NewBeaconAdapter(getSupportFragmentManager(),beacons);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        final IntentFilter bluetoothFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            interrupt = false;
            t = new Thread() {
                @Override
                public void run() {
                    try {
                        while (!isInterrupted() && !interrupt) {
                            Thread.sleep(5000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG, "BT run");
                                    BTAdapter.startDiscovery();
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                    }
                }
            };
            t.start();
        }
        else{
            scanCallback = new ScanCallback() {
                /*@Override
                public void onBatchScanResults(List<ScanResult> results) {
                    Log.i("MEHMET", "onbatch'e girdi");
                    super.onBatchScanResults(results);
                    for(ScanResult result : results){
                        int rssi = result.getRssi();
                        String BSSID = result.getDevice().getAddress();
                        ScanRecord record = result.getScanRecord();
                        long timestamp = result.getTimestampNanos() / 1000000L;
                        Log.i("asdf","BSSID = " + BSSID + "\nrssi = " + rssi + "\n" + Long.toString(System.currentTimeMillis()) + " " + Long.toString(timestamp));
                    }
                }*/

                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        Log.i(TAG, "Bluetooth device found\n");
                        int rssi = result.getRssi();
                        BluetoothDevice device = result.getDevice();
                        Beacon newBeacon = new Beacon(device.getName(), device.getAddress(),rssi);
                        Log.i(TAG, "rssi degisimi: " + rssi + " MAC: " + newBeacon.getAddress());

                        ////BURAYA BIR ESY YAPAPASCAHIOIX

                        boolean beaconExist = false;
                        for (Beacon beacon : beacons) {
                            if(beacon.getAddress().equalsIgnoreCase(newBeacon.getAddress())){
                                beacon.setRssi(newBeacon.getRssi());
                                beaconExist = true;
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        if(!beaconExist) {
                            Log.i(TAG, "Add yapmadan once");
                            if (!Globals.doesBeaconsExists(newBeacon.getAddress())){
                                beacons.add(newBeacon);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            };
            bluetoothLeScanner.startScan(filters, settings, scanCallback);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BTAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
        else {
            interrupt = true;
        }
    }
}