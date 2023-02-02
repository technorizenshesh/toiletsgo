package com.toilets.go.utills;

import com.toilets.go.models.SuccessResNearbyList;
import com.toilets.go.models.SuccessResRequests;

public interface CustomClickListener {
    void cardClicked(SuccessResRequests.Result f, String Status,Integer position);
}