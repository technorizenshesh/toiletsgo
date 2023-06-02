package com.toilets.go.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddWalletResponse implements Serializable {
        @SerializedName("result")
        @Expose
        public Result result;
        @SerializedName("message")
        @Expose
        public String message;
        @SerializedName("status")
        @Expose
        public String status;

    public class Result  implements Serializable{
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("first_name")
        @Expose
        public String firstName;
        @SerializedName("last_name")
        @Expose
        public String lastName;
        @SerializedName("user_name")
        @Expose
        public String userName;
        @SerializedName("country")
        @Expose
        public String country;
        @SerializedName("dob")
        @Expose
        public String dob;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("password")
        @Expose
        public String password;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("wallet")
        @Expose
        public String wallet;
        @SerializedName("register_id")
        @Expose
        public String registerId;
        @SerializedName("social_id")
        @Expose
        public String socialId;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("token")
        @Expose
        public String token;
        @SerializedName("expired_at")
        @Expose
        public String expiredAt;
        @SerializedName("last_login")
        @Expose
        public String lastLogin;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
        @SerializedName("mobile")
        @Expose
        public String mobile;
        @SerializedName("ios_register_id")
        @Expose
        public Object iosRegisterId;
        @SerializedName("lat")
        @Expose
        public String lat;
        @SerializedName("lon")
        @Expose
        public String lon;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("city")
        @Expose
        public String city;
        @SerializedName("step")
        @Expose
        public String step;
        @SerializedName("gender")
        @Expose
        public String gender;
        @SerializedName("establishment_no")
        @Expose
        public String establishmentNo;

    }
}
