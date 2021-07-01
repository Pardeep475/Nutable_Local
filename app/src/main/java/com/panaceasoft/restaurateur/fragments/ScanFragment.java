package com.panaceasoft.restaurateur.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.MainActivity;
import com.panaceasoft.restaurateur.activities.UserForgotPasswordActivity;
import com.panaceasoft.restaurateur.activities.UserLoginActivity;
import com.panaceasoft.restaurateur.activities.UserRegisterActivity;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class ScanFragment extends Fragment {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */

    private View view;
    private LinearLayout mainLayout;
    private Button btnScan;
    private ImageView imgScan, imgFruit;
    private TextView textManual;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     * *------------------------------------------------------------------------------------------------
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scan, container, false);

        initUI();

        return view;
    }

    @Override
    public void onDestroy() {
        try {
            Utils.unbindDrawables(mainLayout);
            //GlobalData.shopData = null;
            super.onDestroy();
        } catch (Exception e) {
            super.onDestroy();
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void initUI() {
        try {

            mainLayout = this.view.findViewById(R.id.main_layout);
            mainLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

            btnScan = this.view.findViewById(R.id.btn_scan);
            imgScan = this.view.findViewById(R.id.img_scan);
            imgFruit = this.view.findViewById(R.id.img_fruit);

            textManual  =  this.view.findViewById(R.id.text_manual);

            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doScan();
                }
            });

            textManual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doManual();
                }
            });

        } catch (Exception e) {
            Utils.psErrorLogE("Error in Init UI.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void doScan() {

    }

    private void doManual() {
        textManual.setEnabled(false);
        imgScan.setVisibility(View.GONE);
        imgFruit.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textManual.setEnabled(true);
                if(getActivity() != null) {
                    ((MainActivity) getActivity()).openShopList();
                }
            }
        }, 2000);
    }


    /**
     * ------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */

}
