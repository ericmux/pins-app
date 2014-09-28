package com.brh.pin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.brh.pin.api.APIHandler;
import com.brh.pin.model.LatLongL;
import com.brh.pin.model.Post;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.*;

public class FeedActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, View.OnClickListener {

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationClient mLocationClient;
    private GoogleMap map;
    private ListView feedView;
    private View mapView;
    private Button btnFeed;
    private Button btnMap;
    private Button btnPin;
    private Button btnSettings;
    private Button btnRecent;
    private Button btnFilter;
    boolean connected = false;
    private ArrayList<String> feedPosts;
    private SharedPreferences sharedpreferences;
    private static Map<String, Float> tagColors = new HashMap<String, Float>();
    private static Map<String, Integer> tagCbIds = new HashMap<String, Integer>();
    private static Map<String, Boolean> tagMapShow = new HashMap<String, Boolean>();

    static {
        tagColors.put("event", BitmapDescriptorFactory.HUE_YELLOW);
        tagColors.put("study", BitmapDescriptorFactory.HUE_BLUE);
        tagColors.put("warning", BitmapDescriptorFactory.HUE_RED);
        tagColors.put("sport", BitmapDescriptorFactory.HUE_GREEN);
        tagColors.put("party", BitmapDescriptorFactory.HUE_ORANGE);
        tagColors.put("random", BitmapDescriptorFactory.HUE_VIOLET);

        tagCbIds.put("event", R.id.cb_event);
        tagCbIds.put("study", R.id.cb_study);
        tagCbIds.put("warning", R.id.cb_warning);
        tagCbIds.put("sport", R.id.cb_sport);
        tagCbIds.put("party", R.id.cb_party);
        tagCbIds.put("random", R.id.cb_random);

        tagMapShow.put("event", true);
        tagMapShow.put("study", true);
        tagMapShow.put("warning", true);
        tagMapShow.put("sport", true);
        tagMapShow.put("party", true);
        tagMapShow.put("random", true);
    }

    ;

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
//        connected = true;
//        Display the connection status
//        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        refreshFeedAndMap();

        Location mCurrentLocation = mLocationClient.getLastLocation();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 16));
    }

    private void refreshFeedAndMap() {
        Location mCurrentLocation = mLocationClient.getLastLocation();

        APIHandler apiHandler = new APIHandler();
        Log.e("pinapp", "calling getPosts");
        apiHandler.getPosts(new LatLongL(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                new APIHandler.GetPostsListener() {
                    @Override
                    public void onGotPosts(List<Post> posts) {
                        map.clear();
                        Log.e("pinapp", "onGotPosts");
                        Log.e("pinapp", "number of posts received: " + posts.size());

                        clearFeed();
                        Collections.sort(posts, new Comparator<Post>() {
                            @Override
                            public int compare(Post lhs, Post rhs) {
                                return ((int) (rhs.getAge() - lhs.getAge()));
                            }
                        });
                        for (Post post : posts) {
                            if (!tagMapShow.get(post.getTags().get(0)))
                                continue;

                            Log.e("pinapp", post.toString());
                            addMarkerFromPost(post);

                            // add to feed
                            String itemContent;
                            itemContent = "\"" + post.getContent() + "\"";
                            itemContent += " - " + post.getCreator() + "\n";

                            for (String tag : post.getTags()) {
                                itemContent += "#" + tag + " ";
                            }

                            itemContent += "\n";
                            itemContent += (int) post.getAge() / 60 + " min";
                            addItemList(itemContent);
                        }
                    }
                });

    }

    private void clearFeed() {
        feedPosts.clear();
        ((ArrayAdapter<String>) feedView.getAdapter()).notifyDataSetChanged();
    }

    protected void addItemList(String str) {
        feedPosts.add(0,str);
        ((ArrayAdapter<String>) feedView.getAdapter()).notifyDataSetChanged();
    }

    void showErrorDialog(int error) {
        Toast.makeText(this, "Error code: " + error, Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        sharedpreferences = this.getSharedPreferences("MyPref", 0);

        feedView = (ListView) findViewById(R.id.feed_view);
        mapView = findViewById(R.id.map_view);

        this.populateFeedView();

        mLocationClient = new LocationClient(this, this, this);

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_view)).getMap();

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);



        btnFeed = (Button) findViewById(R.id.btn_feed);
        btnMap = (Button) findViewById(R.id.btn_map);
        btnPin = (Button) findViewById(R.id.btn_pin);
        btnSettings = (Button) findViewById(R.id.btn_settings);
        btnRecent = (Button) findViewById(R.id.btn_recent);
        btnFilter = (Button) findViewById(R.id.btn_filter);
        btnFeed.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnPin.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnRecent.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        sharedpreferences = this.getSharedPreferences("MyPref", 0);

        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    /*
    * Called when the Activity is no longer visible.
    */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View clicked) {

        if (clicked == btnPin) {
            if (!connected) {
                Toast.makeText(this, "Maps not connected.", Toast.LENGTH_SHORT);
            }
            final Post post = new Post();
            Location mLocation = mLocationClient.getLastLocation();
            post.setLatitude(mLocation.getLatitude());
            post.setLongitude(mLocation.getLongitude());
            post.setCreator(sharedpreferences.getString("username", "user_not_found"));
            createNewPinDialog(post, new Runnable() {
                @Override
                public void run() {
                    APIHandler apiHandler = new APIHandler();
                    apiHandler.savePost(post);

//                    addMarkerFromPost(post);
                }
            }, null).show();
        }
        else if (clicked == btnMap && mapView.getVisibility() != View.VISIBLE) {
            feedView.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
        }
        else if (clicked == btnFeed && feedView.getVisibility() != View.VISIBLE) {
            mapView.setVisibility(View.GONE);
            feedView.setVisibility(View.VISIBLE);
        }
        else if (clicked == btnSettings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (clicked == btnRecent) {
            refreshFeedAndMap();
        }
        else if (clicked == btnFilter) {
            openFilterDialog();
        }

    }

    private void openFilterDialog() {
        final View dialogContent = View.inflate(this, R.layout.filter_dialog, null);

        for (String tag : tagCbIds.keySet()) {
            ((CheckBox) dialogContent.findViewById(tagCbIds.get(tag))).setChecked(tagMapShow.get(tag));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tag Filtering:")
                .setView(dialogContent)
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String tag : tagCbIds.keySet()) {
                            if (!(((CheckBox) dialogContent.findViewById(tagCbIds.get(tag))).isChecked())) {
                                tagMapShow.put(tag, false);
                            }
                            else
                                tagMapShow.put(tag, true);
                        }
                        refreshFeedAndMap();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
            }
        });

        dialog.show();
    }

    private void populateFeedView() {
        feedPosts = new ArrayList<String>();
        feedPosts.clear();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.feed_item, R.id.feed_item_text, feedPosts);

        feedView.setAdapter(adapter);
    }

    /**
     * Fills the post given with content typed by the user.
     * @param post
     * @param success
     * @param failure
     * @return
     */
    private AlertDialog createNewPinDialog(final Post post, final Runnable success, final Runnable failure) {
        View dialogContent = View.inflate(this, R.layout.edit_dialog, null);
        final TextView etContentEvent = (TextView) dialogContent
                .findViewById(R.id.content_event);

        etContentEvent.setText("Description here...");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Event Creation:")
                .setView(dialogContent)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        post.setContent(etContentEvent.getText().toString());
                        if (success != null)
                            success.run();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (failure != null)
                            failure.run();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (failure != null)
                    failure.run();
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                etContentEvent.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etContentEvent, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        return dialog;
    }

    private void addMarkerFromPost(Post post) {
        StringBuilder builder = new StringBuilder();
        for (String tag : post.getTags()) {
            builder.append("#" + tag + " ");
        }

        map.addMarker(new MarkerOptions()
                .anchor(0.5f, 1.0f)
                .position(new LatLng(post.getLatitude(), post.getLongitude()))
                .title(builder.toString())
                .snippet(post.getContent())
                .icon(BitmapDescriptorFactory.defaultMarker(tagColors.get(post.getTags().get(0)))));
    }


//    class PopupAdapter implements GoogleMap.InfoWindowAdapter {
//        LayoutInflater inflater=null;
//
//        PopupAdapter(LayoutInflater inflater) {
//            this.inflater=inflater;
//        }
//
//        @Override
//        public View getInfoWindow(Marker marker) {
//            return(null);
//        }
//
//        @Override
//        public View getInfoContents(Marker marker) {
//            View popup = inflater.inflate(R.layout.popup, null);
//
//            TextView tv = (TextView) popup.findViewById(R.id.title);
//
//            tv.setText(marker.getTitle());
//            tv = (TextView) popup.findViewById(R.id.snippet);
//            tv.setText(marker.getSnippet());
//
//            return(popup);
//        }
//    }
}