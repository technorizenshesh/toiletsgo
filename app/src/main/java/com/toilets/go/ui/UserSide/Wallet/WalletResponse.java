package com.toilets.go.ui.UserSide.Wallet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.toilets.go.models.SuccessResGetCountry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WalletResponse   implements Serializable {
        @SerializedName("result")
        @Expose
        public ArrayList<Result> result;
        @SerializedName("message")
        @Expose
        public String message;
        @SerializedName("status")
        @Expose
        public String status;

    public class Result implements Serializable {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("user_id")
        @Expose
        public String userId;
        @SerializedName("amount")
        @Expose
        public String amount;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
        @SerializedName("txt_id")
        @Expose
        public String txtId;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("user_name")
        @Expose
        public String userName;

    }
}
