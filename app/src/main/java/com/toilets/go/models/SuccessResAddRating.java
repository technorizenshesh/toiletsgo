package com.toilets.go.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuccessResAddRating {
    @SerializedName("result")
    @Expose
    private SuccessResAcptRej.Result result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public SuccessResAcptRej.Result getResult() {
        return result;
    }

    public void setResult(SuccessResAcptRej.Result result) {
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
}
