package com.toilets.go.retrofit;


import com.toilets.go.models.SuccessResBooking;
import com.toilets.go.models.SuccessResGetCountry;
import com.toilets.go.models.SuccessResGetGender;
import com.toilets.go.models.SuccessResNearbyList;
import com.toilets.go.models.SuccessResProfile;
import com.toilets.go.models.SuccessResRequests;
import com.toilets.go.models.SuccessResSignup;
import com.toilets.go.models.SuccessResAddbank;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface GosInterface {
    @FormUrlEncoded
    @POST("login")
    Call<SuccessResSignup> login(@FieldMap Map<String, String> paramHashMap);

    @Multipart
    @POST("signup")
    Call<SuccessResSignup> signup(
            @Part("first_name") RequestBody first_name,
            @Part("last_name") RequestBody last_name,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("country") RequestBody country,
            @Part("gender") RequestBody gender,
            @Part("register_id") RequestBody register_id,
            @Part("type") RequestBody type,
            @Part MultipartBody.Part file);

    @Multipart
    @POST("add_toilet")
    Call<SuccessResSignup> add_toilet(
            @Part("user_id") RequestBody user_id,
            @Part("toilet_name") RequestBody toilet_name,
            @Part("price") RequestBody price,
            @Part("description") RequestBody description,
            @Part("address") RequestBody address,
            @Part("lat") RequestBody lat,
            @Part("lon") RequestBody lon,
            @Part("token") RequestBody token,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part file2);

    @GET("get_country")
    Call<SuccessResGetCountry> get_country();
    @GET("get_gender")
    Call<SuccessResGetGender> get_gender();
    @FormUrlEncoded
    @POST("get_profile")
    Call<SuccessResProfile> get_profile(@FieldMap Map<String, String> paramHashMap);
    @FormUrlEncoded
    @POST("add_update_bank_account")
    Call<SuccessResAddbank> add_bank_account(@FieldMap Map<String, String> paramHashMap);
    @FormUrlEncoded
    @POST("get_provider_list_nearbuy")
    Call<SuccessResNearbyList> get_provider_list_nearbuy(@FieldMap Map<String, String> paramHashMap);
    @FormUrlEncoded
    @POST("booking_request")
    Call<SuccessResBooking> booking_request(@FieldMap Map<String, String> paramHashMap);
    @FormUrlEncoded
    @POST("get_order_history")
    Call<SuccessResRequests> get_order_history(@FieldMap Map<String, String> paramHashMap);


}