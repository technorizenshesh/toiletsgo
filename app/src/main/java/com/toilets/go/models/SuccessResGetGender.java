package com.toilets.go.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public  class SuccessResGetGender implements Serializable

  {

    @SerializedName("result")
    @Expose
    private List<Result> result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Result> getResult() {
      return result;
    }

    public void setResult(List<Result> result) {
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
    public class Result implements Serializable
    {

      @SerializedName("id")
      @Expose
      private String id;
      @SerializedName("name")
      @Expose
      private String name;
      @SerializedName("date_time")
      @Expose
      private String dateTime;
      private final static long serialVersionUID = -4424055283696988143L;

      public String getId() {
        return id;
      }

      public void setId(String id) {
        this.id = id;
      }

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getDateTime() {
        return dateTime;
      }

      public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
      }

    }
  }
