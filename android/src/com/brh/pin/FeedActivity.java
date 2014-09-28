package com.brh.pin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.brh.pin.api.APIHandler;
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
    private Button btnPin;
    boolean connected = false;

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        connected = true;
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        Location mCurrentLocation = mLocationClient.getLastLocation();




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
        btnPin = (Button) findViewById(R.id.btn_pin);
        btnFeed.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnPin.setOnClickListener(this);

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

        if (clicked == btnPin) {
            if (!connected) {
                Toast.makeText(this, "Maps not connected.", Toast.LENGTH_SHORT);
            }
            final Post post = new Post();
            post.setLocation(mLocationClient.getLastLocation());
            post.setCreator("moco");
            createNewPinDialog(post, new Runnable() {
                @Override
                public void run() {
                    APIHandler apiHandler = new APIHandler();
                    apiHandler.savePost(post);
                }
            }, null).show();
        }

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

        etContentEvent.setText("Evento bizurado!");

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
}