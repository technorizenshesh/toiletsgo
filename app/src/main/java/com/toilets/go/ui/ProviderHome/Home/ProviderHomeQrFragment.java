package com.toilets.go.ui.ProviderHome.Home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.toilets.go.R;
import com.toilets.go.databinding.FragmentProviderHomeQrBinding;
import com.toilets.go.models.SuccessResProfile;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.ui.ProviderHome.ProviderHomeActivity;
import com.toilets.go.ui.UserSide.HomeUserAct;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProviderHomeQrFragment extends Fragment {
FragmentProviderHomeQrBinding binding ;
    Session session;
    private GosInterface apiInterface;

    private static final int REQUEST_CODE_QR_SCAN = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_provider_home_qr, container, false);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(requireActivity());

         binding.processBooking.setOnClickListener(v -> {
             Intent i = new Intent(getActivity(), QrCodeActivity.class);
             startActivityForResult( i,REQUEST_CODE_QR_SCAN);
         });
         binding.processBooking2.setOnClickListener(v -> {
            /* Bundle bundle = new Bundle();
             bundle.putString("from", "home");
             Navigation.findNavController(binding.getRoot())
                     .navigate(R.id.action_provider_qr_to_provider_home, bundle);*/
             try {
                 ProviderHomeActivity activity = (ProviderHomeActivity) getActivity();
                 assert activity != null;
                 activity.hideBadge();
             }catch (Exception e){
                 e.printStackTrace();}
         });

          binding.SitchBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
              if (isChecked){
                  changeAutoAccept("True");
              }else {
                  changeAutoAccept("False");

              }
          });
        getUserProfileAPI();
        return binding.getRoot() ;
    }

    private void changeAutoAccept(String yes) {
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        map.put("token", session.getAuthtoken());
        map.put("auto_accept", yes);
        Call<SuccessResProfile> call = apiInterface.get_profile(map);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SuccessResProfile> call,
                                   Response<SuccessResProfile> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResProfile data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equals("1")) {
                        getUserProfileAPI();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK)
        {
            Log.d("LOGTAG","COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d("LOGTAG","Have scan result in your app activity :"+ result);
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }
    }
    private void getUserProfileAPI() {
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        map.put("token", session.getAuthtoken());
        Call<SuccessResProfile> call = apiInterface.get_profile(map);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SuccessResProfile> call,
                                   Response<SuccessResProfile> response) {
                DataManager.getInstance().hideProgressMessage();

                try {
                    SuccessResProfile data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equals("1")) {
                        String isacpt = data.getResult().getAutoAccpet();
                         if (isacpt.equalsIgnoreCase("True")){
                             binding.SitchBtn.setChecked(true);
                         }else binding.SitchBtn.setChecked(false);
                        // binding.setModel(response.body().getResult());
                        // binding.setImageUrl(response.body().getResult().getImage());
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