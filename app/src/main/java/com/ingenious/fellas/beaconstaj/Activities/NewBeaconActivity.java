package com.ingenious.fellas.beaconstaj.Activities;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
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
    private ScanCallback scanCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beacon);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new NewBeaconAdapter(getSupportFragmentManager(),beacons);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        interrupt = false;
         t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted() && !interrupt) {
                        Thread.sleep(300);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        Log.i(TAG, "Bluetooth device found\n");
                        int rssi = result.getRssi();
                        BluetoothDevice device = result.getDevice();
                        Beacon newDevice = new Beacon(device.getName(), device.getAddress(),rssi);
                        Log.i(TAG, "rssi degisimi: "+rssi+" MAC: "+newDevice.getAddress());

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
            BTAdapter.getBluetoothLeScanner().startScan(scanCallback);
        }
        else {
            t.start();
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
            this.unregisterReceiver(bReciever);

        }
    }
}