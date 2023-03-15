package com.toilets.go.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SuccessResRatingList  implements Serializable {

    @SerializedName("result")
    @Expose
    private ArrayList<Result> result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public ArrayList<Result> getResult() {
        return result;
    }

    public void setResult(ArrayList<Result> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Result implements Serializable {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("toilet_id")
        @Expose
        private String toiletId;
        @SerializedName("rating")
        @Expose
        private String rating;
        @SerializedName("comment")
        @Expose
        private String comment;
        @SerializedName("date_time")
        @Expose
        private String dateTime;
        @SerializedName("time_ago")
        @Expose
        private String time_ago;
        @SerializedName("user_name")
        @Expose
        private String user_name;
        @SerializedName("image")
        @Expose
        private String image;

        public String getTime_ago() {
            return time_ago;
        }

        public void setTime_ago(String time_ago) {
            this.time_ago = time_ago;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getToiletId() {
            return toiletId;
        }

        public void setToiletId(String toiletId) {
            this.toiletId = toiletId;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }


    }
}
