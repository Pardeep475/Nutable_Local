package com.panaceasoft.restaurateur.activities;

import android.app.ProgressDialog;
import android.graphics.HardwareRenderer;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.adapters.FriendListAdapter;
import com.panaceasoft.restaurateur.models.FriendListData;
import com.panaceasoft.restaurateur.models.FriendListResponse;
import com.panaceasoft.restaurateur.models.FriendListResult;
import com.panaceasoft.restaurateur.models.PItemData;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SplitPaymentActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView mRecyclerFriendList;
    FriendListAdapter mAdapter;
    private HashMap<String, String> params = new HashMap<>();
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;
    private ArrayList<FriendListData> mListFriend=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_payment);
        initToolbar();
        initUI();
        initProgressDialog();
        bindData();
    }


    void initUI() {
        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in init data.", e);
        }
        mRecyclerFriendList = findViewById(R.id.recyclerSplitFriendList);
        mRecyclerFriendList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    void setAdapter(){
        mAdapter = new FriendListAdapter(mListFriend);
        mRecyclerFriendList.setAdapter(mAdapter);
    }

    private void initToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.back_icon, this.getTheme());
            toolbar.setNavigationIcon(drawable);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    private void initProgressDialog() {
        try {
            prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Please wait...");
            prgDialog.setCancelable(false);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initProgressDialog.", e);
        }
    }


    private void bindData() {
        try {

            final String URL = Config.APP_API_URL + Config.GET_FRIEND_LIST;
            Utils.psLog(URL);
            getFriendLIst( URL, new HashMap<String, String>());
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }
    }

    private void getFriendLIst(String postURL, HashMap<String, String> params) {
        prgDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            prgDialog.cancel();
                            Utils.psLog(" .... Starting Get User Callback .... ");

                            String status = response.getString("status");
                            Utils.psLog("Response" + response);
                            if (status.equals(jsonStatusSuccessString)) {
                                FriendListResult mResult;
                                Gson gson = new Gson();
                                mResult = gson.fromJson(response.getString("data"), FriendListResult.class);
                                mListFriend.addAll(mResult.getResult());
                                setAdapter();
                            } else {
                                Utils.psLog("Get Friend List");
                            }
                        } catch (JSONException e) {
                            prgDialog.cancel();
                            Utils.psLog("Get Friend List : " + e.getMessage());
                            e.printStackTrace();
                        } catch (Exception e) {
                            prgDialog.cancel();
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    prgDialog.cancel();
                    Utils.psLog("Error: " + error.getMessage());
                } catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }
            }
        });
        req.setShouldCache(false);
        // add the request object to the queue to be executed
        VolleySingleton.getInstance(SplitPaymentActivity.this).addToRequestQueue(req);

    }

}