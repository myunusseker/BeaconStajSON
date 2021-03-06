package com.ingenious.fellas.beaconstaj.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.ingenious.fellas.beaconstaj.Classes.Beacon;
import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Classes.RequestHandler;
import com.ingenious.fellas.beaconstaj.Fragments.BeaconDetailFragment;
import com.ingenious.fellas.beaconstaj.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    private View recyclerView;
    private ProgressBar progressBar;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_list);
        Globals.whichActivity = 3;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(Globals.namesurname+"'s Beacons");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BeaconListActivity.this, NewBeaconActivity.class));
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.list_progress);
        recyclerView = findViewById(R.id.beacon_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.beacon_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        new getBeaconsTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new getBeaconsTask().execute();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Globals.myBeacons));
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
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mBeacons.get(position);
            holder.mIdView.setText(mBeacons.get(position).getName());
            holder.mContentView.setText(mBeacons.get(position).getAddress());
            if (!mBeacons.get(position).getIsNear())
                holder.icon.setBackgroundResource(R.drawable.icon_map_black);
            holder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mBeacons.get(position).getIsNear()) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, BeaconMapActivity.class);
                        intent.putExtra(BeaconDetailFragment.ARG_ITEM_NAME, holder.mItem.getName());
                        intent.putExtra(BeaconDetailFragment.ARG_ITEM_MAC, holder.mItem.getAddress());
                        context.startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(BeaconListActivity.this, BeaconFinderActivity.class);
                        intent.putExtra("mac", holder.mItem.getAddress());
                        v.getContext().startActivity(intent);
                    }
                }
            });
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
            public final ImageView icon;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                icon = (ImageView) view.findViewById(R.id.icon);
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

    public class getBeaconsTask extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            HashMap<String,String> h = new HashMap<>();
            h.put("user_id", String.valueOf(Globals.id));
            JSONObject response = RequestHandler.sendPostRequest(Globals.URL + "getbeacons.php", h);
            try {
                Log.i(Globals.TAG, response.getString("status_message"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(Globals.TAG, "response'u alamadik");
            }

            JSONArray arr = new JSONArray();
            try {
                if (response.getJSONArray("data") != null)
                    arr = response.getJSONArray("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arr;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null) return;
            Globals.myBeacons = new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Globals.myBeacons.add(new Beacon(jsonObject.getString("beacon_name"),
                            jsonObject.getString("mac"), 0));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("oopo", "bos bekleme basladi");
            for (int i=0;i<Integer.MAX_VALUE/50;i++);
            Log.i("oopo", "bos bekleme bitti");
            for(int i=0;i<Globals.myBeacons.size();i++){
                Globals.myBeacons.get(i).setIsNear(false);
                for(int j=0;j<Globals.beaconsAround.size();j++)
                    if(Globals.myBeacons.get(i).getAddress().equals(Globals.beaconsAround.get(j).getAddress()))
                        Globals.myBeacons.get(i).setIsNear(true);
            }
            progressBar.setVisibility(View.GONE);
            setupRecyclerView((RecyclerView) recyclerView);

        }
    }
}
