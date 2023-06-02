package com.toilets.go.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
  public  class SuccessResGetCountry   implements Serializable {
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

    public class Result implements Serializable {

      @SerializedName("id")
      @Expose
      private String id;
      @SerializedName("sortname")
      @Expose
      private String sortname;
      @SerializedName("name")
      @Expose
      private String name;
      @SerializedName("status")
      @Expose
      private String status;

      public String getId() {
        return id;
      }

      public void setId(String id) {
        this.id = id;
      }

      public String getSortname() {
        return sortname;
      }

      public void setSortname(String sortname) {
        this.sortname = sortname;
      }

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getStatus() {
        return status;
      }

      public void setStatus(String status) {
        this.status = status;
      }

    }
  }