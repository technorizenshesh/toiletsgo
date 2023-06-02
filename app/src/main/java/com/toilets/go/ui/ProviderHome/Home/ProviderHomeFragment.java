package com.toilets.go.ui.ProviderHome.Home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.toilets.go.adapters.CustomerListAdapter;
import com.toilets.go.R;
import com.toilets.go.databinding.FragmentProviderHomeBinding;
import com.toilets.go.models.SuccessResAcptRej;
import com.toilets.go.models.SuccessResRequests;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.utills.CustomClickListener;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.toilets.go.utills.DataManager.checkConnection;
import static com.toilets.go.utills.DataManager.showNoInternet;


public class ProviderHomeFragment extends Fragment implements CustomClickListener {
    FragmentProviderHomeBinding binding;
    Session session;
    String status = "Accept";
    private GosInterface apiInterface;
    List<SuccessResRequests.Result> res = new ArrayList<>();
    CustomerListAdapter myRecyclerViewAdapter;

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(mMessageReceiver);
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            //do other stuff here
        }
    };

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_provider_home, container, false);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(requireActivity());
        Log.e(TAG, "getFireBaseTokengetFireBaseToken: " + session.getFireBaseToken());
        binding.btnOne.setOnClickListener(v -> {

            if (checkConnection((requireActivity()))) {
                status = "Accept";

                getRequestAPI(status);
                binding.btnOne.setBackground(getActivity().getResources().getDrawable(R.drawable.border_btn_selected));
                binding.btnTwo.setBackground(getActivity().getResources().getDrawable(R.drawable.border_btn));


            } else {
                showNoInternet(requireActivity(), true);
            }
        });
        binding.btnTwo.setOnClickListener(v -> {

            if (checkConnection((requireActivity()))) {
                status = "Accept";

                status = "Pending";
                getRequestAPI(status);
                binding.btnTwo.setBackground(getActivity().getResources().getDrawable(R.drawable.border_btn_selected));
                binding.btnOne.setBackground(getActivity().getResources().getDrawable(R.drawable.border_btn));

            } else {
                showNoInternet(requireActivity(), true);
            }
        });
        return binding.getRoot();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Widget_Material_ListPopupWindow;
        dialog.setContentView(R.layout.dialog_new_request);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Button btn_reject = dialog.findViewById(R.id.btn_reject);
        Button btn_accept = dialog.findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(v -> {
            dialog.dismiss();
        });
        btn_reject.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public void onResume() {
        if (checkConnection((requireActivity()))) {
            getRequestAPI(status);


        } else {
            showNoInternet(requireActivity(), true);
        }
        requireActivity().registerReceiver(mMessageReceiver, new IntentFilter("Booking"));

        super.onResume();
    }

    private void getRequestAPI(String status) {
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        map.put("status", status);
        map.put("token", session.getAuthtoken());
        Log.e(TAG, "sendRequestAPI: " + map);
        Call<SuccessResRequests> call = apiInterface.get_order_history(map);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SuccessResRequests> call,
                                   Response<SuccessResRequests> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResRequests data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equalsIgnoreCase("1")) {
                        binding.nodata.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        //Toast.makeText(requireActivity(), data.getMessage(), Toast.LENGTH_SHORT).show();
                        res.clear();
                        assert response.body() != null;
                        res = response.body().getResult();
                        myRecyclerViewAdapter = new CustomerListAdapter(res, requireActivity(),
                                ProviderHomeFragment.this);
                        binding.setMyAdapter(myRecyclerViewAdapter);
                    } else {
                        Toast.makeText(requireActivity(), data.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.nodata.setVisibility(View.VISIBLE);
                        binding.recyclerView.setVisibility(View.GONE);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResRequests> call, Throwable t) {
                call.cancel();
                Log.e(TAG, "onFailure: " + t.getCause());
                Log.e(TAG, "onFailure: " + t.getMessage());
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                Toast.makeText(requireActivity(), t.getCause().toString(),
                        Toast.LENGTH_SHORT).show();
                binding.nodata.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }


    @Override
    public void cardClicked(SuccessResRequests.Result f, String Status, Integer position) {
        if (Status.equalsIgnoreCase("Clicked")) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("Result",f);
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_booking_to_booking_details, bundle);
        } else if (Status.equalsIgnoreCase("Accept")) {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.accept_booking).setMessage(R.string.are_you_sure_to_accept)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        acceptRejectApi(f, Status, position);
                        dialog.dismiss();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        } else {
            new AlertDialog.Builder(requireActivity()).setTitle(R.string.reject_booking)
                    .setMessage(R.string.are_you_sure_to_reject)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        acceptRejectApi(f, Status, position);
                        dialog.dismiss();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void acceptRejectApi(SuccessResRequests.Result f, String status, Integer position) {
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("request_id", f.getId());
        map.put("user_id", session.getUserId());
        map.put("status", status);
        map.put("token", session.getAuthtoken());
        Log.e(TAG, "get_accept_cancel_order: " + map);
        Call<SuccessResAcptRej> call = apiInterface.get_accept_cancel_order(map);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SuccessResAcptRej> call,
                                   Response<SuccessResAcptRej> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResAcptRej data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equalsIgnoreCase("1")) {
                        myRecyclerViewAdapter.removeAt(position);
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResAcptRej> call, Throwable t) {
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