package com.toilets.go.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SuccessResCheckRes  implements Serializable {
    @SerializedName("establishment_status")
    @Expose
    private String establishmentStatus;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    public String getEstablishmentStatus() {
        return establishmentStatus;
    }

    public void setEstablishmentStatus(String establishmentStatus) {
        this.establishmentStatus = establishmentStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}