package com.ingenious.fellas.beaconstaj.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Classes.RequestHandler;
import com.ingenious.fellas.beaconstaj.Fragments.BeaconDetailFragment;
import com.ingenious.fellas.beaconstaj.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BeaconMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String latitude,longtitude;
    MarkerOptions a = new MarkerOptions()
            .position(new LatLng(50,6));
    Marker m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_beacon_map);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(BeaconDetailFragment.ARG_ITEM_NAME,
                    getIntent().getStringExtra(BeaconDetailFragment.ARG_ITEM_NAME));
            arguments.putString(BeaconDetailFragment.ARG_ITEM_MAC,
                    getIntent().getStringExtra(BeaconDetailFragment.ARG_ITEM_MAC));
            BeaconDetailFragment fragment = new BeaconDetailFragment();
            fragment.setArguments(arguments);


            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.beacon_map);
            mapFragment.getMapAsync(this);
        }
        new getLocationTask().execute(getIntent().getStringExtra(BeaconDetailFragment.ARG_ITEM_MAC));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        m = mMap.addMarker(a);

        //while(latitude == null);
        // Add a marker in Sydney and move the camera
        //LatLng place = new LatLng(Double.valueOf(latitude),Double.valueOf(longtitude));
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(m.getPosition()));
    }

    public class getLocationTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            HashMap<String, String> h = new HashMap<>();
            h.put("mac", String.valueOf(params[0]));
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
            if (jsonObject == null) return;

            try {
                latitude = jsonObject.getString("latitude");
                longtitude = jsonObject.getString("longtitude");



                m.setPosition(new LatLng(Double.valueOf(latitude),Double.valueOf(longtitude)));
                LatLngBounds.Builder builder = new LatLngBounds.Builder().include(m.getPosition());
                LatLngBounds latLngBounds = builder.build();

                int padding = 15;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds,padding);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(m.getPosition()));
                mMap.animateCamera(cameraUpdate);

                Log.i("latlar", latitude);
                Log.i("latlar", longtitude);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
