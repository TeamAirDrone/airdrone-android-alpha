package com.stonedpotatoproductions.roots;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public final static String EXTRA_LAT = "com.stonedpotatoproductions.Roots.MESSAGE";
    public final static String EXTRA_LNG = "com.stonedpotatoproductions.Roots.MESSAGE";

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    double laValue;
    double lnValue;

    Button locanzeigen;
    TextView lonlatText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        locanzeigen = (Button) findViewById(R.id.button_maps);
        lonlatText = (TextView) findViewById(R.id.lnglatText);
        setSupportActionBar(toolbar);

        locanzeigen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = "Lat: " + lnValue + " Lng: "+ laValue;
                lonlatText.setText(s);
            }
        });
    }



    @Override
    public void onConnected(Bundle bundle) {
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            else {
                handleNewLocation(location);
            }
        }
        catch(SecurityException ex)
        {

        }
    }

    private void handleNewLocation(Location location) {
        //Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        laValue = location.getLatitude();
        lnValue = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
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
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
            //Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    public void mkf_marker(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        EditText lat = (EditText) findViewById(R.id.txt_lat);
        EditText lng = (EditText) findViewById(R.id.txt_lng);
        double iLat = Double.parseDouble(lat.getText().toString());
        double iLng = Double.parseDouble(lng.getText().toString());

        Bundle b = new Bundle();
        b.putDouble("lat", iLat);
        b.putDouble("lng", iLng);
        intent.putExtras(b);
        startActivity(intent);
    }



    public void goDetails(View view){
        Intent intent = new Intent(this, Details.class);
        startActivity(intent);
    }

    public void goMaps(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        double iLat = laValue;
        double iLng = lnValue;
        Bundle b = new Bundle();

        Log.i("LatLng","Lat: "+ laValue+" Lng: "+lnValue);

        b.putDouble("lat", iLat);
        b.putDouble("lng", iLng);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
