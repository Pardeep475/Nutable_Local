package com.panaceasoft.restaurateur.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Panacea-Soft on 26/6/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class FriendListResponse {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("data")
    @Expose
    private FriendListResult data;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FriendListResult getData() {
        return data;
    }

    public void setData(FriendListResult data) {
        this.data = data;
    }
}
