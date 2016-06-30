package com.ingenious.fellas.beaconstaj.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.R;

public class BeaconFinderActivity extends AppCompatActivity {
    public static String mac;
    public static Context context;
    public static ToneGenerator toneG;
    public static Beacon mBeacon;
    public static TextView finderRssi;
    public static TextView finderResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_finder);
        Globals.whichActivity = 2;
        mac = getIntent().getStringExtra("mac");
        mBeacon = Globals.getBeacon(mac);
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50);
        finderRssi = (TextView) findViewById(R.id.beacon_finder_rssi);
        finderResult = (TextView) findViewById(R.id.beacon_finder_result);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Globals.whichActivity = 3;
        toneG.stopTone();
    }
}
