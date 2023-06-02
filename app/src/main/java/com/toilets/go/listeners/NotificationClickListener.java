package com.toilets.go.listeners;

import com.toilets.go.models.SuccessResNotifications;
import com.toilets.go.models.SuccessResRequests;


public interface NotificationClickListener {
    void notificationCardClicked(SuccessResNotifications.Result f, String Status, Integer position);
}