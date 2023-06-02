package com.toilets.go.utills;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import java.util.List;

public class LocationHandler {
    private final Context context;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private final LocationCallback locationCallback;
    private static LocationRequest locationRequest;
    private final OnLocationUpdateListener onLocationUpdateListener;
    private boolean updateStartedInternally = false;
    private static long UPDATE_INTERVAL = 5 * 1000,  /* 5 secs */
            FASTEST_INTERVAL = 2000; /* 2 sec */
    final private ResultReceiver resultReceiver;

    //-------------------------------------------------------------------------
    public LocationHandler(Context p_context, ResultReceiver p_resultReceiver,
                           OnLocationUpdateListener p_onLocationUpdateListener,
                           long p_updateInterval, long p_fastestInterval) {
        this.context = p_context;
        this.resultReceiver = p_resultReceiver;
        this.onLocationUpdateListener = p_onLocationUpdateListener;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(p_context);
        UPDATE_INTERVAL = p_updateInterval;
        FASTEST_INTERVAL = p_fastestInterval;
        createLocationRequest();
        getDeviceLocation();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                try {
                    List<Location> l_locationList = locationResult.getLocations();
                    if (l_locationList.size() > 0) {
                        //The last location in the list is the newest
                        Location l_location = l_locationList.get(l_locationList.size() - 1);
                        lastKnownLocation = l_location;
                        if (p_onLocationUpdateListener != null) {
                            p_onLocationUpdateListener.onLocationChange(l_location);
                            if (updateStartedInternally) {
                                stopLocationUpdate();
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        };
    }

    //-------------------------------------------------------------------------
    private void getDeviceLocation() {
        try {
            Task<Location> l_locationResult = fusedLocationProviderClient.getLastLocation();
            l_locationResult.addOnCompleteListener(p_task -> {
                if (p_task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = p_task.getResult();
                    if (lastKnownLocation == null) {
                        updateStartedInternally = true;
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    } else {
                        onLocationUpdateListener.onLocationChange(lastKnownLocation);

                    }
                } else {
                    onLocationUpdateListener.onError("Can't get Location");
                }
            });
        } catch (SecurityException e) {
            onLocationUpdateListener.onError(e.getMessage());
        }
    }

    //-------------------------------------------------------------------------
    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    //-------------------------------------------------------------------------
    //other new Methods but not using right now..
    public static LocationRequest createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);//set the interval in which you want to get locations
        locationRequest.setFastestInterval(FASTEST_INTERVAL);//if a location is available sooner you can get it (i.e. another app is using the location services)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public interface OnLocationUpdateListener {
        void onLocationChange(Location location);

        void onError(String error);
    }

}

