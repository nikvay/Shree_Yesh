package com.shreeyesh.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.shreeyesh.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusTrackFragment  extends Fragment implements
        AdapterView.OnItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    /* OnMapReadyCallback,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            LocationListener {*/

    ArrayList markerPoints = new ArrayList();
    String bookAddress;
    private Geocoder geocoder;
    private List<Address> list;
    private String lati, longi;
    //============= Map =======================
    Context mContext;
    boolean GpsStatus;

    SupportMapFragment mapFragment;
    MapView mapView;
    //        View mapView;
    GoogleMap mMap;
    View view;
    String city = "";
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    LatLng current_LatLang;
    Marker mCurrLocationMarker;
    public static double latitude, longitude;
    private double current_latitude, current_longitude;

    LocationManager manager;  //=============== END ==============

    public BusTrackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_track, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        locationOnOff();
        // =================================== Initialize Google API Client ===============================
        buildGoogleApiClient();//Init Google API Client

        return view;

    } //=========== End onCreate () ==========================

    private void find_All_IDs(View view) {
    }



    private void locationOnOff() {
        manager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (GpsStatus) {
            Toast.makeText(mContext, "Location Services Is Enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Location Services Is Disabled", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        getCurrentLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);

        LatLng current_LatLang = new LatLng(current_latitude, current_longitude);

        mMap.addMarker(new MarkerOptions()
                .position(current_LatLang)
                .title(city)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_LatLang));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_LatLang, 11));

//        mMap.addMarker(new MarkerOptions().position(new LatLng(18.5204, 73.8567)));
//        CameraPosition cameraPosition = CameraPosition.builder()
//                .target(new LatLng(18.5204, 73.8567))
//                .zoom(16)
//                .bearing(8)
//                .tilt(45)
//                .build();
//        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        current_LatLang = new LatLng(location.getLatitude(), location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("LatLang", current_latitude + "");

        mLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(city);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


  /*  private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            current_latitude = location.getLatitude();
            current_longitude = location.getLongitude();
            onMapReady(mMap);
        }
    }*/
    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            current_latitude = location.getLatitude();
            current_longitude = location.getLongitude();

            LatLng latLng = new LatLng(current_latitude, current_longitude);
            markerPoints.add(0, latLng);

            onMapReady(mMap); //refresh map

            getAddress(current_latitude, current_longitude);

        }
    }

    private void getAddress(double latitude, double longitude) {
        geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);

            if (list != null && list.size() > 0) {
                bookAddress = list.get(0).getAddressLine(0);
                String city = list.get(0).getLocality();
                String state = list.get(0).getAdminArea();
                String subArea = list.get(0).getSubAdminArea();
                String country = list.get(0).getCountryName();
                String postalCode = list.get(0).getPostalCode();
                String knownName = list.get(0).getFeatureName();

//                Toast.makeText(mContext, ""+bookAddress, Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, ""+knownName+","+city+","+state+","+country+","+postalCode, Toast.LENGTH_SHORT).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

