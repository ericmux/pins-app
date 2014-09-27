package com.brh.pin;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

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

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        Location mCurrentLocation = mLocationClient.getLastLocation();

//        APIHandler apiHandler = new APIHandler();
//        apiHandler.savePost(new Post("Bizu!", location, "moco"));


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 16));
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

        feedView = (ListView) findViewById(R.id.feed_view);
        mapView = findViewById(R.id.map_view);

        this.pupulateFeedView();

        mLocationClient = new LocationClient(this, this, this);

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_view)).getMap();

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        btnFeed = (Button) findViewById(R.id.btn_feed);
        btnMap = (Button) findViewById(R.id.btn_map);
        btnFeed.setOnClickListener(this);
        btnMap.setOnClickListener(this);

//        map.addMarker(new MarkerOptions()
//                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
//                .position(new LatLng(0, 0)));

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

        if (clicked == btnMap && mapView.getVisibility() != View.VISIBLE) {
            feedView.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
        }

        if (clicked == btnFeed && feedView.getVisibility() != View.VISIBLE) {
            mapView.setVisibility(View.GONE);
            feedView.setVisibility(View.VISIBLE);
        }

    }

    private void pupulateFeedView() {
        String[] values = new String[] {
                "Sleeping all day, hacking all night",
                "Hackhaton!!!",
                "Sleepyy",
                "zzzzzzzzzzzz",
                "Sleeping all day, hacking all night",
                "Hackhaton!!!",
                "Sleepyy",
                "zzzzzzzzzzzz",
                "Sleeping all day, hacking all night",
                "Hackhaton!!!",
                "Sleepyy",
                "zzzzzzzzzzzz",
                "Sleeping all day, hacking all night",
                "Hackhaton!!!",
                "Sleepyy"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        feedView.setAdapter(adapter);
    }
}