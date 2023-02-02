package com.toilets.go.ui.LoginSignup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.toilets.go.databinding.ActivityBasicDetailsBinding;
import com.toilets.go.models.SuccessResSignup;
import com.toilets.go.R;
import com.toilets.go.SearchLocationMapAct;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.RealPathUtil;
import com.toilets.go.utills.Session;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.Constant;
import com.toilets.go.retrofit.GosInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BasicDetailsActivity extends AppCompatActivity {
ActivityBasicDetailsBinding binding ;
String UserType="";
    private static final int SELECT_FILE = 2;
    private static final int SELECT_FILE2 = 22;
    String str_image_path = "";
    String str_image_path2 = "";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_CAMERA2 = 12;
    private GosInterface apiInterface;
    private static final int MY_PERMISSION_CONSTANT = 5;
    Session session ;
    boolean cameraClicked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_basic_details);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(this);
        if (getIntent() != null) {
            UserType = getIntent().getExtras().getString("User_type");
        }
binding.edtAddress.setText(session.getRestraID());
         binding.edtAddress.setOnClickListener( v -> {
             if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                     Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                     && ActivityCompat.checkSelfPermission(getApplicationContext(),
                     Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(BasicDetailsActivity.this,
                         new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                 Manifest.permission.ACCESS_COARSE_LOCATION},
                         Constant.LOCATION_REQUEST);
             } else {


                 startActivity(new Intent(BasicDetailsActivity.this,
                         SearchLocationMapAct.class));
             }
         });
        binding.btnSubmit.setOnClickListener(v -> {
                if (binding.edtToiletName.getText().toString().equalsIgnoreCase("")) {
                    binding.edtToiletName.setError(getString(R.string.empty));
                } else if (binding.edtPrice.getText().toString().equalsIgnoreCase("")) {
                    binding.edtPrice.setError(getString(R.string.empty));
                } else if (binding.edtDesc.getText().toString().equalsIgnoreCase("")) {
                    binding.edtDesc.setError(getString(R.string.empty));
                } else if (binding.edtAddress.getText().toString().equalsIgnoreCase("")) {
                    binding.edtAddress.setError(getString(R.string.empty));
                } else  if (str_image_path.equalsIgnoreCase("")) {
                    Toast.makeText(this, getString(R.string.please_pick_image), Toast.LENGTH_SHORT).show();
                } else if (str_image_path2.equalsIgnoreCase("")) {
                    Toast.makeText(this, getString(R.string.please_pick_another_image), Toast.LENGTH_SHORT).show();
                } else {
                    SignupAPI(binding.edtToiletName.getText().toString()
                            , binding.edtPrice.getText().toString()
                            , binding.edtDesc.getText().toString()
                            , binding.edtAddress.getText().toString());

                }




        });

        binding.ivAddPost.setOnClickListener(v ->
                {
                    final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.select_from_gallery), getString(R.string.cancel)};
                    AlertDialog.Builder builder = new AlertDialog.Builder(BasicDetailsActivity.this);
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
        binding.ivAddPost2.setOnClickListener(v ->
                {
                    final CharSequence[] options = {getString(R.string.take_photo), getString(R.string.select_from_gallery), getString(R.string.cancel)};
                    AlertDialog.Builder builder = new AlertDialog.Builder(BasicDetailsActivity.this);
                    builder.setTitle(getString(R.string.select_image));
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals(getString(R.string.take_photo))) {
                                cameraClicked = true;
                                if (checkPermisssionForReadStorage()) {

                                    openCamera2();
                                }
                            } else if (options[item].equals(getString(R.string.select_from_gallery))) {
                                cameraClicked = false;
                                if (checkPermisssionForReadStorage()) {

                                    getPhotoFromGallary2();

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


    private void SignupAPI(String name, String price, String desc, String address) {
        DataManager.getInstance().showProgressMessage(BasicDetailsActivity.this, 
                getString(R.string.please_wait));
        MultipartBody.Part filePart;
        if (!str_image_path.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path));
            if (file != null) {
                filePart = MultipartBody.Part.createFormData("image1", file.getName(),
                        RequestBody.create(MediaType.parse("image/*"), file));
            } else {
                filePart = null;
            }

        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }
        MultipartBody.Part filePart2 ;
        if (!str_image_path2.equalsIgnoreCase("")) {
            File file = DataManager.getInstance().saveBitmapToFile(new File(str_image_path2));
            if (file != null) {
                filePart2 = MultipartBody.Part.createFormData("image2", file.getName(),
                        RequestBody.create(MediaType.parse("image/*"), file));
            } else {
                filePart2 = null;
            }

        } else {
            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            filePart2= MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        }

        RequestBody User_id = RequestBody.create(MediaType.parse("text/plain"), session.getUserId());
        RequestBody T_name = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody Descript = RequestBody.create(MediaType.parse("text/plain"), desc);
        RequestBody Address = RequestBody.create(MediaType.parse("text/plain"), address);
        RequestBody PRICE = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), session.getAuthtoken());
        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), session.getHOME_LAT());
        RequestBody lang = RequestBody.create(MediaType.parse("text/plain"), session.getHOME_LONG());
        Call<SuccessResSignup> call = apiInterface.add_toilet(User_id, T_name, PRICE, Descript,
                Address, lat, lang, token, filePart,filePart2);
        call.enqueue(new Callback<SuccessResSignup>() {
            @Override
            public void onResponse(Call<SuccessResSignup> call, Response<SuccessResSignup> response) {
                DataManager.getInstance().hideProgressMessage();
                try {
                    String data = response.body().getStatus();
                    String message = response.body().getMessage();
                    if (data.equals("1")) {
                        SuccessResSignup.Result signup = response.body().getResult();
                        Gson gson = new Gson();
                        String json = gson.toJson(signup);

                        startActivity(new Intent(getApplicationContext(), BankDetailsActivity.class)
                                .putExtra("User_type", UserType));

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
    private void getPhotoFromGallary2() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE2);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(BasicDetailsActivity.this.getPackageManager()) != null)
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }
    private void openCamera2() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(BasicDetailsActivity.this.getPackageManager()) != null)
            startActivityForResult(cameraIntent, REQUEST_CAMERA2);
    }

    //CHECKING FOR Camera STATUS
    public boolean checkPermisssionForReadStorage() {
        if (ContextCompat.checkSelfPermission(BasicDetailsActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(BasicDetailsActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ||

                ContextCompat.checkSelfPermission(BasicDetailsActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(BasicDetailsActivity.this,
                    Manifest.permission.CAMERA)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(BasicDetailsActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    ||
                    ActivityCompat.shouldShowRequestPermissionRationale(BasicDetailsActivity.this,
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
                    Toast.makeText(BasicDetailsActivity.this, getResources()
                                    .getString(R.string.permission_denied_boo),
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
                        Toast.makeText(BasicDetailsActivity.this, getResources().getString(R.string.permission_denied_boo),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BasicDetailsActivity.this, getResources().getString(R.string.permission_denied_boo),
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
                    Glide.with(BasicDetailsActivity.this)
                            .load(selectedImage)
                            .centerCrop()
                            .into(binding.ivAddPost);
                    Uri tempUri = getImageUri(BasicDetailsActivity.this, bitmap);
                    String image = RealPathUtil.getRealPath(BasicDetailsActivity.this, tempUri);
                    str_image_path = image;

                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }

            } else 
                if (requestCode == REQUEST_CAMERA) {

                try {
                    if (data != null) {
                        // TODO
                        Bundle extras = data.getExtras();
                        Bitmap bitmapNew = (Bitmap) extras.get("data");
                        Bitmap imageBitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());
                        Uri tempUri = getImageUri(BasicDetailsActivity.this, imageBitmap);
                        String image = RealPathUtil.getRealPath(BasicDetailsActivity.this, tempUri);
                        str_image_path = image;
                        Glide.with(BasicDetailsActivity.this)
                                .load(imageBitmap)
                                .centerCrop()
                                .into(binding.ivAddPost);
//                        updateCoverPhoto();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else 

            if (requestCode == SELECT_FILE2) {
                try {
                    Uri selectedImage = data.getData();
                    Bitmap bitmapNew = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    Bitmap bitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());
                    Glide.with(BasicDetailsActivity.this)
                            .load(selectedImage)
                            .centerCrop()
                            .into(binding.ivAddPost2);
                    Uri tempUri = getImageUri(BasicDetailsActivity.this, bitmap);
                    String image = RealPathUtil.getRealPath(BasicDetailsActivity.this, tempUri);
                    str_image_path2 = image;

                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }

            } else
            if (requestCode == REQUEST_CAMERA2) {

                try {
                    if (data != null) {
                        // TODO
                        Bundle extras = data.getExtras();
                        Bitmap bitmapNew = (Bitmap) extras.get("data");
                        Bitmap imageBitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());
                        Uri tempUri = getImageUri(BasicDetailsActivity.this, imageBitmap);
                        String image = RealPathUtil.getRealPath(BasicDetailsActivity.this, tempUri);
                        str_image_path2 = image;
                        Glide.with(BasicDetailsActivity.this)
                                .load(imageBitmap)
                                .centerCrop()
                                .into(binding.ivAddPost2);
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
}