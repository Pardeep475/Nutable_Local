package com.panaceasoft.restaurateur.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 26/6/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class FriendListResult {
    @SerializedName("count")
    @Expose
    private String count;

    @SerializedName("result")
    @Expose
    private ArrayList<FriendListData> result=new ArrayList<>();

    public ArrayList<FriendListData> getResult() {
        return result;
    }

    public void setResult(ArrayList<FriendListData> result) {
        this.result = result;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
