package com.toilets.go.ui.UserSide.Review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.toilets.go.R;
import com.toilets.go.databinding.FragmentPrivacyBinding;


public class PrivacyFragment extends Fragment {
    FragmentPrivacyBinding binding ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater
                ,R.layout.fragment_privacy, container, false);
        binding.header.tvtitle.setText(getString(R.string.trems_condition));
        binding.header.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
        return binding.getRoot();
    }
}