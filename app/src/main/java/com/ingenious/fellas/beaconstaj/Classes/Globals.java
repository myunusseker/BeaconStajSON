package com.ingenious.fellas.beaconstaj.Classes;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ingenious.fellas.beaconstaj.Activities.BeaconFinderActivity;
import com.ingenious.fellas.beaconstaj.Activities.NewBeaconActivity;
import com.ingenious.fellas.beaconstaj.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mehmet on 09/06/16.
 */
public class Globals{
    public static String email, username, password ,namesurname;
    public static String TAG = "asdf", URL = "http://188.166.29.184/";
    public static int id;
    public static ArrayList<Beacon> myBeacons = new ArrayList<>();
    public static int whichActivity=0;
    public static List<Beacon> beaconsAround = new ArrayList<>();
    public static Location mLastLocation;

    public static ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            int rssi = result.getRssi();
            BluetoothDevice device = result.getDevice();
            if(whichActivity==1){
                Log.i(TAG, "W=1 ve Beacon bulundu");
                Beacon newBeacon = new Beacon(device.getName(), device.getAddress(),rssi);
                Log.i(TAG, "RSSI degeri: " + rssi + " MAC: " + newBeacon.getAddress());
                if (!Globals.doesBeaconsAround(newBeacon.getAddress())){
                    Log.i(TAG, "Yeni beacon listeye ekleniyor, Konum Gonderiliyor");
                    beaconsAround.add(newBeacon);
                    new Globals.sendLocationTask().execute(newBeacon);
                }
                else {
                    Log.i(TAG, "Beacon Guncelleniyor");
                    for (Beacon beacon : beaconsAround) {
                        if (beacon.getAddress().equalsIgnoreCase(newBeacon.getAddress())) {
                            beacon.setRssi(newBeacon.getRssi());
                            break;
                        }
                    }
                }
                NewBeaconActivity.mAdapter.notifyDataSetChanged();
            }
            else if (whichActivity == 2){
                Log.i("YUNUS", "Hedeflenen Cihaz icin Tarama Basladi");
                String foundMac = device.getAddress();
                if(foundMac.equalsIgnoreCase(BeaconFinderActivity.mac)){
                    Log.i("YUNUS", String.valueOf(rssi));
                    (BeaconFinderActivity.finderRssi).setText(String.valueOf(rssi));
                    BeaconFinderActivity.mBeacon.setRssi(rssi);
                    Log.i("YUNUS",BeaconFinderActivity.mBeacon.getDistance());
                    (BeaconFinderActivity.finderResult).setText(BeaconFinderActivity.mBeacon.getDistance());
                    BeaconFinderActivity.toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,rssi*10+1000);
                    if(rssi>-38){
                        (BeaconFinderActivity.finderResult).setText("You have found");
                    }
                }
            }
            else if (whichActivity == 3){
                Log.i(TAG, "Arkaplanda Cihaz Bulundu");
                Beacon newBeacon = new Beacon(device.getName(), device.getAddress(),rssi);
                Log.i(TAG, "rssi degisimi: " + rssi + " MAC: " + newBeacon.getAddress());
                if(!Globals.doesBeaconsAround(newBeacon.getAddress())) {
                    beaconsAround.add(newBeacon);
                    Log.i(TAG, "Ilk defa goruldu, Konum Gonderilecek");
                    new Globals.sendLocationTask().execute(newBeacon);
                }
            }
        }
    };


    public static void initialize(Context applicationContext) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        email = sharedPref.getString("email","nullUser");
        username = sharedPref.getString("username","nullUser");
        password = sharedPref.getString("password","nullUser");
        namesurname = sharedPref.getString("namesurname","nullUser");
        id = sharedPref.getInt("id",-1);

    }


    public static boolean doesBeaconsExists(String mac){
        for (int i=0;i<myBeacons.size();i++)
            if(myBeacons.get(i).getAddress().equals(mac))
                return true;
        return false;
    }

    public static boolean doesBeaconsAround(String mac){
        for (int i=0;i<beaconsAround.size();i++)
            if(beaconsAround.get(i).getAddress().equals(mac))
                return true;
        return false;
    }

    public static Beacon getBeacon(String mac) {
        for(int i=0;i<myBeacons.size();i++)
        {
            if(myBeacons.get(i).getAddress().equals(mac))
                return myBeacons.get(i);
        }
        return null;
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static class sendLocationTask extends AsyncTask<Beacon,Void,Void> {

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
            hashMap.put("lastseen", Globals.getCurrentTimeStamp());
            Log.i("time",Globals.getCurrentTimeStamp());
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
