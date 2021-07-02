package com.panaceasoft.restaurateur.activities;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.listeners.GPSTracker;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class BaseActivity extends AppCompatActivity {

    public GPSTracker gpsTracker;
    public double _latitude = 0.0, _longitude = 0.0;
    private SharedPreferences pref;
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
    }

    public boolean getLocation() {
        gpsTracker = new GPSTracker(this,this);
        if (gpsTracker.canGetLocation()) {
            _latitude  = gpsTracker.getLatitude();
            _longitude = gpsTracker.getLongitude();
            return true;
        } else {
            gpsTracker.showSettingsAlert();
            return false;
        }
    }



    public float getDistance(double latitude, double longitude) {
        Location locationCurrent = new Location("Current Location");
        if(_latitude == 0.0) {
            _latitude = gpsTracker.getLatitude();
        }

        if(_longitude == 0.0) {
            _longitude = gpsTracker.getLongitude();
        }



        locationCurrent.setLatitude(_latitude);
        locationCurrent.setLongitude(_longitude);


        Location locationRestaurant = new Location("Restaurant Location");
        locationRestaurant.setLatitude(latitude);
        locationRestaurant.setLongitude(longitude);

        float distance = locationCurrent.distanceTo(locationRestaurant);
        return distance;
    }


    private void doLogout() {

        Log.e("heretimer", "heretimer");

        if(pref.getBoolean("_login_user_table_enter", false)) {
            double shopGetLat = 0.0, shopGetLng = 0.0;
            if(!pref.getString("shop_lat", "").equalsIgnoreCase("")) {
                shopGetLat = Double.valueOf(pref.getString("shop_lat", ""));
            }

            if(!pref.getString("shop_lng", "").equalsIgnoreCase("")) {
                shopGetLng = Double.valueOf(pref.getString("shop_lng", ""));
            }

            if(getDistance(shopGetLat, shopGetLng) > 10.0) {
                doEnterTableLogout();
            }
        }
    }


    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    private void startTimer(){

        try {
            stopTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        doLogout();
                    }
                });
            }
        };
        mTimer1.schedule(mTt1, 60000, 5000);
    }


    private void doEnterTableLogout() {
        final String URL = Config.APP_API_URL + Config.TABLE_LOGOUT;
        Utils.psLog(URL);

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id",  String.valueOf(pref.getInt("_login_user_id", 0)));
        params.put("shop_id",  pref.getString("shop_id", ""));
        params.put("table_id", pref.getString("table_id", ""));

        doTableSubmit(URL, params);
    }


    private void doTableSubmit(String postURL, HashMap<String, String> params) {

        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Utils.psLog(" .... Starting User Login Callback .... ");
                            String status = response.getString("status");
                            Utils.psLog("Response" + response);
                            if (status.equals("success")) {
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("shop_id", "");
                                editor.putString("table_id", "");
                                editor.putString("shop_lat", "");
                                editor.putString("shop_lng", "");
                                editor.putBoolean("_login_user_table_enter", false);
                                editor.apply();
                            } else {
                                Utils.psLog("Login Fail");
                            }
                        } catch (JSONException e) {
                            Utils.psLog("Login Fail : " + e.getMessage());
                            e.printStackTrace();
                        } catch (Exception e) {
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Utils.psLog("Error: " + error.getMessage());
                }catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }
            }
        });
        req.setShouldCache(false);
        // add the request object to the queue to be executed
        VolleySingleton.getInstance(BaseActivity.this).addToRequestQueue(req);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopTimer();
    }
}