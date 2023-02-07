package com.toilets.go.ui.LoginSignup;

import static com.toilets.go.utills.DataManager.checkConnection;
import static com.toilets.go.utills.DataManager.showNoInternet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.toilets.go.R;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;
import com.toilets.go.databinding.ActivityBankDetailsBinding;
import com.toilets.go.models.SuccessResAddbank;
import com.toilets.go.models.SuccessResProfile;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.ui.ProviderHome.ProviderHomeActivity;
import com.toilets.go.ui.UserSide.HomeUserAct;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDetailsActivity extends AppCompatActivity {

    ActivityBankDetailsBinding binding;
    String UserType = "";
    Session session;
    private GosInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bank_details);
        session = new Session(this);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        UserType = session.getUSERTYPE();
        binding.btnSubmit.setOnClickListener(v -> {

            if (binding.edtBankName.getText().toString().equalsIgnoreCase("")) {
                binding.edtBankName.setError(getString(R.string.empty));
            } else if (binding.edtAcNo.getText().toString().equalsIgnoreCase("")) {
                binding.edtAcNo.setError(getString(R.string.empty));
            } else if (binding.edtHolder.getText().toString().equalsIgnoreCase("")) {
                binding.edtHolder.setError(getString(R.string.empty));
            } else if (binding.edtIfsc.getText().toString().equalsIgnoreCase("")) {
                binding.edtIfsc.setError(getString(R.string.empty));
            } else if (binding.edtBranch.getText().toString().equalsIgnoreCase("")) {
                binding.edtBranch.setError(getString(R.string.empty));
            } else {
                if (checkConnection(BankDetailsActivity.this)) {
                    addBankDetailsAPI(binding.edtBankName.getText().toString()
                            , binding.edtAcNo.getText().toString()
                            , binding.edtHolder.getText().toString()
                            , binding.edtIfsc.getText().toString(),
                            binding.edtBranch.getText().toString());
                } else {
                    showNoInternet(BankDetailsActivity.this, true);
                }
            }
        });
        if (checkConnection(BankDetailsActivity.this)) {
            getUserProfileAPI();
        } else {
            showNoInternet(BankDetailsActivity.this, true);
        }

    }

    private void addBankDetailsAPI(String edtBankName,
                                   String edtAcNo,
                                   String edtHolder,
                                   String edtIfsc,
                                   String edtBranch) {
        DataManager.getInstance().showProgressMessage(BankDetailsActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("bank_name", edtBankName);
        map.put("holder_name", edtHolder);
        map.put("ifsc_code", edtIfsc);
        map.put("branch", edtBranch);
        map.put("account_number", edtAcNo);
        map.put("user_id", session.getUserId());
        map.put("token", session.getAuthtoken());
        Call<SuccessResAddbank> call = apiInterface.add_bank_account(map);
        call.enqueue(new Callback<SuccessResAddbank>() {
            @Override
            public void onResponse(Call<SuccessResAddbank> call,
                                   Response<SuccessResAddbank> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResAddbank data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equals("1")) {
                        Log.e("TAG", "onResponse: " + UserType);
                        if (UserType.equalsIgnoreCase("in")) {
                            onBackPressed();
                        } else if (UserType.equalsIgnoreCase("User")) {
                            startActivity(new Intent(getApplicationContext(), HomeUserAct.class)
                                    .putExtra("User_type", UserType).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            startActivity(new Intent(getApplicationContext(), ProviderHomeActivity.class)
                                    .putExtra("User_type", UserType).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResAddbank> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }

    private void getUserProfileAPI() {
        DataManager.getInstance().showProgressMessage(BankDetailsActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        map.put("token", session.getAuthtoken());
        Call<SuccessResProfile> call = apiInterface.get_profile(map);
        call.enqueue(new Callback<SuccessResProfile>() {
            @Override
            public void onResponse(Call<SuccessResProfile> call,
                                   Response<SuccessResProfile> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResProfile data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equals("1")) {
                        SuccessResProfile.Result data3 = response.body().getResult();
                        binding.edtBankName.setText(data3.getBankName());
                        binding.edtAcNo.setText(data3.getAccountNumber());
                        binding.edtHolder.setText(data3.getHolderName());
                        binding.edtIfsc.setText(data3.getIfscCode());
                        binding.edtBranch.setText(data3.getBranch());
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