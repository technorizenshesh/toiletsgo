package com.toilets.go.ui.LoginSignup;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.toilets.go.adapters.CountryAdapter;
import com.toilets.go.adapters.GenderAdapter;
import com.toilets.go.databinding.ActivitySignupBinding;
import com.toilets.go.models.SuccessResGetCountry;
import com.toilets.go.models.SuccessResGetGender;
import com.toilets.go.models.SuccessResSignup;
import com.toilets.go.R;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.RealPathUtil;
import com.toilets.go.utills.RecyclerTouchListener;
import com.toilets.go.utills.Session;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class SignupActivity extends AppCompatActivity {
    String UserType = "";
    String Country = "";
    String Gender = "";
    ActivitySignupBinding binding;
    private static final int SELECT_FILE = 2;
    String str_image_path = "";
    private static final int REQUEST_CAMERA = 1;
    private GosInterface apiInterface;
    private static final int MY_PERMISSION_CONSTANT = 5;
    boolean cameraClicked = true;
    Session session;
    private List<SuccessResGetCountry.Result> country_list;
    private List<SuccessResGetGender.Result> gender_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(this);
        FirebaseApp.initializeApp(this);
        getToken();
        if (getIntent() != null) {
            UserType = getIntent().getExtras().getString("User_type");
        }
        get_countryAPI();
        get_genderAPI();
        binding.btnSubmit.setOnClickListener(v -> {
            if (binding.edtFirstName.getText().toString().equalsIgnoreCase("")) {
                binding.edtFirstName.setError(getString(R.string.empty));
            } else if (binding.edtLastName.getText().toString().equalsIgnoreCase("")) {
                binding.edtLastName.setError(getString(R.string.empty));
            } else if (binding.edtEmail.getText().toString().equalsIgnoreCase("")) {
                binding.edtEmail.setError(getString(R.string.empty));
            } else if (binding.edtpass.getText().toString().equalsIgnoreCase("")) {
                binding.edtpass.setError(getString(R.string.empty));
            } else if (binding.edtGender.getText().toString().equalsIgnoreCase("")) {
                binding.edtGender.setError(getString(R.string.empty));
            } else if (binding.edtCountry.getText().toString().equalsIgnoreCase("")) {
                binding.edtCountry.setError(getString(R.string.empty));
            } else if (str_image_path.equalsIgnoreCase("")) {
                Toast.makeText(this, getString(R.string.please_pick_image), Toast.LENGTH_SHORT).show();
            } else {
                SignupAPI(binding.edtFirstName.getText().toString()
                        , binding.edtLastName.getText().toString()
                        , binding.edtEmail.getText().toString()
                        , binding.edtpass.getText().toString());

            }
        });
        binding.goLogin.setOnClickListener(v -> {

            startActivity(new Intent(this, LoginActivity.class)
                    .putExtra("User_type", UserType));

        });
        binding.edtCountry.setOnClickListener(v -> {
            OpenBottemSheet(getString(R.string.select_country));
        });
        binding.edtGender.setOnClickListener(v -> {
            OpenBottemSheet(getString(R.string.select_gender));
        });
        binding.ivAddPost.setOnClickListener(v ->
                {
                    final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.select_from_gallery), getString(R.string.cancel)};
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setTitle(getString(R.string.select_image));
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals(getString(R.string.take_photo))) {
                                cameraClicked = true;
                                if (checkPermisssionForReadStorage()) {

                                    openCamera();
                                }
                            } else if (options[item].equals(getString(R.string.select_from_gallery))) {
                                cameraClicked = false;
                                if (checkPermisssionForReadStorage()) {

                                    getPhotoFromGallary();

                                }
                            } else if (options[item].equals(getString(R.string.cancel))) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
        );
    }

    private void SignupAPI(String first, String last, String email, String pass) {
        DataManager.getInstance().showProgressMessage(SignupActivity.this, getString(R.string.please_wait));
        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            if (file != null) {
                filePart = MultipartBody.Part.createFormData("image", file.getName(),
                        RequestBody.create(MediaType.parse("image/*"), file));
            } else {
                filePart = null;
            }

        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }

        RequestBody firstname = RequestBody.create(MediaType.parse("text/plain"), first);
        RequestBody lastname = RequestBody.create(MediaType.parse("text/plain"), last);
        RequestBody emailaddress = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), pass);
        RequestBody cntry = RequestBody.create(MediaType.parse("text/plain"), Country);
        RequestBody gende = RequestBody.create(MediaType.parse("text/plain"), Gender);
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), UserType);
        RequestBody regist = RequestBody.create(MediaType.parse("text/plain"), "1234rf");
        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), "1234rf");
        RequestBody lang = RequestBody.create(MediaType.parse("text/plain"), "1234rf");
        Call<SuccessResSignup> call = apiInterface.signup(firstname, lastname, emailaddress, password,
                cntry, gende, regist, type, filePart);
        call.enqueue(new Callback<SuccessResSignup>() {
            @Override
            public void onResponse(Call<SuccessResSignup> call, Response<SuccessResSignup> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    if (response.body()!=null) {
                        String data = response.body().getStatus();
                        String message = response.body().getMessage();
                        if (data.equals("1")) {
                            SuccessResSignup.Result signup = response.body().getResult();
                            Gson gson = new Gson();
                            String json = gson.toJson(signup);
                            session.setUSERDATA(json);
                            session.setUserId(signup.getId());
                            session.setLogin(true);
                            session.setUSERTYPE(UserType);
                            session.setAuthtoken(response.body().getResult().getToken());
                            if (session.getUSERTYPE().equalsIgnoreCase("USER")) {
                                startActivity(new Intent(SignupActivity.this, BankDetailsActivity.class)
                                        .putExtra("User_type", UserType));
                            } else {
                                startActivity(new Intent(SignupActivity.this, BasicDetailsActivity.class)
                                        .putExtra("User_type", UserType));
                            }

                        } else {
                            Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {

                    Log.d("TAG", "onResponse: " + e);
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


    private void getPhotoFromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(SignupActivity.this.getPackageManager()) != null)
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(SignupActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(SignupActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(SignupActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this,
                    Manifest.permission.CAMERA)

                    ||

                    ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)

            ) {

                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.
                                READ_EXTERNAL_STORAGE, Manifest.permission
                                .WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);

            } else {

                requestPermissions(
                        new String[]{Manifest.permission.CAMERA, Manifest.permission
                                .READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_CONSTANT);
            }
            return false;
        } else {

            //  explain("Please Allow Location Permission");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 1000: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.permission_denied_boo),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case MY_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean read_external_storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean write_external_storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (camera && read_external_storage && write_external_storage) {
                        if (cameraClicked) {
                            openCamera();
                        } else {
                            getPhotoFromGallary();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, getResources().getString(R.string.permission_denied_boo),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.permission_denied_boo),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("Result_code", requestCode + "");

            if (requestCode == SELECT_FILE) {
                try {
                    Uri selectedImage = data.getData();
                    Bitmap bitmapNew = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    Bitmap bitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());
                    Glide.with(SignupActivity.this)
                            .load(selectedImage)
                            .centerCrop()
                            .into(binding.ivProfile);
                    Uri tempUri = getImageUri(SignupActivity.this, bitmap);
                    String image = RealPathUtil.getRealPath(SignupActivity.this, tempUri);
                    str_image_path = image;

                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }

            } else if (requestCode == REQUEST_CAMERA) {

                try {
                    if (data != null) {
                        // TODO
                        Bundle extras = data.getExtras();
                        Bitmap bitmapNew = (Bitmap) extras.get("data");
                        Bitmap imageBitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());
                        Uri tempUri = getImageUri(SignupActivity.this, imageBitmap);
                        String image = RealPathUtil.getRealPath(SignupActivity.this, tempUri);
                        str_image_path = image;
                        Glide.with(SignupActivity.this)
                                .load(imageBitmap)
                                .centerCrop()
                                .into(binding.ivProfile);
//                        updateCoverPhoto();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public Bitmap BITMAP_RE_SIZER(Bitmap bitmap, int newWidth, int newHeight) {

        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    private void OpenBottemSheet(String type) {
        RoundedBottomSheetDialog mBottomSheetDialog = new RoundedBottomSheetDialog(SignupActivity.this);
        View sheetView = mBottomSheetDialog.getLayoutInflater().inflate(R.layout.home_bottam, null);
        mBottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mBottomSheetDialog.setContentView(sheetView);
        TextView title = mBottomSheetDialog.findViewById(R.id.title);
        title.setText(type);
        RecyclerView recycle = mBottomSheetDialog.findViewById(R.id.recycle_bottem);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SignupActivity.this);
        recycle.setLayoutManager(linearLayoutManager);
        mBottomSheetDialog.show();
        if (type.equalsIgnoreCase(getString(R.string.select_country))) {
            CountryAdapter adapter = new CountryAdapter(getApplicationContext(), country_list);
            recycle.setAdapter(adapter);
            recycle.addOnItemTouchListener(new RecyclerTouchListener(SignupActivity.this, recycle,
                    new RecyclerTouchListener.ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            Log.e("->>", "" + country_list.get(position));
                            Log.e("->>", country_list.get(position).getName());
                            Log.e("->>", country_list.get(position).getId());
                            binding.edtCountry.setText(country_list.get(position).getName());
                            Country = country_list.get(position).getId();
                            mBottomSheetDialog.dismiss();

                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));
        } else {
            GenderAdapter adapter = new GenderAdapter(getApplicationContext(), gender_list);
            recycle.setAdapter(adapter);
            recycle.addOnItemTouchListener(new RecyclerTouchListener(SignupActivity.this, recycle,
                    new RecyclerTouchListener.ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            Log.e("->>", "" + gender_list.get(position));
                            Log.e("->>", gender_list.get(position).getName());
                            Log.e("->>", gender_list.get(position).getId());
                            binding.edtGender.setText(gender_list.get(position).getName());
                            Gender = gender_list.get(position).getId();
                            mBottomSheetDialog.dismiss();

                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));

        }
    }

    private void get_countryAPI() {
       // DataManager.getInstance().showProgressMessage(SignupActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        Call<SuccessResGetCountry> call = apiInterface.get_country();
        call.enqueue(new Callback<SuccessResGetCountry>() {
            @Override
            public void onResponse(Call<SuccessResGetCountry> call,
                                   Response<SuccessResGetCountry> response) {
           //     DataManager.getInstance().hideProgressMessage();
                try {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        country_list = response.body().getResult();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResGetCountry> call, Throwable t) {
                call.cancel();
                Log.e("TAG", "onFailure: " + t.getLocalizedMessage());
                Log.e("TAG", "onFailure: " + t.getMessage());
            //    DataManager.getInstance().hideProgressMessage();
            }
        });

    }

    private void get_genderAPI() {
      //  DataManager.getInstance().showProgressMessage(SignupActivity.this, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
        map.put("user_id", session.getUserId());
        Call<SuccessResGetGender> call = apiInterface.get_gender();
        call.enqueue(new Callback<SuccessResGetGender>() {
            @Override
            public void onResponse(Call<SuccessResGetGender> call,
                                   Response<SuccessResGetGender> response) {
              //  DataManager.getInstance().hideProgressMessage();
                try {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        gender_list = response.body().getResult();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessResGetGender> call, Throwable t) {
                call.cancel();
                Log.e("TAG", "onFailure: " + t.getLocalizedMessage());
                Log.e("TAG", "onFailure: " + t.getMessage());
              //  DataManager.getInstance().hideProgressMessage();
            }
        });

    }
    private void getToken() {
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.e(TAG, "getToken: =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"+token );
                        session.setFireBaseToken(token);
                    });
        } catch (Exception e) {
            Toast.makeText(SignupActivity.this, "Error=>" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}