package com.toilets.go;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.GPSTracker;
import com.toilets.go.utills.Session;
import com.toilets.go.databinding.ActivitySearchLocationMapBinding;

public class SearchLocationMapAct  extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap gMap;
    GPSTracker gpsTracker;
    ActivitySearchLocationMapBinding binding;
    Marker marker;
    String lat = "";
    String lon = "";
    String useriddeivce = "";
    String friend_idlast = "";
    String friendnamelast = "";
    String friendimage = "";
    String getChatImage = "";
    String from = "";
    String id = "";
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_location_map);
        session = new Session(this);
        try {
            from = getIntent().getStringExtra("from");

        } catch (Exception e) {

        }
        gpsTracker = new GPSTracker(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(SearchLocationMapAct.this);
        binding.sendBtn.setOnClickListener(v -> {

            onBackPressed();
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            gMap = googleMap;

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);

            LatLng sydney = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            binding.eventLocation.setText(DataManager.CurrentCity(
                    gpsTracker.getLatitude() ,gpsTracker.getLongitude(),getApplicationContext()));
            session.setHOME_LAT(lat);
            session.setHOME_LONG(lon);
            session.setRestraID(binding.eventLocation.getText().toString().trim());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(sydney)      // Sets the center of the map to location user
                    .zoom(13)                   // Sets the zoom
                    .build();
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            gMap.setOnMapClickListener(latLng -> {
                gMap.clear();

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.addMarker(markerOptions);
                lat = "" + latLng.latitude;
                lon = "" + latLng.longitude;
                binding.eventLocation.setText(DataManager.CurrentCity(
                        latLng.latitude
                        ,latLng.longitude
                        ,getApplicationContext()));
                session.setHOME_LAT(lat);
                session.setHOME_LONG(lon);
                session.setRestraID(binding.eventLocation.getText().toString().trim());
            });

        } catch (Exception e) {
            Log.d("TAG", "onMapReady: " + e);
        }
    }
}