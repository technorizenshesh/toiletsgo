package com.toilets.go.ui.UserSide.Booking;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.toilets.go.R;
import com.toilets.go.adapters.CustomerListAdapter;
import com.toilets.go.adapters.UserBookingAdapter;
import com.toilets.go.databinding.FragmentBookingBinding;
import com.toilets.go.models.SuccessResRequests;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.ui.ProviderHome.Home.ProviderHomeFragment;
import com.toilets.go.utills.CustomClickListener;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class BookingFragment extends Fragment  implements CustomClickListener {

    FragmentBookingBinding binding ;
    Session session;
    String status = "Accept";
    private GosInterface apiInterface;
    List<SuccessResRequests.Result> res = new ArrayList<>();
    UserBookingAdapter myRecyclerViewAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_booking,
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
        getRequestAPI(status);
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

    }
}