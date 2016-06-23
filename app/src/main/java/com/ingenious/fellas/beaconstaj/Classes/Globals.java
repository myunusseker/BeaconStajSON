package com.ingenious.fellas.beaconstaj.Classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by mehmet on 09/06/16.
 */
public class Globals {
    public static String email, username, password ,namesurname;
    public static String TAG = "asdf", URL = "http://188.166.29.184/";
    public static int id;
    public static ArrayList<Beacon> myBeacons = new ArrayList<>();


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

    public static Beacon getBeacon(String mac)
    {
        for(int i=0;i<myBeacons.size();i++)
        {
            if(myBeacons.get(i).getAddress().equals(mac))
                return myBeacons.get(i);
        }
        return null;
    }

}
