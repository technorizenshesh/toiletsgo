package com.toilets.go.ui.UserSide.Notification;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toilets.go.R;
import com.toilets.go.databinding.FragmentNotificationBinding;


public class NotificationFragment extends Fragment {
    FragmentNotificationBinding binding ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater
                ,R.layout.fragment_notification, container, false);
        binding.header.tvtitle.setText("Notification");
        binding.header.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
        return binding.getRoot();
    }
}