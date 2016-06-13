package com.ingenious.fellas.beaconstaj.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Fragments.BeaconDetailFragment;
import com.ingenious.fellas.beaconstaj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of My Beacons. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BeaconDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BeaconListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ArrayList<Beacon> dummyBeacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // TODO: Burasi username olcak sonradan
        toolbar.setTitle(Globals.email+"'s Beacons");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BeaconListActivity.this, NewBeaconActivity.class));
            }
        });

        dummyBeacons = new ArrayList<>();
        for(int i=1;i<=20;i++){
            dummyBeacons.add(new Beacon("Name " + i, "Mac address " + i, i ));
        }

        View recyclerView = findViewById(R.id.beacon_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.beacon_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(dummyBeacons));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Beacon> mBeacons;

        public SimpleItemRecyclerViewAdapter(List<Beacon> items) {
            mBeacons = items;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.beacon_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mBeacons.get(position);
            holder.mIdView.setText(mBeacons.get(position).getName());
            holder.mContentView.setText(mBeacons.get(position).getAddress());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(BeaconDetailFragment.ARG_ITEM_NAME, holder.mItem.getName());
                        arguments.putString(BeaconDetailFragment.ARG_ITEM_MAC, holder.mItem.getAddress());
                        BeaconDetailFragment fragment = new BeaconDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.beacon_detail_container, fragment)
                                .commit();
                    } else {
                        Log.i("asdf", holder.mItem.getName() + " " + holder.mItem.getAddress());
                        Context context = v.getContext();
                        Intent intent = new Intent(context, BeaconDetailActivity.class);
                        intent.putExtra(BeaconDetailFragment.ARG_ITEM_NAME, holder.mItem.getName());
                        intent.putExtra(BeaconDetailFragment.ARG_ITEM_MAC, holder.mItem.getAddress());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mBeacons.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Beacon mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.logout){
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("UserPreferences",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(BeaconListActivity.this , MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
