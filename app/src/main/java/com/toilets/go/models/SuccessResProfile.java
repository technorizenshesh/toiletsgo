package com.toilets.go.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public  class SuccessResProfile implements Serializable {
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

  public class Result implements Serializable
  {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("register_id")
    @Expose
    private String registerId;
    @SerializedName("social_id")
    @Expose
    private String socialId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("expired_at")
    @Expose
    private String expiredAt;
    @SerializedName("last_login")
    @Expose
    private String lastLogin;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("ios_register_id")
    @Expose
    private Object iosRegisterId;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("step")
    @Expose
    private String step;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("holder_name")
    @Expose
    private String holderName;
    @SerializedName("ifsc_code")
    @Expose
    private String ifscCode;
    @SerializedName("branch")
    @Expose
    private String branch;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    private final static long serialVersionUID = -8004714735225712728L;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

    public String getUserName() {
      return userName;
    }

    public void setUserName(String userName) {
      this.userName = userName;
    }

    public String getCountry() {
      return country;
    }

    public void setCountry(String country) {
      this.country = country;
    }

    public String getDob() {
      return dob;
    }

    public void setDob(String dob) {
      this.dob = dob;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getImage() {
      return image;
    }

    public void setImage(String image) {
      this.image = image;
    }

    public String getRegisterId() {
      return registerId;
    }

    public void setRegisterId(String registerId) {
      this.registerId = registerId;
    }

    public String getSocialId() {
      return socialId;
    }

    public void setSocialId(String socialId) {
      this.socialId = socialId;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    public String getExpiredAt() {
      return expiredAt;
    }

    public void setExpiredAt(String expiredAt) {
      this.expiredAt = expiredAt;
    }

    public String getLastLogin() {
      return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
      this.lastLogin = lastLogin;
    }

    public String getDateTime() {
      return dateTime;
    }

    public void setDateTime(String dateTime) {
      this.dateTime = dateTime;
    }

    public String getMobile() {
      return mobile;
    }

    public void setMobile(String mobile) {
      this.mobile = mobile;
    }

    public Object getIosRegisterId() {
      return iosRegisterId;
    }

    public void setIosRegisterId(Object iosRegisterId) {
      this.iosRegisterId = iosRegisterId;
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

    public String getCity() {
      return city;
    }

    public void setCity(String city) {
      this.city = city;
    }

    public String getStep() {
      return step;
    }

    public void setStep(String step) {
      this.step = step;
    }

    public String getGender() {
      return gender;
    }

    public void setGender(String gender) {
      this.gender = gender;
    }

    public String getBankName() {
      return bankName;
    }

    public void setBankName(String bankName) {
      this.bankName = bankName;
    }

    public String getHolderName() {
      return holderName;
    }

    public void setHolderName(String holderName) {
      this.holderName = holderName;
    }

    public String getIfscCode() {
      return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
      this.ifscCode = ifscCode;
    }

    public String getBranch() {
      return branch;
    }

    public void setBranch(String branch) {
      this.branch = branch;
    }

    public String getAccountNumber() {
      return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
      this.accountNumber = accountNumber;
    }

  }
}