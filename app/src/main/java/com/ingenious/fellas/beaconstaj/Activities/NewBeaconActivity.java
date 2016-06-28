package com.ingenious.fellas.beaconstaj.Activities;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Classes.NewBeaconAdapter;
import com.ingenious.fellas.beaconstaj.Classes.RequestHandler;
import com.ingenious.fellas.beaconstaj.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NewBeaconActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private RecyclerView recyclerView;
    public static  NewBeaconAdapter mAdapter;
    public static GoogleApiClient mGoogleApiClient;

    private static final String TAG = "MEHMET";

    private static Location mLastLocation;

    @Override
    protected void onStart() {
        Globals.whichActivity = 1;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        Log.i("aaa","mGoogleApiClient connected");
        mGoogleApiClient.connect();
        super.onStart();

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        Log.i("aaa","mGoogleApiClient disconnected");
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beacon);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Available Beacons");

        //------------Deneme

        //

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new NewBeaconAdapter(getSupportFragmentManager(),Globals.beaconsAround);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location newLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (newLocation == null)
            Log.i("aaa", "yeni konum null");
        else
            mLastLocation = newLocation;
        Log.i("aaa","konum aldik");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public static class sendLocationTask extends AsyncTask<Beacon,Void,Void>{

        @Override
        protected Void doInBackground(Beacon... params) {
            Beacon beacon = params[0];
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("mac",beacon.getAddress());
            if(mLastLocation == null) {
                Log.i("aaa", "mLastLocation null ki ");
                return null;
            }
            Log.i("aaa",beacon.getAddress());
            Log.i("aaa", String.valueOf(mLastLocation.getLatitude()));
            Log.i("aaa", String.valueOf(mLastLocation.getLongitude()));
            hashMap.put("latitude", String.valueOf(mLastLocation.getLatitude()));
            hashMap.put("longtitude", String.valueOf(mLastLocation.getLongitude()));
            JSONObject response = RequestHandler.sendPostRequest(Globals.URL + "addLocation.php", hashMap);
            if (response == null)
                Log.i("aaa","response null ");
            try {
                if (response!= null && response.get("status_message").equals("success"))
                    Log.i("aaa","adam gibi konumu yolladik");
                else {
                    Log.i("aaa", "adam gibi yollayamadik");
                    Log.i("aaa",response.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}