package com.toilets.go.ui.UserSide.Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.toilets.go.databinding.FragmentProfileBinding;
import com.toilets.go.models.SuccessResProfile;
import com.toilets.go.R;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    Session session;
    GosInterface anInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_profile, container, false);
        binding.header.tvtitle.setText(" Profile ");
        session = new Session(getContext());
        anInterface = ApiClient.getClient().create(GosInterface.class);
        binding.header.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
        binding.ivEdit.setOnClickListener(v -> {

        });
        getUserProfileAPI();
        return binding.getRoot();
    }

    private void getUserProfileAPI() {
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        map.put("token", session.getAuthtoken());
        Call<SuccessResProfile> call = anInterface.get_profile(map);
        call.enqueue(new Callback<SuccessResProfile>() {
            @Override
            public void onResponse(Call<SuccessResProfile> call,
                                   Response<SuccessResProfile> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResProfile data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equals("1")) {
                    binding.setModel(response.body().getResult());
                    binding.setImageUrl(response.body().getResult().getImage());
                    }
                        
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResProfile> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }



}