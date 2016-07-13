package com.ingenious.fellas.beaconstaj.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ingenious.fellas.beaconstaj.Activities.BeaconFinderActivity;
import com.ingenious.fellas.beaconstaj.Activities.BeaconMapActivity;
import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Classes.RequestHandler;
import com.ingenious.fellas.beaconstaj.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BeaconDetailFragment extends Fragment {

    public static final String ARG_ITEM_NAME = "item_name";
    public static final String ARG_ITEM_MAC = "item_mac";
    private String mac;
    private String name;
    private Button lostButton;
    private Button mapOrFindButton;
    private Beacon mBeacon;
    private int isWanted = 0;

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
            new isWantedTask().execute(mBeacon.getAddress());
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
        lostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = 1 - isWanted;
                Log.i("hello",String.valueOf(a));
                new setWantedTask().execute(mBeacon.getAddress(),String.valueOf(a));
            }
        });

        return rootView;
    }

    public class isWantedTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            HashMap<String, String> h = new HashMap<>();
            h.put("mac", String.valueOf(params[0]));
            JSONObject response = RequestHandler.sendPostRequest(Globals.URL + "isWanted.php", h);
            try {
                Log.i(Globals.TAG, response.getString("status_message"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(Globals.TAG, "response'u alamadik");
            }

            JSONObject obj = new JSONObject();
            try {
                if (response.getJSONObject("data") != null)
                    obj = response.getJSONObject("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return obj;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject == null) return;
            try {
                String str = jsonObject.getString("isWanted");
                isWanted = Integer.valueOf(str);
                Log.i("hello", "isWanted: " + isWanted);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class setWantedTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            HashMap<String, String> h = new HashMap<>();
            h.put("mac", String.valueOf(params[0]));
            h.put("isWanted",String.valueOf(params[1]));
            Log.i("hello","newparam: "+String.valueOf(params[1]));
            JSONObject response = RequestHandler.sendPostRequest(Globals.URL + "setWanted.php", h);
            try {
                Log.i(Globals.TAG, response.getString("status_message"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(Globals.TAG, "response'u alamadik");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new isWantedTask().execute(mBeacon.getAddress());
        }
    }
}
