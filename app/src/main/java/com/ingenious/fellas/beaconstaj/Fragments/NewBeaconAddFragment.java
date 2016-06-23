package com.ingenious.fellas.beaconstaj.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Classes.RequestHandler;
import com.ingenious.fellas.beaconstaj.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NewBeaconAddFragment extends DialogFragment {

    private String mac, beaconName;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mac = getArguments().getString("mac");
        Log.i(Globals.TAG,"asfd" + mac);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder .setView(inflater.inflate(R.layout.dialog_newbeacon, null))
                .setTitle("Specify your device settings")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = (EditText) ((Dialog) dialog).findViewById(R.id.newBeaconName);
                        beaconName = editText.getText().toString();
                        new AddNewBeaconTask().execute(mac, beaconName,"null","null", String.valueOf(Globals.id));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
        return builder.create();
    }
    public class AddNewBeaconTask extends AsyncTask<String,Void,JSONObject>
    {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Globals.URL + "addbeacon.php";
            HashMap<String, String> h = new HashMap<>();
            h.put("mac", params[0]);
            h.put("beacon_name",params[1]);
            h.put("photo",params[2]);
            h.put("icon",params[3]);
            h.put("user_id",params[4]);

            JSONObject jsonData = RequestHandler.sendPostRequest(url, h);

            return jsonData;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                String status_message = jsonObject.getString("status_message");
                if(status_message.equals("success"))
                {
                    Log.i("YUNUS","EKLEDIM");
                    Toast.makeText(context,beaconName + "\n" + mac + "\nis successfully added.",Toast.LENGTH_LONG).show();
                    Globals.myBeacons.add(new Beacon(beaconName,mac));
                }
                else
                {
                    Log.i("YUNUS",status_message);
                    if(context != null)
                        Toast.makeText(context,status_message,Toast.LENGTH_LONG).show();
                    else
                        Log.i("asdf", "activity yok aq");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}