package com.toilets.go.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public  class SuccessResNearbyList implements Serializable {

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

  public class Result implements Serializable
  {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("toilet_name")
    @Expose
    private String toiletName;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("image1")
    @Expose
    private String image1;
    @SerializedName("image2")
    @Expose
    private String image2;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("estimate_time")
    @Expose
    private Integer estimateTime;
    @SerializedName("image")
    @Expose
    private String image;

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

    public String getToiletName() {
      return toiletName;
    }

    public void setToiletName(String toiletName) {
      this.toiletName = toiletName;
    }

    public String getPrice() {
      return price;
    }

    public void setPrice(String price) {
      this.price = price;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }

    public String getLat() {
      return lat;
    }

    public void setLat(String lat) {
      this.lat = lat;
    }

    public String getLon() {
      return lon;
    }

    public void setLon(String lon) {
      this.lon = lon;
    }

    public String getDateTime() {
      return dateTime;
    }

    public void setDateTime(String dateTime) {
      this.dateTime = dateTime;
    }

    public String getImage1() {
      return image1;
    }

    public void setImage1(String image1) {
      this.image1 = image1;
    }

    public String getImage2() {
      return image2;
    }

    public void setImage2(String image2) {
      this.image2 = image2;
    }

    public String getDistance() {
      return distance;
    }

    public void setDistance(String distance) {
      this.distance = distance;
    }

    public Integer getEstimateTime() {
      return estimateTime;
    }

    public void setEstimateTime(Integer estimateTime) {
      this.estimateTime = estimateTime;
    }

    public String getImage() {
      return image;
    }

    public void setImage(String image) {
      this.image = image;
    }

  }
}