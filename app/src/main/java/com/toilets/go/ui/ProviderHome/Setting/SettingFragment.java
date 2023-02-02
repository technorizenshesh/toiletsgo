package com.toilets.go.ui.ProviderHome.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.toilets.go.R;
import com.toilets.go.databinding.FragmentSettingBinding;
import com.toilets.go.ui.LoginSignup.BankDetailsActivity;
import com.toilets.go.utills.Session;

public class SettingFragment extends Fragment {
    FragmentSettingBinding binding;
Session session ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_setting, container, false);
         session = new Session(getActivity());
        binding.tvBankDetails.setOnClickListener(v -> {
            getActivity().startActivity(new Intent(getActivity(), BankDetailsActivity.class)
                    .putExtra("User_type", "in"));
        });

        binding.tvPayment.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("from", "home");
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_setting_to_payment_history, bundle);
        });
        binding.tvContactUs.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("from", "home");
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_setting_t_contact_us_fragment, bundle);
        }); binding.tvTerms.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("from", "home");
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_setting_t_privacy_fragment, bundle);
        });

         binding.tvLogout.setOnClickListener(v -> {
             session.logout();
         });
        return binding.getRoot();
    }
}