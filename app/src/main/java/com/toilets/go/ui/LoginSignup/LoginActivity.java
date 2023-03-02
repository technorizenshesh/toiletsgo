package com.toilets.go.ui.LoginSignup;

import static com.toilets.go.utills.DataManager.checkConnection;
import static com.toilets.go.utills.DataManager.showNoInternet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.toilets.go.R;
import com.toilets.go.databinding.ActivityLoginBinding;
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


public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String UserType = "";
    Session session;
    private GosInterface apiInterface;

    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(getString(R.string.i_aggree)
        );
        spanTxt.append(" " + getString(R.string.tos));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(getApplicationContext(), "Terms of services Clicked",
                        Toast.LENGTH_SHORT).show();
            }
        }, spanTxt.length() - getString(R.string.tos).length(), spanTxt.length(), 0);
        spanTxt.append(" " + getString(R.string.and) + " ");
        spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), 32, spanTxt.length(), 0);
        spanTxt.append(getString(R.string.privacy));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(getApplicationContext(), "Privacy Policy Clicked",
                        Toast.LENGTH_SHORT).show();
            }
        }, spanTxt.length() - getString(R.string.privacy).length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(this);
        UserType = session.getUSERTYPE();
        //binding.prvc.setText(Html.fromHtml(getString(R.string.datal)));
        customTextView(binding.prvc);
        binding.btnSubmit.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString().trim();
            String pass = binding.edtPass.getText().toString().trim();
            if (email.equalsIgnoreCase("")) {
                binding.edtEmail.setError(getString(R.string.empty));
            } else if (pass.equalsIgnoreCase("")) {
                binding.edtPass.setError(getString(R.string.empty));
            } else {
                if (checkConnection(LoginActivity.this)) {
                    loginAPI(email, pass);
                } else {
                    showNoInternet(LoginActivity.this, true);
                }
            }
        });
        binding.botm.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignupActivity.class)
                    .putExtra("User_type", UserType));
        });
    }


    private void loginAPI(String email, String pass) {
        DataManager.getInstance().showProgressMessage(LoginActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", pass);
        map.put("register_id", session.getFireBaseToken());
        map.put("type", UserType);
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
                                    .putExtra("User_type", UserType).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            if (signup.getStep().equalsIgnoreCase("0")) {
                                startActivity(new Intent(getApplicationContext(),
                                        BasicDetailsActivity.class)
                                        .putExtra("User_type", UserType).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

                            } else {
                                session.setUSERBASIC("1");
                                startActivity(new Intent(getApplicationContext(),
                                        ProviderHomeActivity.class)
                                        .putExtra("User_type", UserType).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        }
                    } else {
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