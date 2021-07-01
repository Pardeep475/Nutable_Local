package com.panaceasoft.restaurateur.fragments;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.EditProfileActivity;
import com.panaceasoft.restaurateur.activities.PasswordUpdateActivity;
import com.panaceasoft.restaurateur.utilities.Utils;

import java.io.File;

/**
 * Created by Panacea-Soft on 8/1/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class ProfileFragment extends Fragment {

    private ImageView imgProfilePhoto;
    private EditText tvUserName;
    private EditText tvEmail;
    private EditText tvPhone;
    private EditText tvAboutMe;
    private EditText tvDeliveryAddress;
    private EditText tvBillingAddress;
    private EditText tvAllergy;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     * *------------------------------------------------------------------------------------------------
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(view);

        bindData();

        return view;
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void initUI(View view) {
        imgProfilePhoto = view.findViewById(R.id.iv_profile_photo);
        imgProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvUserName = view.findViewById(R.id.tv_name);
        tvUserName.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvPhone = view.findViewById(R.id.tv_phone);
        tvPhone.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvEmail = view.findViewById(R.id.tv_email);
        tvEmail.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvAboutMe = view.findViewById(R.id.tv_about_me);
        tvAboutMe.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvDeliveryAddress = view.findViewById(R.id.tv_delivery_address);
        tvDeliveryAddress.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvBillingAddress = view.findViewById(R.id.tv_billing_address);
        tvBillingAddress.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvAllergy = view.findViewById(R.id.tv_allergy);
        tvAllergy.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     * *------------------------------------------------------------------------------------------------
     */
    public void bindData() {
        try {

            if (getContext() != null) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

                tvUserName.setText(pref.getString("_login_user_name", ""));
                tvEmail.setText(pref.getString("_login_user_email", ""));
                tvPhone.setText(pref.getString("_login_user_phone", ""));


                if (pref.getString("_login_user_about_me", "").equals("")) {
                    tvAboutMe.setVisibility(View.GONE);
                } else {
                    tvAboutMe.setVisibility(View.VISIBLE);
                    tvAboutMe.setText(pref.getString("_login_user_about_me", ""));
                }

                if (pref.getString("_login_user_delivery_address", "").equals("")) {
                    tvDeliveryAddress.setVisibility(View.GONE);
                } else {
                    tvDeliveryAddress.setVisibility(View.VISIBLE);
                    tvDeliveryAddress.setText(pref.getString("_login_user_delivery_address", ""));
                }

                if (pref.getString("_login_user_billing_address", "").equals("")) {
                    tvBillingAddress.setVisibility(View.GONE);
                } else {
                    tvBillingAddress.setVisibility(View.VISIBLE);
                    tvBillingAddress.setText(pref.getString("_login_user_billing_address", ""));
                }

                if (pref.getString("_login_user_allergy", "").equals("")) {
                    tvAllergy.setVisibility(View.GONE);
                } else {
                    tvAllergy.setVisibility(View.VISIBLE);
                    tvAllergy.setText(pref.getString("_login_user_allergy", ""));
                }


                File file;

                ContextWrapper cw = new ContextWrapper(Utils.activity.getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                file = new File(directory, pref.getString("_login_user_photo", ""));
                //file = new File(Environment.getExternalStorageDirectory() + "/" + pref.getString("_login_user_photo", ""));
                if (file.exists()) {
                    Utils.psLog("File is exist");
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imgProfilePhoto.setImageBitmap(myBitmap);
                } else {
                    Drawable myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_person_black);
                    imgProfilePhoto.setImageDrawable(myDrawable);
                }

                Utils.psLog("Successfully loaded.");
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bind data.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            inflater.inflate(R.menu.menu_edit_profile, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }catch (Exception e) {
            Utils.psErrorLog("onCreateOptionsMenu", e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            final Intent intent;
            intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}





