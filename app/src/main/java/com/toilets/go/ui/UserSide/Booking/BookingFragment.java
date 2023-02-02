package com.toilets.go.ui.UserSide.Booking;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toilets.go.R;
import com.toilets.go.databinding.FragmentBookingBinding;


public class BookingFragment extends Fragment {

    FragmentBookingBinding binding ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_booking,
                container, false);
   binding.header.tvtitle.setText("My Booking");
   binding.header.imgHeader.setOnClickListener(v -> {
getActivity().onBackPressed();
   });

    return binding.getRoot();
    }

}