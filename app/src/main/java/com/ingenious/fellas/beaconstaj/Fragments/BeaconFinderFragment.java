package com.ingenious.fellas.beaconstaj.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.R;

import java.util.ArrayList;
import java.util.List;

public class BeaconFinderFragment extends DialogFragment {

    private String mac, beaconName;
    private Context context;
    private ToneGenerator toneG;
    private BluetoothAdapter BTAdapter;
    private ScanCallback callback;
    private Beacon mBeacon;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mac = getArguments().getString("mac");
        beaconName = getArguments().getString("beaconName");
        mBeacon = Globals.getBeacon(mac);
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.fragment_beacon_finder, null))
                .setTitle("Finding Your Device: " + beaconName)
                .setPositiveButton("I found it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        BluetoothLeScanner bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        callback  = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Log.i("YUNUS", "Bluyo\n");
                int rssi = result.getRssi();
                String foundMac = result.getDevice().getAddress();
                if(foundMac.equalsIgnoreCase(mac)){
                    Log.i("YUNUS", String.valueOf(rssi));
                    ((TextView) getDialog().findViewById(R.id.beacon_finder_rssi)).setText(String.valueOf(rssi));
                    mBeacon.setRssi(rssi);
                    Log.i("YUNUS",mBeacon.getDistance());
                    ((TextView) getDialog().findViewById(R.id.beacon_finder_result)).setText(mBeacon.getDistance());
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,rssi*10+1000);
                    if(rssi>-35){
                        ((TextView) getDialog().findViewById(R.id.beacon_finder_result)).setText("You have found");
                    }
                }
            }
        };
        bluetoothLeScanner.startScan(filters, settings, callback);
        return builder.create();
    }

    @Override
    public void onDestroy() {
        BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner().stopScan(callback);
        toneG.stopTone();
        super.onDestroy();
    }
}