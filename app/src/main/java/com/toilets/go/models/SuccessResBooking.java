package com.toilets.go.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public  class SuccessResBooking implements Serializable {
  @SerializedName("result")
  @Expose
  private Result result;
  @SerializedName("message")
  @Expose
  private String message;
  @SerializedName("status")
  @Expose
  private String status;

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
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
    @SerializedName("cart_id")
    @Expose
    private String cartId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("order_time")
    @Expose
    private String orderTime;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("provider_id")
    @Expose
    private String providerId;
    @SerializedName("status")
    @Expose
    private String status;

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

    public String getCartId() {
      return cartId;
    }

    public void setCartId(String cartId) {
      this.cartId = cartId;
    }

    public String getAmount() {
      return amount;
    }

    public void setAmount(String amount) {
      this.amount = amount;
    }

    public String getDateTime() {
      return dateTime;
    }

    public void setDateTime(String dateTime) {
      this.dateTime = dateTime;
    }

    public String getOrderDate() {
      return orderDate;
    }

    public void setOrderDate(String orderDate) {
      this.orderDate = orderDate;
    }

    public String getOrderTime() {
      return orderTime;
    }

    public void setOrderTime(String orderTime) {
      this.orderTime = orderTime;
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

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }

    public String getProviderId() {
      return providerId;
    }

    public void setProviderId(String providerId) {
      this.providerId = providerId;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

  }
  }
