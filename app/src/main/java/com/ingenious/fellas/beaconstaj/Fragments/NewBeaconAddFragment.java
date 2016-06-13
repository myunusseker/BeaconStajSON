package com.ingenious.fellas.beaconstaj.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;

import com.ingenious.fellas.beaconstaj.R;

public class NewBeaconAddFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder .setView(inflater.inflate(R.layout.dialog_newbeacon, null))
                .setTitle("Please specify your device settings")
                .setIcon(R.drawable.ic_add)
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        Log.i("YUNUS","yey ekledim");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("YUNUS","ayyy vazgecti");
                    }
                });
        return builder.create();
    }
}
