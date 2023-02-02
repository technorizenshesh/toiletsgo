package com.toilets.go.ui.UserSide.Review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.toilets.go.R;
import com.toilets.go.databinding.FragmentContactUsBinding;


public class ContactUsFragment extends Fragment {
    FragmentContactUsBinding binding ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater
                ,R.layout.fragment_contact_us, container, false);
        binding.header.tvtitle.setText("Contact Us ");
        binding.header.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
        return binding.getRoot();
    }
}