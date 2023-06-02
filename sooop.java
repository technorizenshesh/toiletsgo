package com.my.vibras;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.my.vibras.Company.HomeComapnyAct;
import com.my.vibras.act.ForgetPasswordAct;
import com.my.vibras.act.HomeUserAct;
import com.my.vibras.databinding.ActivityLoginBinding;
import com.my.vibras.model.SocilaLoginResponse;
import com.my.vibras.model.SuccessResSignup;
import com.my.vibras.retrofit.ApiClient;
import com.my.vibras.retrofit.Constant;
import com.my.vibras.retrofit.NetworkAvailablity;
import com.my.vibras.retrofit.VibrasInterface;
import com.my.vibras.utility.BetterActivityResult;
import com.my.vibras.utility.DataManager;
import com.my.vibras.utility.SharedPreferenceUtility;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;
import static com.my.vibras.retrofit.Constant.showToast;

public class LoginAct extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100 ;
    ActivityLoginBinding binding;
    String LoginType;
    private static final int RC_SIGN_IN = 007;
    private static final String TAG = "LoginAct";
    private FirebaseAuth mAuth;
    private String strEmail = "", strPassword = "", deviceToken = "";
    private VibrasInterface apiInterface;
    private GoogleSignInClient mGoogleSignInClient;
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher
            = BetterActivityResult.registerActivityForResult(this);

    ProgressDialog dialog;
    LoginButton login_button;
    private CallbackManager callbackManager;
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        apiInterface = ApiClient.getClient().create(VibrasInterface.class);
        getLocation();
        getToken();
        if (SDK_INT >= Build.VERSION_CODES.R) {
            requestPermissions();
        }else {
            requestPermissions2();

        }
        checkPermission();
        if (getIntent() != null) {
            LoginType = getIntent().getStringExtra("loginType").toString();
        }
        binding.llLogin.setOnClickListener(v -> {
            startActivity(new Intent(LoginAct.this, SignUpAct.class)
                    .putExtra("loginType", LoginType));
        });

        binding.txtForogot.setOnClickListener(v -> {
            startActivity(new Intent(LoginAct.this, ForgetPasswordAct.class).putExtra("loginType", LoginType));

        });
        binding.RLogin.setOnClickListener(v -> {
            strEmail = binding.edtEmail.getText().toString().trim();
            strPassword = binding.edtPassword.getText().toString().trim();
            if (isValid()) {
                if (NetworkAvailablity.checkNetworkStatus(this)) {
                    login();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.msg_noInternet), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.on_error), Toast.LENGTH_SHORT).show();
            }
        });
        binding.googleLogin.setOnClickListener(v -> {
            signOut();
            googleSignIn();
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        loginManager = LoginManager.getInstance();
        loginManager.logOut();
        callbackManager = CallbackManager.Factory.create();
        binding.loginButton.setReadPermissions("email", "public_profile");
        login_button = (LoginButton) findViewById(R.id.login_button);
        //  LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserDetails(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
        binding.fbLoginLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // hashFromSHA1("BE:05:3C:F6:E7:9E:8A:61:BA:7F:5B:E6:68:1E:89:78:78:2D:7B:5E");
                hashFromSHA1("BE:05:3C:F6:E7:9E:8A:61:BA:7F:5B:E6:68:1E:89:78:78:2D:7B:5E");
                FacebookSdk.sdkInitialize(getApplicationContext());
                AppEventsLogger.activateApp(getApplication());
                login_button.performClick();
            }
        });

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.my.vibras",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        // hashFromSHA1("35:FB:D8:73:DB:D9:AA:E4:EB:FC:4A:8A:18:D7:CD:02:7E:EF:3B:25");
       /* hashFromSHA1("BE:05:3C:F6:E7:9E:8A:61:BA:7F:5B:E6:68:1E:89:78:78:2D:7B:5E");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());*/

    }

    private void requestPermissions2()  {
            // below line is use to request permission in the current activity.
            // this method is use to handle error in runtime permissions

    /*    if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(LoginAct.this, new String[]{WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }*/














            Dexter.withContext(this)
                    // below line is use to request the number of permissions which are required in our app.
                    .withPermissions(Manifest.permission.CAMERA,
                            // below is the list of permissions
                            Manifest.permission.ACCESS_FINE_LOCATION
                            , READ_EXTERNAL_STORAGE)

                    // after adding permissions we are calling an with listener method.
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            // this method is called when all permissions are granted
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                // do you work now
                                Toast.makeText(LoginAct.this,
                                        "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                            }
                            // check for permanent denial of any permission
                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permanently, we will show user a dialog message.
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            // this method is called when user grants some permission and denies some of them.
                            permissionToken.continuePermissionRequest();
                        }
                    }).withErrorListener(error -> {
                        // we are displaying a toast message for error message.
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    })
                    // below line is use to run the permissions on same thread and to check the permissions
                    .onSameThread().check();
        }



    private void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject response,
                            GraphResponse response1) {
                        try {
                            final String socialId = (response.getString("id"));
                            String email = null;
                            final String username = (response.get("name").toString());
                            JSONObject profile_pic_data =
                                    new JSONObject(response.get("picture").toString());
                            JSONObject profile_pic_url =
                                    new JSONObject(profile_pic_data.getString("data"));
                            final String imageUrl = profile_pic_url.getString("url");
                            Log.e("imageUrl---->", "" + imageUrl);
                            // SocialLogin(socialId, "",username,"",imageUrl);
                            try {
                                email = (response.get("email").toString());

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                            Log.e("socialId---->", "" + socialId);
                            Log.e("username---->", "" + username);
                            Log.e("email---->", "" + email);
                            //  socialLoginApi(personName,personName, email, socialId, "Google");

                            socialLoginApi(username, "", email, socialId, "FB");

                        } catch (Exception e) {

                            e.printStackTrace();

                        }
                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email, picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();


    }

    public void hashFromSHA1(String sha1) {
        String[] arr = sha1.split(":");
        byte[] byteArr = new byte[arr.length];

        for (int i = 0; i < arr.length; i++) {
            byteArr[i] = Integer.decode("0x" + arr[i]).byteValue();
        }

        Log.e("hash : ", Base64.encodeToString(byteArr, Base64.NO_WRAP));
    }


    private void getToken() {
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "" + getString(R.string.fetching_fcm_token_failed), task.getException());
                                return;
                            }
                            // Get new FCM registration token
                            String token = task.getResult();
                            deviceToken = token;
                            SharedPreferenceUtility.getInstance(getApplication()).putString(Constant.REGISTER_ID, token);
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(LoginAct.this, "Error=>" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(LoginAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(LoginAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginAct.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constant.LOCATION_REQUEST);
        } else {
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

                case PERMISSION_REQUEST_CODE:
                    if (grantResults.length > 0) {
                        boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                        if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                            // perform action when allow permission success
                        } else {
                            Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;

            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(LoginAct.this, LoginAct.this.getResources().getString(R.string.permisson_denied), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    private void login() {
        TimeZone tz = TimeZone.getDefault();
        String id = tz.getID();
        DataManager.getInstance().showProgressMessage(LoginAct.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();

        boolean val = SharedPreferenceUtility.getInstance(LoginAct.this)
                .getBoolean(Constant.SELECTED_LANGUAGE);
        if (!val) {
            map.put("language", "en");
        } else {
            map.put("language", "sp");
        }
        map.put("email", strEmail);
        map.put("password", strPassword);
        map.put("register_id", deviceToken);
        map.put("time_zone", id);
        Call<SuccessResSignup> call = apiInterface.login(map);
        call.enqueue(new Callback<SuccessResSignup>() {
            @Override
            public void onResponse(Call<SuccessResSignup> call,
                                   Response<SuccessResSignup> response) {
                try {
                    DataManager.getInstance().hideProgressMessage();
                    SuccessResSignup data = response.body();
                    Log.e("data", data.status);
                    if (data.status.equals("1")) {
                        DataManager.getInstance().hideProgressMessage();
                        String dataResponse = new Gson().toJson(response.body());
                        Log.e("MapMap", "EDIT PROFILE RESPONSE" + dataResponse);
                        SharedPreferenceUtility.getInstance(LoginAct.this).putString(Constant.USER_ID, data.getResult().getId());
                        Toast.makeText(LoginAct.this, "" + getResources().getString(R.string.logged_in_success), Toast.LENGTH_SHORT).show();
                        if (LoginType.equalsIgnoreCase("user")) {
                         /*   if ( data.getResult().getFirst_login().equalsIgnoreCase("0")) {
                                SharedPreferenceUtility.getInstance(LoginAct.this).putString(Constant.USER_TYPE, "user");
                                startActivity(new Intent(LoginAct.this, TakeSelfieAct.class)
                                        .putExtra("loginType", LoginType)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            } else {*/
                                if ( data.getResult().getFirst_login().equalsIgnoreCase("1")) {
                                SharedPreferenceUtility.getInstance(LoginAct.this)
                                        .putString(Constant.USER_TYPE, "user");
                                SharedPreferenceUtility.getInstance(getApplication())
                                        .putBoolean(Constant.IS_USER_LOGGED_IN, true);
                                startActivity(new Intent(LoginAct.this,
                                        HomeUserAct.class));
                                finish();}
                                else {
                                    SharedPreferenceUtility.getInstance(LoginAct.this).
                                            putString(Constant.USER_TYPE, "user");
                                    startActivity(new Intent(LoginAct.this, TakeSelfieAct.class)
                                            .putExtra("loginType", LoginType)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                }

                           // }
                        } else {
                            SharedPreferenceUtility.getInstance(LoginAct.this).putString(Constant.USER_TYPE, "company");
                            SharedPreferenceUtility.getInstance(getApplication()).putBoolean(Constant.IS_USER_LOGGED_IN, true);
                            startActivity(new Intent(LoginAct.this, HomeComapnyAct.class)
                                    .putExtra("loginType",
                                            LoginType)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }

                    } else if (data.status.equals("0")) {
                        if (data.getMessage().equalsIgnoreCase("Your account is not verified please verify your email now")) {
                            getOTP();
                        } else {
                            showToast(LoginAct.this, data.message);
                        }
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
    private void getOTP() {
        Map<String, String> map = new HashMap<>();
        map.put("email", strEmail);
        Call<SuccessResSignup> call = apiInterface.resend_otp(map);
        call.enqueue(new Callback<SuccessResSignup>() {
            @Override
            public void onResponse(Call<SuccessResSignup> call,
                                   Response<SuccessResSignup> response) {
                try {
                    SuccessResSignup data = response.body();
                    Log.e("data", data.status);
                    if (data.status.equals("1")) {
                        showToast(LoginAct.this, getString(R.string.unvirified_ac));
                        DataManager.getInstance().hideProgressMessage();
                        startActivity(new Intent(LoginAct.this,
                                VerificationAct.class)
                                .putExtra("otp", data.getResult().getOtp())
                                .putExtra("user_id", data.getResult().getId())
                                .putExtra("type", LoginType)
                        );
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
    private boolean isValid() {
        if (strEmail.equalsIgnoreCase("")) {
            binding.edtEmail.setError(getString(R.string.enter_email));
            return false;
        } else if (strPassword.equalsIgnoreCase("")) {
            binding.edtPassword.setError(getString(R.string.enter_pass));
            return false;
        }
        return true;
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
    //**************************GOOGLE Login *********************************************
    private void googleSignIn() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //  startActivityForResult(signInIntent, RC_SIGN_IN);
        activityLauncher.launch(signInIntent, result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }


        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(Exception.class);
            Log.e(TAG, "display GoogleSignInAccount:getDisplayName()) " + account.getDisplayName());
            Log.e(TAG, "display GoogleSignInAccount:getEmail()); " + account.getEmail());
            Log.e(TAG, "display GoogleSignInAccount:getPhotoUrl()); " + account.getPhotoUrl());
            Log.e(TAG, "display GoogleSignInAccount:getFamilyName()); " + account.getFamilyName());
            Log.e(TAG, "display GoogleSignInAccount:getGivenName()); " + account.getGivenName());
            Log.e(TAG, "display GoogleSignInAccount:getId()); " + account.getId());
            String personName = account.getDisplayName();
            String email = account.getEmail();
            String socialId = account.getId();
            socialLoginApi(personName, personName, email, socialId, "Google");
        } catch (Exception e) {

            Log.w(TAG, "signInResult:failed code=" + e.getMessage());//StatusCode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        } else if (requestCode == 64206) {
            //callbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
//            AuthManager.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }

    private void socialLoginApi(String first_name, String last_name, String email, String social_id, String social_type) {
        dialog = new ProgressDialog(LoginAct.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        TimeZone tz = TimeZone.getDefault();
        String id = tz.getID();
        String lang = "en";
        boolean val = SharedPreferenceUtility.getInstance(LoginAct.this)
                .getBoolean(Constant.SELECTED_LANGUAGE);
        if (!val) {
            lang= "en";
        } else {
            lang= "sp";
        }
        Call<SocilaLoginResponse> call = apiInterface.socialLogin(
                first_name, "", email, "", "", LoginType,
                "", id, social_id, social_type, deviceToken,lang);//, PrefManager.getString(Constant.REGISTER_ID));
        call.enqueue(new Callback<SocilaLoginResponse>() {
            @Override
            public void onResponse(Call<SocilaLoginResponse> call, Response<SocilaLoginResponse> response) {

                dialog.dismiss();
                try {
                    // Log.e(TAG, "onResponse: " + new Gson().toJson(response));
                    if (response.body() != null) {
                        if (response.body().getStatus().equals("1")) {
                            SharedPreferenceUtility.getInstance(LoginAct.this)
                                    .putString(Constant.USER_ID, response.body().getResult().getId());
                            SharedPreferenceUtility.getInstance(LoginAct.this)
                                    .putString(Constant.USER_NAME, response.body().getResult().getFirstName());
                            SharedPreferenceUtility.getInstance(LoginAct.this)
                                    .putString(Constant.USER_IMAGE, response.body().getResult().getImage());
                          /*  SharedPreferenceUtility.getInstance(getApplication())
                                    .putBoolean(Constant.IS_USER_LOGGED_IN, true);*/
                            if (LoginType.equalsIgnoreCase("user")) {
                                SharedPreferenceUtility.getInstance(LoginAct.this).putString(Constant.USER_TYPE, "user");
                                startActivity(new Intent(LoginAct.this, TakeSelfieAct.class).putExtra("loginType", LoginType));
                                finish();
                            } else {
                                SharedPreferenceUtility.getInstance(LoginAct.this).putString(Constant.USER_TYPE, "company");
                                SharedPreferenceUtility.getInstance(getApplication()).putBoolean(Constant.IS_USER_LOGGED_IN, true);
                                startActivity(new Intent(LoginAct.this, HomeComapnyAct.class).putExtra("loginType", LoginType).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            }

                         /*   startActivity(new Intent(LoginAct.this,
                                    TakeSelfieAct.class).putExtra("loginType", LoginType));

                            finish();*/
                        } else {
                        }

                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dialog.dismiss();
                    //  utils.showToast(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<SocilaLoginResponse> call, Throwable t) {
                call.cancel();
                dialog.dismiss();
                //  utils.showToast(t.getMessage());
            }
        });


    }





    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(LoginAct.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(LoginAct.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.
                    PERMISSION_GRANTED;
        }
    }

    private void requestPermissions() {
        // below line is use to request permission in the current activity.
        // this method is use to handle error in runtime permissions

     if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(LoginAct.this, new String[]{WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }














        Dexter.withContext(this)
                // below line is use to request the number of permissions which are required in our app.
                .withPermissions(Manifest.permission.CAMERA,
                        // below is the list of permissions
                        Manifest.permission.ACCESS_FINE_LOCATION
                        , READ_EXTERNAL_STORAGE,
                        ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)

                // after adding permissions we are calling an with listener method.
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        // this method is called when all permissions are granted
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            // do you work now
                            Toast.makeText(LoginAct.this,
                                    "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                        }
                        // check for permanent denial of any permission
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permanently, we will show user a dialog message.
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        // this method is called when user grants some permission and denies some of them.
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(error -> {
                    // we are displaying a toast message for error message.
                    Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                })
                // below line is use to run the permissions on same thread and to check the permissions
                .onSameThread().check();
    }

    // below is the shoe setting dialog method which is use to display a dialogue message.
    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginAct.this);

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel();
            // below is the intent from which we are redirecting our user.
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // this method is called when user click on negative button.
            dialog.cancel();
        });
        // below line is used to display our dialog
        builder.show();
    }


}


