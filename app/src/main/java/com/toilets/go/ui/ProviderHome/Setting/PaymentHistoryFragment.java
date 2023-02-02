package com.toilets.go.ui.ProviderHome.Setting;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toilets.go.R;
import com.toilets.go.databinding.FragmentPaymentHistoryBinding;


public class PaymentHistoryFragment extends Fragment {

    FragmentPaymentHistoryBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_payment_history, container,
                false);
        binding.header.tvtitle.setText("Payment's");
        binding.header.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
        binding.btnPayment.setOnClickListener(v -> {
          /*  Bundle bundle = new Bundle();
            bundle.putString("from", "home");
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.
                            action_navigation_payment_fragment_to_success_fragment, bundle);*/
        });
        return binding.getRoot();
    }
}