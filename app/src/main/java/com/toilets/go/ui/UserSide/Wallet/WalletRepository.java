package com.toilets.go.ui.UserSide.Wallet;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.toilets.go.models.AddWalletResponse;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletRepository {
    private static final String TAG = WalletRepository.class.getSimpleName();
    private GosInterface apiRequest;

    public WalletRepository() {
        apiRequest = ApiClient.getClient().create(GosInterface.class);
    }

    public LiveData<WalletResponse> getMovieArticles(String user_id, String auth) {
        final MutableLiveData<WalletResponse> data = new MutableLiveData<>();
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("token", auth);
        apiRequest.get_wallet_history(map)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<WalletResponse> call,
                                           @NonNull Response<WalletResponse> response) {
                        Log.d(TAG, "onResponse response:: " + response);
                        if (response.body() != null) {
                            data.setValue(response.body());
                            Log.d(TAG, "articles total result:: " + response.body().message);
                            Log.d(TAG, "articles size:: " + response.body().result.size());
                            Log.d(TAG, "articles title pos 0:: " + response.body().result.get(0).id);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WalletResponse> call, @NonNull Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }

    public LiveData<AddWalletResponse> getAddMoney(String query, String user_id, String auth) {
        final MutableLiveData<AddWalletResponse> data = new MutableLiveData<>();
        Map<String, String> map = new HashMap<>();
        map.put("amount", query);
        map.put("user_id", user_id);
        map.put("token", auth);
        apiRequest.add_money(map)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<AddWalletResponse> call,
                                           @NonNull Response<AddWalletResponse> response) {
                        Log.d(TAG, "onResponse response:: " + response);
                        if (response.body() != null) {
                            data.setValue(response.body());
                            Log.d(TAG, "articles total result:: " + response.body().message);
                            Log.d(TAG, "articles size:: " + response.body().result);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AddWalletResponse> call, @NonNull Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }


}
