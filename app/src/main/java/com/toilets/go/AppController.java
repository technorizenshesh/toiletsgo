package com.toilets.go;


import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleObserver;

import com.bumptech.glide.Glide;
import com.toilets.go.utills.Session;


public class AppController extends Application implements LifecycleObserver {
    private static Context context;
    Session session;
    int c = 0;

    public void onCreate() {
        super.onCreate();
        //   ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        AppController.context = getApplicationContext();
        session = new Session(AppController.context);
//        if (session.isLoggedIn()) {
//            if (c>=1){
//
//            }else {
//                c++;
//            startService();}
//        }
    }
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

    @Override
    public void onTerminate() {
        //stopService();
        super.onTerminate();
    }

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
}