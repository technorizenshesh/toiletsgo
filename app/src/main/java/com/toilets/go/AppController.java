package com.toilets.go;


import static android.content.ContentValues.TAG;

import static com.toilets.go.utills.DataManager.checkConnection;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleObserver;

import com.bumptech.glide.Glide;
import com.toilets.go.utills.Session;


public class AppController extends Application implements LifecycleObserver {
    private static Context context;
    Session session;
    int c = 0;

/*

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onCreate(owner);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
       // stopService();
        DefaultLifecycleObserver.super.onPause(owner);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
      //  stopService();
        DefaultLifecycleObserver.super.onStop(owner);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {

        DefaultLifecycleObserver.super.onDestroy(owner);
    }
*/

    public static Context getContext() {
        return AppController.context;
    }

    /*   @SuppressLint("CheckResult")
       @OnLifecycleEvent(Lifecycle.Event.ON_START)
       public void onMoveToForeground() {
           if (session.isLoggedIn()) {
             //  startService();
           }
   
       }
   
       @SuppressLint("CheckResult")
       @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
       public void onMoveToBackground() {
       }
   */
    @BindingAdapter("profileImage")
    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.user)
                .into(view);
    }

    public void onCreate() {
        super.onCreate();
        //   ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        AppController.context = getApplicationContext();
        session = new Session(AppController.context);
        if (checkConnection(this)) {
            Log.e(TAG, "YE:checkConnectioncheckConnectioncheckConnectioncheckConnection " );
        } else {
            Log.e(TAG, "Neh:checkConnectioncheckConnectioncheckConnectioncheckConnection " );

            Nodata(context);
        }
//        if (session.isLoggedIn()) {
//            if (c>=1){
//
//            }else {
//                c++;
//            startService();}
//        }
    }

    private void Nodata(Context context) {
    }

    @Override
    public void onTerminate() {
        //stopService();
        super.onTerminate();
    }
}