package com.ingenious.fellas.beaconstaj.Fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ingenious.fellas.beaconstaj.Activities.BeaconDetailActivity;
import com.ingenious.fellas.beaconstaj.Activities.BeaconFinderActivity;
import com.ingenious.fellas.beaconstaj.Activities.BeaconListActivity;
import com.ingenious.fellas.beaconstaj.Activities.BeaconMapActivity;
import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Classes.RequestHandler;
import com.ingenious.fellas.beaconstaj.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BeaconDetailFragment extends Fragment {

    public static final String ARG_ITEM_NAME = "item_name";
    public static final String ARG_ITEM_MAC = "item_mac";
    private String mac;
    private String name;
    private Button lostButton;
    private Button mapOrFindButton;
    private Beacon mBeacon;

    public BeaconDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_NAME)) {
            mBeacon = new Beacon(getArguments().getString(ARG_ITEM_NAME)
                    ,getArguments().getString(ARG_ITEM_MAC), 1);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mBeacon.getName());
            }
        }
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("mac", mBeacon.getAddress());;
                bundle.putString("beaconName", mBeacon.getName());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.beacon_detail, container, false);
        ((TextView) rootView.findViewById(R.id.beacon_detail)).setText(mBeacon.getAddress());
        lostButton = (Button) rootView.findViewById(R.id.lost_button);
        mapOrFindButton = (Button) rootView.findViewById(R.id.map_or_find_button);
        if(/*BEACON UZAKTAYSA*/ true)
        {
            mapOrFindButton.setText("Locate On Map");
        }
        else
        {
            mapOrFindButton.setText("Help Me Find My Beacon");
        }
        mapOrFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent;
                if(true /* UZAKTAYSA*/)
                {
                    intent = new Intent(context, BeaconMapActivity.class);
                    intent.putExtra(BeaconDetailFragment.ARG_ITEM_NAME,mBeacon.getName());
                    intent.putExtra(BeaconDetailFragment.ARG_ITEM_MAC, mBeacon.getAddress());
                }
                else
                {
                    intent = new Intent(context, BeaconFinderActivity.class);
                    intent.putExtra("mac", mBeacon.getAddress());
                }
                context.startActivity(intent);
            }
        });
        return rootView;
    }
}
