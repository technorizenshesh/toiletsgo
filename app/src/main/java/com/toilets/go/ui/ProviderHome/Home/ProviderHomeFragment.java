package com.toilets.go.ui.ProviderHome.Home;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

import com.toilets.go.adapters.CustomerListAdapter;
import com.toilets.go.R;
import com.toilets.go.databinding.FragmentProviderHomeBinding;
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


public class ProviderHomeFragment extends Fragment implements CustomClickListener {
    FragmentProviderHomeBinding binding;
    Session session;
    String status = "Accept";
    private GosInterface apiInterface;
    List<SuccessResRequests.Result> res = new ArrayList<>();

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_provider_home, container, false);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(requireActivity());
        binding.btnOne.setOnClickListener(v -> {
            status = "Accept";
            getRequestAPI(status);
            binding.btnOne.setBackground(getActivity().getResources().getDrawable(R.drawable.border_btn_selected));
            binding.btnTwo.setBackground(getActivity().getResources().getDrawable(R.drawable.border_btn));
        });
        binding.btnTwo.setOnClickListener(v -> {
            status = "Pending";
            getRequestAPI(status);
            binding.btnTwo.setBackground(getActivity().getResources().getDrawable(R.drawable.border_btn_selected));
            binding.btnOne.setBackground(getActivity().getResources().getDrawable(R.drawable.border_btn));
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
        getRequestAPI(status);
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

                        CustomerListAdapter myRecyclerViewAdapter = new CustomerListAdapter(
                                res,
                                requireActivity(), ProviderHomeFragment.this::cardClicked);
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
    public void cardClicked(SuccessResRequests.Result f, String Status) {
        if (Status.equalsIgnoreCase("Accept")) {

        }
    }
}