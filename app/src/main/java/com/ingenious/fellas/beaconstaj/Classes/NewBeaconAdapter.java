package com.ingenious.fellas.beaconstaj.Classes;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ingenious.fellas.beaconstaj.Activities.NewBeaconActivity;
import com.ingenious.fellas.beaconstaj.Fragments.NewBeaconAddFragment;
import com.ingenious.fellas.beaconstaj.R;

import java.util.List;

public class NewBeaconAdapter extends RecyclerView.Adapter<NewBeaconAdapter.MyViewHolder> {

    private final FragmentManager fragmentManager;
    private List<Beacon> beaconList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView address, distance, rssi;
        public MyViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.address);
            rssi = (TextView) view.findViewById(R.id.rssi);
            distance = (TextView) view.findViewById(R.id.distance);
        }
    }


    public NewBeaconAdapter(FragmentManager supportFragmentManager, List<Beacon> beaconList) {
        this.beaconList = beaconList;
        this.fragmentManager = supportFragmentManager;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_beacon_list, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Beacon beacon = beaconList.get(position);
        holder.address.setText(beacon.getAddress());
        holder.rssi.setText(Integer.toString(beacon.getRssi()));
        holder.distance.setText(beacon.getDistance());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewBeaconAddFragment dialog = new NewBeaconAddFragment();
                Beacon selected = beaconList.get(position);
                dialog.show(fragmentManager,"dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return beaconList.size();
    }

}