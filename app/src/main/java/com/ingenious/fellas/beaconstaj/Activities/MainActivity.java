package com.ingenious.fellas.beaconstaj.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 10;
    private static int BLUETOOTH_CHANGED = 0;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Globals.initialize(getApplicationContext());

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        //attempt to open bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Your Device does not support Bluetooth LE", Toast.LENGTH_LONG).show();
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Log.i("Attempt to login", "" + REQUEST_ENABLE_BT);
        }
        else
        {
            startApp();
        }

    }

    private void startBluetoothSearch() {
        BluetoothAdapter BTAdapter;
        BluetoothLeScanner bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner.startScan(filters, settings, Globals.scanCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            BLUETOOTH_CHANGED = 1;
            if (resultCode == RESULT_OK) {
                Log.i("Bluetooth acildi", "burada");
                startApp();
            } else {
                Log.i("Bluetooth", "Acilmadi");
                finish();
            }
        }
    }
    private void startApp()
    {
        startBluetoothSearch();
        Log.i("asdf", "main activity burasi");
        if (!isConnected) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
        if (Globals.username.equals("nullUser")) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, BeaconListActivity.class);
            startActivity(intent);
        }
    }
}
