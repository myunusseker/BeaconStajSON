package com.ingenious.fellas.beaconstaj.Fragments;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ingenious.fellas.beaconstaj.Activities.BeaconDetailActivity;
import com.ingenious.fellas.beaconstaj.Activities.BeaconListActivity;
import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.R;

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
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_NAME));
            mBeacon = new Beacon(getArguments().getString(ARG_ITEM_NAME)
                    ,getArguments().getString(ARG_ITEM_MAC), 1);


            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mBeacon.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.beacon_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mBeacon != null) {
            Log.i("asdf","123" + mBeacon.getAddress());
            ((TextView) rootView.findViewById(R.id.beacon_detail)).setText(getArguments().getString(ARG_ITEM_MAC));
        }
        else {
            Log.i("asdf","123");
        }

        return rootView;
    }
}
