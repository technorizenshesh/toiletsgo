package com.toilets.go.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SuccessResNotifications implements Serializable
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
        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("provider_name")
        @Expose
        private String providerName;
        @SerializedName("cart_details")
        @Expose
        private CartDetails cartDetails;
        private final static long serialVersionUID = 8045345857609395993L;

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

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getProviderName() {
            return providerName;
        }

        public void setProviderName(String providerName) {
            this.providerName = providerName;
        }

        public CartDetails getCartDetails() {
            return cartDetails;
        }

        public void setCartDetails(CartDetails cartDetails) {
            this.cartDetails = cartDetails;
        }
        public class CartDetails implements Serializable
        {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("user_id")
            @Expose
            private String userId;
            @SerializedName("shop_id")
            @Expose
            private String shopId;
            @SerializedName("item_id")
            @Expose
            private String itemId;
            @SerializedName("quantity")
            @Expose
            private String quantity;
            @SerializedName("item_amount")
            @Expose
            private String itemAmount;
            @SerializedName("date_time")
            @Expose
            private String dateTime;
            @SerializedName("status")
            @Expose
            private String status;
            @SerializedName("color")
            @Expose
            private String color;
            @SerializedName("size")
            @Expose
            private String size;
            @SerializedName("toilet_name")
            @Expose
            private String toiletName;
            @SerializedName("address")
            @Expose
            private String address;
            @SerializedName("image1")
            @Expose
            private String image1;
            @SerializedName("image2")
            @Expose
            private String image2;
            private final static long serialVersionUID = 534809182374706652L;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
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

            public String getShopId() {
                return shopId;
            }

            public void setShopId(String shopId) {
                this.shopId = shopId;
            }

            public String getItemId() {
                return itemId;
            }

            public void setItemId(String itemId) {
                this.itemId = itemId;
            }

            public String getQuantity() {
                return quantity;
            }

            public void setQuantity(String quantity) {
                this.quantity = quantity;
            }

            public String getItemAmount() {
                return itemAmount;
            }

            public void setItemAmount(String itemAmount) {
                this.itemAmount = itemAmount;
            }

            public String getDateTime() {
                return dateTime;
            }

            public void setDateTime(String dateTime) {
                this.dateTime = dateTime;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public String getToiletName() {
                return toiletName;
            }

            public void setToiletName(String toiletName) {
                this.toiletName = toiletName;
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

        }
    }
}