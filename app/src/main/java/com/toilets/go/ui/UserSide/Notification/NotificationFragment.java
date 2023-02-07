package com.toilets.go.ui.UserSide.Notification;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.toilets.go.R;
import com.toilets.go.adapters.NotificationListAdapter;
import com.toilets.go.databinding.FragmentNotificationBinding;
import com.toilets.go.listeners.NotificationClickListener;
import com.toilets.go.models.SuccessResNotifications;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationFragment extends Fragment implements NotificationClickListener {
    FragmentNotificationBinding binding;
    Session session;
    String status = "Accept";
    List<SuccessResNotifications.Result> res = new ArrayList<>();
    NotificationListAdapter myRecyclerViewAdapter;
    private GosInterface apiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_notification, container, false);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(requireActivity());
        binding.header.tvtitle.setText(getString(R.string.notification));
        binding.header.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });


        getNotificationListAPI();
        return binding.getRoot();
    }

    private void getNotificationListAPI() {
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        map.put("token", session.getAuthtoken());
        Log.e(TAG, "sendRequestAPI: " + map);
        Call<SuccessResNotifications> call = apiInterface.get_notification(map);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SuccessResNotifications> call,
                                   Response<SuccessResNotifications> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResNotifications data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equalsIgnoreCase("1")) {
                        binding.nodata.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        res.clear();
                        res = response.body().getResult();
                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()
                                , LinearLayoutManager.VERTICAL, false));
                        myRecyclerViewAdapter = new NotificationListAdapter(res, requireActivity(),
                                NotificationFragment.this);
                        binding.recyclerView.setAdapter(myRecyclerViewAdapter);
                        binding.recyclerView.hasFixedSize();
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
            public void onFailure(Call<SuccessResNotifications> call, Throwable t) {
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
    public void notificationCardClicked(SuccessResNotifications.Result f, String Status, Integer position) {

    }
}