package com.toilets.go.ui.LoginSignup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.toilets.go.R;
import com.toilets.go.databinding.ActivitySearchPlaceBinding;
import com.toilets.go.ui.UserSide.HomeUserAct;

public class SearchPlaceActivity extends AppCompatActivity {
    ActivitySearchPlaceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(
                this, R.layout.activity_search_place);
        binding.layOut.setOnClickListener(v -> {
            onBackPressed();

        });
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, HomeUserAct.class)
                .putExtra("User_type", "User"));
        super.onBackPressed();
    }
}