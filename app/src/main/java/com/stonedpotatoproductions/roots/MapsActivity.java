package com.stonedpotatoproductions.roots;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.common.ConnectionResult;

import java.util.concurrent.TimeUnit;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnMarkerDragListener,
        OnMapLongClickListener, OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnInfoWindowLongClickListener {

    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private int totalMarkers = 1;
    double latValue;
    double lngValue;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .mapToolbarEnabled(true);

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
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent intent = getIntent();

        if(intent.getExtras() != null) {
            Bundle b = intent.getExtras();
            latValue = b.getDouble("lat");
            lngValue = b.getDouble("lng");
        }else{
            latValue = 48.00;
            lngValue = 16.00;
        }

        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();
        // Add a marker in Sydney and move the camera

        LatLng cords = new LatLng(latValue, lngValue);
        Marker marker = mMap.addMarker(new MarkerOptions().position(cords).draggable(true).title("Marker Nr. " + totalMarkers));
        marker.setSnippet("Position: " + marker.getPosition());
        marker.showInfoWindow();
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(cords, 15);
        mMap.animateCamera(myLocation);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);


    }

    public void addMarker(View view){
        totalMarkers = totalMarkers + 1;
        LatLng center = mMap.getCameraPosition().target;
        String cords = center.toString();
        Marker marker = mMap.addMarker(new MarkerOptions().position(center).draggable(true).title("Marker Nr. " + totalMarkers).snippet("Position: " + cords));
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(center, 15);

        showToast();
        mMap.animateCamera(myLocation);
        marker.showInfoWindow();

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        totalMarkers = totalMarkers + 1;
        String cords = latLng.toString();
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Marker Nr. " + totalMarkers).snippet("Position: " + cords));
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(latLng, 15);

        showToast();
        mMap.animateCamera(myLocation);
        marker.showInfoWindow();

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        marker.setSnippet("Position: " + marker.getPosition().toString());
        marker.showInfoWindow();
    }

    public void showToast(){
        Context context = getApplicationContext();
        CharSequence text = "You added a marker!";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);

        toast.show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        marker.setSnippet("Position: " + marker.getPosition().toString());
        marker.showInfoWindow();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        marker.setSnippet("Position: " + marker.getPosition().toString());
        Log.i("Lat/Lng: ", marker.getPosition().toString());
        marker.showInfoWindow();


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
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
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        lngValue = location.getLatitude();
        latValue = location.getLongitude();

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
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onInfoWindowLongClick(Marker marker) {
        marker.remove();
        totalMarkers = totalMarkers - 1;
    }
};


