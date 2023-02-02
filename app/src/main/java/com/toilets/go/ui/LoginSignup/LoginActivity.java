package com.toilets.go.ui.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.toilets.go.databinding.ActivityLoginBinding;
import com.toilets.go.models.SuccessResSignup;
import com.toilets.go.R;
import com.toilets.go.ui.ProviderHome.ProviderHomeActivity;
import com.toilets.go.ui.UserSide.HomeUserAct;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.utills.Util;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String UserType = "";
    private GosInterface apiInterface;
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(this);

        if (getIntent() != null) {
            UserType = getIntent().getExtras().getString("User_type");
        }
        binding.btnSubmit.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString().trim();
            String pass = binding.edtPass.getText().toString().trim();
            if (email.equalsIgnoreCase("")) {
                binding.edtEmail.setError(getString(R.string.empty));
            } else
                if (pass.equalsIgnoreCase("")) {
                binding.edtPass.setError(getString(R.string.empty));
            } else {
                loginAPI(email, pass);
            }
        });
    }





    private void loginAPI(String email, String pass) {
        DataManager.getInstance().showProgressMessage(LoginActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", pass);
        map.put("register_id", session.getFireBaseToken());
        map.put("type",UserType  );
        Call<SuccessResSignup> call = apiInterface.login(map);
        call.enqueue(new Callback<SuccessResSignup>() {
            @Override
            public void onResponse(Call<SuccessResSignup> call,
                                   Response<SuccessResSignup> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResSignup data = response.body();
                    Log.e("data", data.getStatus());
                    if (data.getStatus().equals("1")) {
                        DataManager.getInstance().hideProgressMessage();
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        SuccessResSignup.Result signup = response.body().getResult();
                        Gson gson = new Gson();
                        String json = gson.toJson(signup);
                        session.setUSERDATA(json);
                        session.setUserId(signup.getId());
                        session.setLogin(true);
                        session.setUSERTYPE(UserType);
                        session.setAuthtoken(response.body().getResult().getToken());
                        if (UserType.equalsIgnoreCase("User")) {
                            startActivity(new Intent(getApplicationContext(), HomeUserAct.class)
                                    .putExtra("User_type", UserType));
                        } else {
                             if (signup.getStep().equalsIgnoreCase("0")){
                                 startActivity(new Intent(getApplicationContext(),
                                         BasicDetailsActivity.class)
                                         .putExtra("User_type", UserType));
                             }else {
                            startActivity(new Intent(getApplicationContext(),
                                    ProviderHomeActivity.class)
                                    .putExtra("User_type", UserType));}
                        }
                    }else {
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResSignup> call, Throwable t) {
                call.cancel();
                DataManager.getInstance().hideProgressMessage();
            }
        });

    }

}