package com.ingenious.fellas.beaconstaj.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Classes.NewBeaconAdapter;
import com.ingenious.fellas.beaconstaj.R;

import java.util.ArrayList;
import java.util.List;

public class NewBeaconActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private RecyclerView recyclerView;
    private NewBeaconAdapter mAdapter;

    volatile Thread t;
    private BluetoothAdapter BTAdapter;
    List<Beacon> beacons = new ArrayList<>();
    static boolean interrupt = false;

    private static final String TAG = "MEHMET";

    private ScanCallback scanCallback;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onStart() {
        super.onStart();
        initializeLocation();
        mGoogleApiClient.connect();
      //  createLocationRequest();
    }
/*
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beacon);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Available Beacons");


        //------------Deneme

        BluetoothLeScanner bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        //

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new NewBeaconAdapter(getSupportFragmentManager(), beacons);
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
        } else {
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.i(TAG, "Bluetooth device found\n");
                        int rssi = result.getRssi();
                        BluetoothDevice device = result.getDevice();
                        Beacon newBeacon = new Beacon(device.getName(), device.getAddress(), rssi);
                        Log.i(TAG, "rssi degisimi: " + rssi + " MAC: " + newBeacon.getAddress());

                        ////BURAYA BIR ESY YAPAPASCAHIOIX

                        boolean beaconExist = false;
                        for (Beacon beacon : beacons) {
                            if (beacon.getAddress().equalsIgnoreCase(newBeacon.getAddress())) {
                                beacon.setRssi(newBeacon.getRssi());
                                beaconExist = true;
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        if (!beaconExist) {
                            Log.i(TAG, "Add yapmadan once");
                            if (!Globals.doesBeaconsExists(newBeacon.getAddress())) {
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

    private void initializeLocation() {
        Log.i("aaa", "initialize location");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BTAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        } else {
            interrupt = true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("aaa", "onConnected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.i("aaa", "Lang : " + mLastLocation.getLatitude() + " Long: " + mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "error -> " + connectionResult.getErrorCode());
    }
}