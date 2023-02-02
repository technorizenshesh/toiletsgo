package com.toilets.go.ui.UserSide.Home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.slider.LabelFormatter;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.toilets.go.R;
import com.toilets.go.databinding.BottemSheeetDetailsBinding;
import com.toilets.go.databinding.BottemSheeetFilterBinding;
import com.toilets.go.models.SuccessResBooking;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.GPSTracker;
import com.toilets.go.utills.Session;
import com.toilets.go.databinding.FragmentUserHomeBinding;
import com.toilets.go.models.SuccessResNearbyList;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.ui.UserSide.HomeUserAct;
import com.toilets.go.utills.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class UserHomeFragment extends Fragment implements OnMapReadyCallback {
    FragmentUserHomeBinding binding;
    Session session;
    private GosInterface apiInterface;
    private ArrayList<SuccessResNearbyList.Result> resultArrayList = new ArrayList<>();
    GoogleMap googleMap;
    GPSTracker gpsTracker;
    HashMap<Marker, SuccessResNearbyList.Result> datasnap = new HashMap<>();
    Geocoder geocoder;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    String price = "";
    String range = "";
    String rating = "";
    String lat = "";
    String lon = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_home, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gpsTracker = new GPSTracker(requireActivity());
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(requireActivity());
        lat = session.getHOME_LAT();
        lon = session.getHOME_LONG();
        Log.e(TAG, "getFireBaseTokengetFireBaseToken: "+session.getFireBaseToken() );

///rearch
        Places.initialize(requireActivity(), requireActivity().getString(R.string.api_key));
        placesClient = Places.createClient(requireActivity());
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        binding.searchBar.setEnabled(true);
        geocoder = new Geocoder(requireActivity(), Locale.getDefault());

        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //   startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    //opening or closing a navigation drawer
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    binding.searchBar.setEnabled(false);
                }
            }
        });
        binding.searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()

                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionsList = new ArrayList<>();
                                for (int i = 0; i < predictionList.size(); i++) {
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    suggestionsList.add(prediction.getFullText(null).toString());
                                }
                                binding.searchBar.updateLastSuggestions(suggestionsList);
                                if (!binding.searchBar.isSuggestionsVisible()) {
                                    binding.searchBar.showSuggestionsList();
                                }
                            }
                        } else {
                            Log.i("mytag", "prediction fetching task unsuccessful");
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.searchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = binding.searchBar.getLastSuggestions().get(position).toString();
                binding.searchBar.setPlaceHolder(suggestion);
                // binding.searchBar.setEnabled(false);
                binding.searchBar.clearSuggestions();
                binding.searchBar.closeSearch();
                binding.searchBar.hideSuggestionsList();

                new Handler().postDelayed(() -> binding.searchBar.clearSuggestions(), 500);
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(binding.searchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                final String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = List.of(Place.Field.LAT_LNG);
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(fetchPlaceResponse -> {
                    Place place = fetchPlaceResponse.getPlace();
                    Log.i("mytag", "Place found: " + place.getName());
                    LatLng latLngOfPlace = place.getLatLng();
                    if (latLngOfPlace != null) {
                        lat = "" + latLngOfPlace.latitude;
                        lon = "" + latLngOfPlace.longitude;
                        getnearByUsersAPI();
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, 14));
                    }
                }).addOnFailureListener(e -> {
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        apiException.printStackTrace();
                        int statusCode = apiException.getStatusCode();
                        Log.i("mytag", "place not found: " + e.getMessage());
                        Log.i("mytag", "status code: " + statusCode);
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });


        binding.menu.setOnClickListener(v -> {
            try {
                HomeUserAct activity = (HomeUserAct) getActivity();
                activity.hideBadge();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.filter.setOnClickListener(v -> {
            try {
                openFilterBottem();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        getnearByUsersAPI();
        super.onResume();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap g) {
        try {
            googleMap = g;

            if (ActivityCompat.checkSelfPermission(requireActivity()
                    , Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.
                    checkSelfPermission(requireActivity()
                            , Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

            try {
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                requireActivity(), R.raw.map_style));
                if (!success) {
                    Log.e("TAG", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("TAG", "Can't find style. Error: ", e);
            }
            if (gpsTracker.canGetLocation()) {

                LatLng sydney = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(sydney)      // Sets the center of the map to location user
                        .zoom(14)                   // Sets the zoom// Sets the tilt of the camera to 30 degrees
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            } else {
                Toast.makeText(requireActivity(), R.string.no_location, Toast.LENGTH_SHORT).show();
            }
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    SuccessResNearbyList.Result data = datasnap.get(marker);
                    binding.mrkerLay.setDatamodel(data);
                    binding.mrkerLay.viewDtl.setOnClickListener(v -> {
                        openBottemSheet(data);
                    });
                    binding.mrkerLay.getRoot().setVisibility(View.VISIBLE);
                    //  openBottemSheet(data);
                    return false;
                }
            });
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    Log.e(TAG, "onMapClick: " + latLng);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openBottemSheet(SuccessResNearbyList.Result data) {
        Log.e(TAG, "openBottemSheet: " + "=-=-=-=-=-=-");
        RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(requireActivity());
        BottemSheeetDetailsBinding bottemSheeetDetailsBinding = DataBindingUtil.inflate(getLayoutInflater()
                , R.layout.bottem_sheeet_details, null, false);
        mBottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mBottomSheetDialog.setContentView(bottemSheeetDetailsBinding.getRoot());
        bottemSheeetDetailsBinding.setDatamodel(data);
        bottemSheeetDetailsBinding.btnSubmit.setOnClickListener(v -> {

            sendRequestAPI(data.getUserId(), data.getId(), data.getPrice());
            mBottomSheetDialog.dismiss();
          /*  Bundle bundle = new Bundle();
            bundle.putString("from", "home");
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_navigation_home_to_payment_fragment, bundle);*/
        });

        mBottomSheetDialog.show();

    }

    private void openFilterBottem() {

        Log.e(TAG, "openBottemSheet: " + "=-=-=-=-=-=-");
        RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(requireActivity());
        BottemSheeetFilterBinding bottemSheeetFilterBinding = DataBindingUtil.inflate(getLayoutInflater()
                , R.layout.bottem_sheeet_filter, null, false);
        mBottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mBottomSheetDialog.setContentView(bottemSheeetFilterBinding.getRoot());
        if (!rating.equalsIgnoreCase("")) {
            //    bottemSheeetFilterBinding.sliderRating.set
            float f = Float.parseFloat(rating);
            bottemSheeetFilterBinding.sliderRating.setValues(f);
        }
        if (!range.equalsIgnoreCase("")) {
            //    bottemSheeetFilterBinding.sliderRating.set
            float f = Float.parseFloat(range);
            bottemSheeetFilterBinding.sliderRange.setValues(f);
        }
        if (!price.equalsIgnoreCase("")) {
            //    bottemSheeetFilterBinding.sliderRating.set
            float f = Float.parseFloat(price);
            bottemSheeetFilterBinding.sliderPrice.setValues(f);
        }
        bottemSheeetFilterBinding.btnSubmit.setOnClickListener(v -> {
            List<Float> val = bottemSheeetFilterBinding.sliderRating.getValues();
            float f11 = val.get(0);
            rating = String.valueOf(f11);
            List<Float> val2 = bottemSheeetFilterBinding.sliderRange.getValues();
            float f112 = val2.get(0);
            range = String.valueOf(f112);
            List<Float> val3 = bottemSheeetFilterBinding.sliderPrice.getValues();
            float f113 = val3.get(0);
            price = String.valueOf(f113);
            Log.e(TAG, "openFilterBottem: rating  ====  " + rating);
            Log.e(TAG, "openFilterBottem: range  ====  " + range);
            Log.e(TAG, "openFilterBottem: price  ====  " + price);
            mBottomSheetDialog.dismiss();
            getnearByUsersAPI();
        });
        bottemSheeetFilterBinding.imgReset.setOnClickListener(v -> {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle(R.string.reset_filters)
                            .setMessage(R.string.are_you_sure_to_reset)
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                price = "";
                                range = "";
                                rating = "";
                                dialog.dismiss();
                                mBottomSheetDialog.dismiss();
                                getnearByUsersAPI();

                            })

                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
        );


        mBottomSheetDialog.show();
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getnearByUsersAPI() {
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.searching));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        map.put("token", session.getAuthtoken());
        map.put("lat", lat);
        map.put("lon", lon);
        map.put("price", price);
        map.put("distance", range);
        map.put("rating", rating);
        Call<SuccessResNearbyList> call = apiInterface.get_provider_list_nearbuy(map);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SuccessResNearbyList> call,
                                   Response<SuccessResNearbyList> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResNearbyList data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equals("1")) {

                        resultArrayList = response.body().getResult();
                        if (resultArrayList.size() >= 1) {
                            for (int i = 0; i < resultArrayList.size(); i++) {
                                SuccessResNearbyList.Result dataobj = resultArrayList.get(i);
                                LatLng sydney = new LatLng(Double.parseDouble(dataobj.getLat()), Double.parseDouble(
                                        dataobj.getLon()));
                                Marker mArker = googleMap.addMarker(new MarkerOptions()
                                        .position(sydney)
                                        .title(dataobj.getToiletName())
                                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.marker_orenge)));
                                datasnap.put(mArker, dataobj);

                            }

                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResNearbyList> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }


    private void sendRequestAPI(String provider_id, String id, String amount) {
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.searching));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        map.put("provider_id", provider_id);
        map.put("toilet_id", id);
        map.put("amount", amount);
        map.put("token", session.getAuthtoken());

        Log.e(TAG, "sendRequestAPI: " + map);
        Call<SuccessResBooking> call = apiInterface.booking_request(map);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SuccessResBooking> call,
                                   Response<SuccessResBooking> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResBooking data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equals("1")) {
                        Toast.makeText(requireActivity(), data.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResBooking> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }
}