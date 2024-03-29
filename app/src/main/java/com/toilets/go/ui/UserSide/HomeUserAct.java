package com.toilets.go.ui.UserSide;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.toilets.go.R;
import com.toilets.go.databinding.ActivityHomeUserBinding;
import com.toilets.go.models.SuccessResProfile;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.LocationHandler;
import com.toilets.go.utills.Session;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeUserAct extends AppCompatActivity {
    private static final int ENABLE_GPS = 3030;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    ActivityHomeUserBinding binding;
    Location mLocation;
    // NavController navController;
    Session session;
    private GosInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_user);
        // navController    = Navigation.findNavController(this, R.id.nav_host_fragment);
        binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        session = new Session(this);
        apiInterface = ApiClient.getClient().create(GosInterface.class);


        setUpNavigationDrawer();
        if (!checkPermission()) {
            requestPermission();
        } else {
            enableGPSAutomatically();
        }

    }

    @Override
    protected void onResume() {
        getUserProfileAPI();
        super.onResume();
    }

    private void getUserProfileAPI() {
        DataManager.getInstance().showProgressMessage(HomeUserAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        map.put("token", session.getAuthtoken());
        Call<SuccessResProfile> call = apiInterface.get_profile(map);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<SuccessResProfile> call,
                                   @NonNull Response<SuccessResProfile> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResProfile data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equals("1")) {
                        binding.drawerLayout.setModel(response.body().getResult());
                        Log.e("TAG", "onResponse: " + response.body().getResult().getWallet());
                        session.setWALLET_BALANCE(response.body().getResult().getWallet());

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SuccessResProfile> call, @NonNull Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }

    public void hideBadge() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START))
            binding.drawer.closeDrawer(GravityCompat.START);
        else binding.drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)){
            binding.drawer.closeDrawer(GravityCompat.START);}
        else {
           /* AlertDialog.Builder builder = new AlertDialog.Builder(HomeUserAct.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.logo);
            builder.setMessage("Do you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> finish())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();*/
            super.onBackPressed();

        }

    }

    private void setUpNavigationDrawer() {


        binding.drawerLayout.tvProfile.setOnClickListener(v ->
                {
                    binding.drawer.closeDrawer(GravityCompat.START);
                    Navigation.findNavController(binding.navHostFragment)
                            .navigate(R.id.action_navigation_home_to_profile_fragment);

                }
        );

        binding.drawerLayout.tvBooking.setOnClickListener(v ->
                {
                    binding.drawer.closeDrawer(GravityCompat.START);
                    Navigation.findNavController(binding.navHostFragment)
                            .navigate(R.id.action_navigation_home_to_booking);

                }
        );
        binding.drawerLayout.tvNotification.setOnClickListener(v ->
                {
                    binding.drawer.closeDrawer(GravityCompat.START);
                    Navigation.findNavController(binding.navHostFragment)
                            .navigate(R.id.action_navigation_home_to_notification);

                }
        );
        binding.drawerLayout.tvReview.setOnClickListener(v ->
                {
                    binding.drawer.closeDrawer(GravityCompat.START);
                    Navigation.findNavController(binding.navHostFragment)
                            .navigate(R.id.action_navigation_home_to_review_fragment);

                }
        );
        binding.drawerLayout.tvContactUs.setOnClickListener(v ->
                {
                    binding.drawer.closeDrawer(GravityCompat.START);
                    Navigation.findNavController(binding.navHostFragment)
                            .navigate(R.id.action_navigation_home_to_contact_us_fragment);

                }
        );


        binding.drawerLayout.btnLogin.setOnClickListener(v ->
                {
                    session.logout();
                }
        );
        binding.drawerLayout.tvWallet.setOnClickListener(v ->
                {
                    binding.drawer.closeDrawer(GravityCompat.START);
                    Navigation.findNavController(binding.navHostFragment)
                            .navigate(R.id.action_user_home_to_walletFragment);

                }
        );


    }

    private void requestPermission() {
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

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel(
                                        "You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION,
                                                                    ACCESS_COARSE_LOCATION},
                                                            REQUEST_LOCATION_PERMISSION);
                                                }
                                            }
                                        });
                                return;
                            }
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
                    l_resolvable.startResolutionForResult(HomeUserAct.this, ENABLE_GPS);
                } catch (Exception e) {
                }
            }
        });
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(HomeUserAct.this)
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
        new LocationHandler(HomeUserAct.this, null,
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
                                    session.setRestraID(DataManager.CurrentCity(lat, longitude, getApplicationContext()));
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