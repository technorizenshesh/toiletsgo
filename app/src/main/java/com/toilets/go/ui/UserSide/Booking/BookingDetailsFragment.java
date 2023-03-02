package com.toilets.go.ui.UserSide.Booking;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.toilets.go.R;
import com.toilets.go.databinding.FragmentBookingDetailsBinding;
import com.toilets.go.databinding.FragmentProviderHomeQrBinding;
import com.toilets.go.models.SuccessResRequests;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.ui.LoginSignup.BasicDetailsActivity;
import com.toilets.go.utills.Session;

import java.util.Objects;

import static com.toilets.go.utills.Util.generateQrCodeFromStringData;


public class BookingDetailsFragment extends Fragment {
    FragmentBookingDetailsBinding binding;
    Session session;
    private GosInterface apiInterface;
    SuccessResRequests.Result result;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_booking_details, container, false);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(requireActivity());
        try {

            if (getArguments() != null) {
                result = (SuccessResRequests.Result) getArguments().getSerializable("Result");
                Log.i("getuserValidate",result.toString());
                binding.header.tvtitle.setText("Booking Details");
                 if(session.getUSERTYPE().equalsIgnoreCase("USER")){
                     binding.processBooking.setVisibility(View.VISIBLE);
                 }
                binding.tvName.setText(result.getUserName());
                binding.tvAddress.setText(result.getAddress());
                binding.tvRating.setText(" $ "+result.getAmount()+" ");
                binding.bookingId.setText("  #"+result.getId()+" ");
                Bitmap qr = generateQrCodeFromStringData(result.getId());
                 binding.qrCodeImage.setImageBitmap(qr);
                Log.e("TAG", "onCreateView: "+result.getId() );
                Glide.with(BookingDetailsFragment.this)
                        .load(result.getCartDetails().getImage1())
                        .centerCrop()
                        .into(binding.userImage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        binding.header.imgHeader.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });


        return binding.getRoot();
    }
}