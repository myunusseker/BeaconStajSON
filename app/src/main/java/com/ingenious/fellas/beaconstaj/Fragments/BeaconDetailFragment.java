package com.ingenious.fellas.beaconstaj.Fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ingenious.fellas.beaconstaj.Activities.BeaconDetailActivity;
import com.ingenious.fellas.beaconstaj.Activities.BeaconListActivity;
import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Beacon detail screen.
 * This fragment is either contained in a {@link BeaconListActivity}
 * in two-pane mode (on tablets) or a {@link BeaconDetailActivity}
 * on handsets.
 */
public class BeaconDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_NAME = "item_name";
    public static final String ARG_ITEM_MAC = "item_mac";

    /**
     * The dummy content this fragment is presenting.
     */
    private Beacon mBeacon;
    private static ToneGenerator toneG;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BeaconDetailFragment() {
    }

    private static ScanCallback callback;
    private BluetoothAdapter BTAdapter;
    private static final String TAG = "MEHMET";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_NAME)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_NAME));
            mBeacon = new Beacon(getArguments().getString(ARG_ITEM_NAME)
                    ,getArguments().getString(ARG_ITEM_MAC), 1);


            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mBeacon.getName());
            }




        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.beacon_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mBeacon != null) {
            Log.i("asdf","123" + mBeacon.getAddress());
            ((TextView) rootView.findViewById(R.id.beacon_detail)).setText(getArguments().getString(ARG_ITEM_MAC));

            BluetoothLeScanner bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
            ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            List<ScanFilter> filters = new ArrayList<ScanFilter>();
            BTAdapter = BluetoothAdapter.getDefaultAdapter();
            callback  = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {

                    Log.i(TAG, "Bluetooth device found\n");
                    int rssi = result.getRssi();
                    String myMAC = result.getDevice().getAddress();
                    if(myMAC.equalsIgnoreCase(mBeacon.getAddress())){
                        ((TextView) rootView.findViewById(R.id.beacon_detail_rssi)).setText("rssi:   " + rssi);
                        mBeacon.setRssi(rssi);
                        ((TextView) rootView.findViewById(R.id.beacon_detail_distance)).setText(mBeacon.getDistance());
                        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,rssi*10+1000);
                        if(rssi>-35){
                            onBack();
                            ((TextView) rootView.findViewById(R.id.beacon_detail_distance)).setText("You have found");
                        }
                    }
                }
            };
            bluetoothLeScanner.startScan(filters, settings, callback);
        }
        else {
            Log.i("asdf","123");
        }

        return rootView;
    }
    public static void onBack(){
        BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner().stopScan(callback);
        toneG.stopTone();
    }
}
