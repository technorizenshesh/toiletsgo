package com.toilets.go.ui.ProviderHome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.toilets.go.R;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.LocationHandler;
import com.toilets.go.utills.Session;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ProviderHomeActivity extends AppCompatActivity   {

    static BottomNavigationView navView;
    private static final int ENABLE_GPS = 3030;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    Location mLocation;
    Session session ;
    public void hideBadge() {
        BottomNavigationItemView itemView = navView.findViewById(R.id.provider_home);
        itemView.performClick();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_provider_home);
        navView = findViewById(R.id.nav_view);
        session = new Session(this);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_user);

       /* AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_setting, R.id.navigation_profile)
                .build();*/
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        if (!checkPermission()) {
            requestPermission();
        } else {
            enableGPSAutomatically();
        }

    }    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0) {

                    boolean coareAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean fineAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (coareAccepted && fineAccepted)
                        enableGPSAutomatically();
                    else {

                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                            showMessageOKCancel(
                                    "You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{ACCESS_FINE_LOCATION,
                                                            ACCESS_COARSE_LOCATION},
                                                    REQUEST_LOCATION_PERMISSION);
                                        }
                                    });
                            return;
                        }

                    }
                }


                break;
        }
    }
    private void enableGPSAutomatically() {
        LocationSettingsRequest.Builder l_builder = new LocationSettingsRequest.Builder().
                addLocationRequest(LocationHandler.createLocationRequest());
        l_builder.setAlwaysShow(true); // this is the key ingredient
        SettingsClient l_settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> l_gpsSettingTask = l_settingsClient.checkLocationSettings(l_builder.build());
        l_gpsSettingTask.addOnSuccessListener(this, p_locationSettingsResponse -> getLocationData());
        l_gpsSettingTask.addOnFailureListener(this, p_exception -> {
            if (p_exception instanceof ResolvableApiException) {
                try {
                    ResolvableApiException l_resolvable = (ResolvableApiException) p_exception;
                    l_resolvable.startResolutionForResult(ProviderHomeActivity.this, ENABLE_GPS);
                } catch (Exception e) {
                }
            }
        });
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ProviderHomeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void getLocationData() {
        long FASTEST_INTERVAL = 50000;
        long UPDATE_INTERVAL = 5 * 1000;
        new LocationHandler(ProviderHomeActivity.this, null,
                new LocationHandler.OnLocationUpdateListener() {
                    @Override
                    public void onLocationChange(Location p_location) {
                        try {
                            mLocation = p_location;
                            if (mLocation.getLatitude() != 0.0 && mLocation.getLongitude() != 0.0) {
                                try {
                                    double lat = mLocation.getLatitude();
                                    double longitude = mLocation.getLongitude();
                                    session.setHOME_LAT(String.valueOf(lat));
                                    session.setHOME_LONG(String.valueOf(longitude));
                                    session.setRestraID(DataManager.CurrentCity(lat,longitude,getApplicationContext()));
                                    Log.e("TAG", "onLocationChange:   lat  --" + String.valueOf(lat) +
                                            "---lang---" + String.valueOf(longitude));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onError(String error) {
                    }
                }, UPDATE_INTERVAL, FASTEST_INTERVAL);
    }



}