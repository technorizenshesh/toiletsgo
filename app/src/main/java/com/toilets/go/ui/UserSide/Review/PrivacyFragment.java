package com.toilets.go.ui.UserSide.Review;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.toilets.go.R;
import com.toilets.go.databinding.FragmentPrivacyBinding;
import com.toilets.go.models.SuccessResNotifications;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PrivacyFragment extends Fragment {
    FragmentPrivacyBinding binding ;
    Session session;
    private GosInterface apiInterface;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater
                ,R.layout.fragment_privacy, container, false);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(requireActivity());

        binding.header.tvtitle.setText(getString(R.string.trems_condition));
        binding.header.imgHeader.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
         getPrivacy();
        return binding.getRoot();
    }

    private void getPrivacy()
        {
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
                        assert data != null;
                        Log.e("data", data.getStatus());
                        if (data.getStatus().equalsIgnoreCase("1")) {

                        } else {
                            Toast.makeText(requireActivity(), data.getMessage(), Toast.LENGTH_SHORT).show();

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
                    DataManager.getInstance().hideProgressMessage();
                }
            });

        }

}