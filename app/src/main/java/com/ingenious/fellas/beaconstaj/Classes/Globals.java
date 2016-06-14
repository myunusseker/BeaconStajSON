package com.ingenious.fellas.beaconstaj.Classes;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mehmet on 09/06/16.
 */
public class Globals {
    public static String email, username, password ,namesurname;
    public static String TAG = "asdf";
    public static int id;

    public static void initialize(Context applicationContext) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        email = sharedPref.getString("email","nullUser");
        username = sharedPref.getString("username","nullUser");
        password = sharedPref.getString("password","nullUser");
        namesurname = sharedPref.getString("namesurname","nullUser");
        id = sharedPref.getInt("id",-1);
    }
}
