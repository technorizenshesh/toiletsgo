package com.toilets.go;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;


import com.google.gson.Gson;
import com.toilets.go.models.SuccessResSignup;
import com.toilets.go.ui.LoginSignup.BasicDetailsActivity;
import com.toilets.go.ui.LoginSignup.LoginTypeActivity;
import com.toilets.go.ui.ProviderHome.ProviderHomeActivity;
import com.toilets.go.ui.UserSide.HomeUserAct;
import com.toilets.go.utills.Session;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Window y = getWindow();
        y.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        session =new  Session(this);
        session.setRestraID("");
        if (session.isLoggedIn()) {
        }
        askNotificationPermission();
        handlerMethod();
    }

    private void handlerMethod() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (session.isLoggedIn()) {
                //   startService();
                Log.e(TAG, "handlerMethod: UserIdUserIdUserIdUserIdUserIdUserIdUserIdUserId "+session.getUserId() );
              if (session.getUSERTYPE().equalsIgnoreCase("USER")){


                  startActivity(new Intent(MainActivity.this,
                          HomeUserAct.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                          |Intent.FLAG_ACTIVITY_CLEAR_TOP));
                  finish();
              }else {
                 // Gson gson = new Gson();
               //   String json = session.getUSERDATA();
                //  SuccessResSignup.Result obj = gson.fromJson(json,  SuccessResSignup.Result.class);
                  if (session.getUSERBASIC().equalsIgnoreCase("1")){
                      startActivity(new Intent(MainActivity.this,
                              ProviderHomeActivity.class)
                              .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                      |Intent.FLAG_ACTIVITY_CLEAR_TOP));
                      finish();

                  }else {
                      startActivity(new Intent(MainActivity.this,
                              BasicDetailsActivity.class).putExtra("User_type", "PROVIDER")
                              .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                      |Intent.FLAG_ACTIVITY_CLEAR_TOP));
                      finish();
                 }
              }


            } else {
                Intent intent = new Intent(MainActivity.this,
                        LoginTypeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
  private final ActivityResultLauncher<String> requestPermissionLauncher =
          registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
              if (isGranted) {
              } else {
                  // TODO: Inform user that that your app will not show notifications.
              }
          });

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                   } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}