package com.toilets.go.utills;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.toilets.go.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class DataManager {

    private static final DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
            return ourInstance;
        }

        private DataManager() {
        }

        private Dialog mDialog;
        private boolean isProgressDialogRunning = false;
//        WP10ProgressBar progressBar;
        ProgressBar progressBar;

        public void showProgressMessage(Activity dialogActivity, String msg) {
            try {
                if (isProgressDialogRunning) {
                    hideProgressMessage();
                }
                isProgressDialogRunning = true;
                mDialog = new Dialog(dialogActivity);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setContentView(R.layout.dialog_loading);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //TextView textView = mDialog.findViewById(R.id.textView);
                progressBar = mDialog.findViewById(R.id.progressBar);
               // textView.setText(msg);
                WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
                lp.dimAmount = 0.6f;

                mDialog.getWindow().setAttributes(lp);
                mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        public static void showNoInternet(Activity dialogActivity, Boolean msg) {
            try {
                 Dialog   silog = new Dialog(dialogActivity);
              silog.requestWindowFeature(Window.FEATURE_NO_TITLE);
              silog.setContentView(R.layout.dialog_no_internet);
              silog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams lp = silog.getWindow().getAttributes();
                lp.dimAmount = 0.6f;
               silog.getWindow().setAttributes(lp);
               silog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
               silog.setCancelable(msg);
               silog.setCanceledOnTouchOutside(msg);
               silog.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void hideProgressMessage() {
            isProgressDialogRunning = true;
            try {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        public static String toBase64(String message) {
            byte[] data;
            try {
                data = message.getBytes("UTF-8");
                String base64Sms = Base64.encodeToString(data, Base64.DEFAULT);
                return base64Sms;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static String fromBase64(String message) {
            byte[] data = Base64.decode(message, Base64.DEFAULT);
            try {
                return new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return null;
        }

     public static String convertDateToString(long l) {
            String str = "";
            Date date = new Date(l);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
            str = dateFormat.format(date);
            return str;
        }
    public static void showToast(Activity context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }
    public static String CurrentCity(double latitude, double longitude, Context c) {
        if (latitude >= 0.0) {
            Geocoder geocoder = new Geocoder(c, Locale.getDefault());
            //List<Address> addresses =geocoder.getFromLocation(latitude, longitude, 1);

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getSubLocality();
                String cityName = addresses.get(0).getLocality();
                String stateName = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                // txt_paddress.setText(address);
                return address+","+cityName+","+stateName+","+country;
                //   txt_state.setText(stateName);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "CurrentCity: " + e.getLocalizedMessage());
                Log.e(TAG, "CurrentCity: " + e.getMessage());
                Log.e(TAG, "CurrentCity: " + e.getCause());
            }
        }

        return "  ";

    }

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }
}
