package com.toilets.go.ui.UserSide.Booking;

import static android.content.ContentValues.TAG;
import static com.toilets.go.utills.DataManager.checkConnection;
import static com.toilets.go.utills.DataManager.showNoInternet;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.toilets.go.R;
import com.toilets.go.adapters.UserBookingAdapter;
import com.toilets.go.databinding.DialogRatingBinding;
import com.toilets.go.databinding.FragmentBookingBinding;
import com.toilets.go.models.SuccessResRequests;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.utills.CustomClickListener;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BookingFragment extends Fragment implements CustomClickListener {

    FragmentBookingBinding binding;
    Session session;
    String status = "Accept";
    List<SuccessResRequests.Result> res = new ArrayList<>();
    UserBookingAdapter myRecyclerViewAdapter;
    private GosInterface apiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking,
                container, false);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(requireActivity());
        binding.header.tvtitle.setText("My Booking");
        binding.header.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        if (checkConnection((requireActivity()))) {
            getRequestAPI(status);
        } else {
            showNoInternet(requireActivity(), true);
        }
        super.onResume();
    }

    private void getRequestAPI(String status) {
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        map.put("status", status);
        map.put("token", session.getAuthtoken());
        Log.e(TAG, "sendRequestAPI: " + map);
        Call<SuccessResRequests> call = apiInterface.get_user_order(map);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SuccessResRequests> call,
                                   Response<SuccessResRequests> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResRequests data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equalsIgnoreCase("1")) {
                        binding.nodata.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        //Toast.makeText(requireActivity(), data.getMessage(), Toast.LENGTH_SHORT).show();
                        res.clear();
                        assert response.body() != null;
                        res = response.body().getResult();
                        myRecyclerViewAdapter = new UserBookingAdapter(res, requireActivity(),
                                BookingFragment.this);
                        binding.setMyAdapter(myRecyclerViewAdapter);
                    } else {
                        Toast.makeText(requireActivity(), data.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.nodata.setVisibility(View.VISIBLE);
                        binding.recyclerView.setVisibility(View.GONE);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResRequests> call, Throwable t) {
                call.cancel();
                Log.e(TAG, "onFailure: " + t.getCause());
                Log.e(TAG, "onFailure: " + t.getMessage());
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                Toast.makeText(requireActivity(), t.getCause().toString(),
                        Toast.LENGTH_SHORT).show();
                binding.nodata.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }

    @Override
    public void cardClicked(SuccessResRequests.Result f, String Status, Integer position) {

            Log.e(TAG, "cardClicked: " );
            showDialog(f.getId());

    }

    private void showDialog(String id) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations
                = android.R.style.Widget_Material_ListPopupWindow;
        DialogRatingBinding binding = DialogRatingBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
          Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        binding.btnSubmit.setOnClickListener(v -> {
            String rating = String.valueOf(binding.ratingBar.getRating());
            String review = binding.etComment.getText().toString();
            if (review.equalsIgnoreCase("")) {
                binding.etComment.setError(getString(R.string.empty));
                DataManager.showToast(requireActivity(), getString(R.string.empty));
            } else {
                submitReview(rating,review,id);
                dialog.dismiss();

            }
        });
        binding.header.imgHeader.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void submitReview(String rating, String review, String id) {

            DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
            Map<String, String> map = new HashMap<>();
            map.put("rating", rating);
            map.put("comment", review);
            map.put("toilet_id", id);
            map.put("token", session.getAuthtoken());
            Log.e(TAG, "sendRequestAPI: " + map);
            Call<ResponseBody> call = apiInterface.add_rating(map);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       Response<ResponseBody> response) {
                    try {
                        DataManager.getInstance().hideProgressMessage();
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        String data = jsonObject.getString("status");

                        String message = jsonObject.getString("message");
                        Toast.makeText(requireActivity(),message, Toast.LENGTH_SHORT).show();

                        //    }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                    Log.e(TAG, "onFailure: " + t.getCause());
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    Toast.makeText(requireActivity(), t.getCause().toString(),
                            Toast.LENGTH_SHORT).show();
                    DataManager.getInstance().hideProgressMessage();
                }
            });


    }
}