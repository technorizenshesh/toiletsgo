package com.toilets.go.ui.UserSide.Review;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.toilets.go.R;
import com.toilets.go.adapters.NotificationListAdapter;
import com.toilets.go.databinding.FragmentReviewBinding;
import com.toilets.go.models.SuccessResNotifications;
import com.toilets.go.retrofit.ApiClient;
import com.toilets.go.retrofit.GosInterface;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReviewFragment extends Fragment {
    FragmentReviewBinding binding;
    NotificationListAdapter myRecyclerViewAdapter;
    Session session;
    private GosInterface apiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_review, container, false);
        apiInterface = ApiClient.getClient().create(GosInterface.class);
        session = new Session(requireActivity());
        binding.header.tvtitle.setText("Rating");
        binding.header.imgHeader.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
        binding.btnSubmit.setOnClickListener(v -> {

            String rating = String.valueOf(binding.ratingBar.getRating());
            String review = binding.etComment.getText().toString();
            if (review.equalsIgnoreCase("")) {
                binding.etComment.setError(getString(R.string.empty));
                DataManager.showToast(requireActivity(), getString(R.string.empty));
            } else {
                submitReview(rating,
                        review);
            }
        });
        return binding.getRoot();
    }

    private void submitReview(String rating, String review)
        {
            DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
            Map<String, String> map = new HashMap<>();
            map.put("rating", rating);
            map.put("comment", review);
            map.put("toilet_id", session.getUserId());
            map.put("token", session.getAuthtoken());
            Log.e(TAG, "sendRequestAPI: " + map);
            Call<ResponseBody> call = apiInterface.add_rating(map);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       Response<ResponseBody> response) {
                    try {
                        DataManager.getInstance().hideProgressMessage();
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        String data = jsonObject.getString("status");

                        String message = jsonObject.getString("message");
                            Toast.makeText(requireActivity(),message, Toast.LENGTH_SHORT).show();

                    //    }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                    Log.e(TAG, "onFailure: " + t.getCause());
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    Toast.makeText(requireActivity(), t.getCause().toString(),
                            Toast.LENGTH_SHORT).show();
                    DataManager.getInstance().hideProgressMessage();
                }
            });

        }

}