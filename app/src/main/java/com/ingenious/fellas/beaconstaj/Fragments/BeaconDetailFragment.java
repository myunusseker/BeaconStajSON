package com.ingenious.fellas.beaconstaj.Fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
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
import android.widget.TextView;

import com.ingenious.fellas.beaconstaj.Activities.BeaconDetailActivity;
import com.ingenious.fellas.beaconstaj.Activities.BeaconListActivity;
import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Classes.RequestHandler;
import com.ingenious.fellas.beaconstaj.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
    String latitude,longtitude;
    /**
     * The dummy content this fragment is presenting.
     */
    private Beacon mBeacon;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
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

                BeaconFinderFragment dialog = new BeaconFinderFragment();
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(),"dialog");
            }
        });
        new getLocationTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.beacon_detail, container, false);
        // Show the dummy content as text in a TextView.
        if (mBeacon != null) {
            Log.i("asdf",mBeacon.getAddress());
            ((TextView) rootView.findViewById(R.id.beacon_detail)).setText(getArguments().getString(ARG_ITEM_MAC));
        }
        else {
            Log.i("asdf","123");
        }

        return rootView;
    }

    public class getLocationTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            HashMap<String,String> h = new HashMap<>();
            h.put("mac", String.valueOf(mBeacon.getAddress()));
            JSONObject response = RequestHandler.sendPostRequest(Globals.URL + "getLocation.php", h);
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
            if(jsonObject == null) return;

                try {
                    latitude = jsonObject.getString("latitude");
                    longtitude = jsonObject.getString("longtitude");
                    Log.i("latlar",latitude);
                    Log.i("latlar",longtitude);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }
}
