package com.toilets.go.ui.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.toilets.go.R;
import com.toilets.go.databinding.ActivityForgetPassBinding;
import com.toilets.go.databinding.ActivityLoginBinding;
import com.toilets.go.models.SuccessResForgetPass;
import com.toilets.go.models.SuccessResSignup;
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

public class ForgetPassActivity extends AppCompatActivity {
    ActivityForgetPassBinding binding;
    String UserType = "";
    Session session;
    private GosInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget_pass);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(this);
        UserType = session.getUSERTYPE();
         binding.btnSubmit.setOnClickListener(v -> {
             String email =  binding.edtEmail.getText().toString();
              if (email.equalsIgnoreCase("")){
                  binding.edtEmail.setError(getString(R.string.empty));
              }else {
                  resetPassword(email);
              }

         });
    }
    private void resetPassword(String email) {
        DataManager.getInstance().showProgressMessage(ForgetPassActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        Call<SuccessResForgetPass> call = apiInterface.forgot_password(map);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SuccessResForgetPass> call,
                                   Response<SuccessResForgetPass> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResForgetPass data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equals("1")) {
                        Toast.makeText(ForgetPassActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        onBackPressed();
                    } else {
                        Toast.makeText(ForgetPassActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResForgetPass> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }


}