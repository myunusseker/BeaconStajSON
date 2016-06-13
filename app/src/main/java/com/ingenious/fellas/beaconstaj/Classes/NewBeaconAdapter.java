package com.ingenious.fellas.beaconstaj.Classes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ingenious.fellas.beaconstaj.R;

import java.util.List;

public class NewBeaconAdapter extends RecyclerView.Adapter<NewBeaconAdapter.MyViewHolder> {

    private List<Beacon> deviceList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView address, distance, rssi;
        public MyViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.address);
            rssi = (TextView) view.findViewById(R.id.rssi);
            distance = (TextView) view.findViewById(R.id.distance);
        }
    }


    public NewBeaconAdapter(List<Beacon> deviceList) {
        this.deviceList = deviceList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_beacon_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Beacon beacon = deviceList.get(position);
        holder.address.setText(beacon.getAddress());
        holder.rssi.setText(Integer.toString(beacon.getRssi()));
        holder.distance.setText(beacon.getDistance());
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

}