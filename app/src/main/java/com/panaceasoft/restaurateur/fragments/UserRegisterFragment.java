package com.panaceasoft.restaurateur.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.MainActivity;
import com.panaceasoft.restaurateur.activities.UserRegisterActivity;
import com.panaceasoft.restaurateur.activities.VerifyActivity;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by Panacea-Soft on 8/1/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class UserRegisterFragment extends Fragment {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private View view;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtPhone;
    private EditText txtAllergyInfo;
    private String userName;
    private String email;
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;
    private LinearLayout mainLayout;
    private TextView textLogin;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_register, container, false);

        initData();

        initUI();

        setupHyperlink();

        return view;
    }


    @Override
    public void onDestroy() {
        try {
            Utils.unbindDrawables(mainLayout);
            //GlobalData.shopdata = null;
            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **-----------------------------------------------------------------------------------------------*/

    private void initData() {
        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI() {

        mainLayout         = this.view.findViewById(R.id.nav_register);
        mainLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        txtName            = this.view.findViewById(R.id.input_name);
        txtEmail           = this.view.findViewById(R.id.input_email);
        txtPassword        = this.view.findViewById(R.id.input_password);
        txtPhone           = this.view.findViewById(R.id.input_phone);
        Button btnRegister = this.view.findViewById(R.id.button_register);
        textLogin          = this.view.findViewById(R.id.text_login);
        txtAllergyInfo     = this.view.findViewById(R.id.input_alergy);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });


        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCancel();
            }
        });

        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    private void doCancel() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).goBack();
        } else if (getActivity() instanceof UserRegisterActivity) {
            getActivity().finish();
        }
    }

    private boolean inputValidation() {

        if(getActivity() != null) {
            if (txtName.getText().toString().equals("")) {
//                Toast.makeText(getActivity().getApplicationContext(), R.string.name_validation_message,
//                        Toast.LENGTH_LONG).show();
                showFailPopup(getResources().getString(R.string.name_validation_message));
                return false;
            }

            if (txtEmail.getText().toString().equals("")) {
//                Toast.makeText(getActivity().getApplicationContext(), R.string.email_validation_message,
//                        Toast.LENGTH_LONG).show();
                showFailPopup(getResources().getString(R.string.email_validation_message));
                return false;
            }

            if (txtPhone.getText().toString().equals("")) {
//                Toast.makeText(getActivity().getApplicationContext(), R.string.phone_validation_message,
//                        Toast.LENGTH_LONG).show();
                showFailPopup(getResources().getString(R.string.phone_validation_message));
                return false;
            }

            if (txtPassword.getText().toString().equals("")) {
//                Toast.makeText(getActivity().getApplicationContext(), R.string.password_validation_message,
//                        Toast.LENGTH_LONG).show();
                showFailPopup(getResources().getString(R.string.password_validation_message));
                return false;
            }

            if (txtAllergyInfo.getText().toString().equals("")) {
//                Toast.makeText(getActivity().getApplicationContext(), R.string.email_validation_message,
//                        Toast.LENGTH_LONG).show();
                showFailPopup(getResources().getString(R.string.alergy_error));
                return false;
            }
            return true;
        }else {
            return false;
        }
    }

    private void doRegister() {

        if (inputValidation()) {

            final String URL = Config.APP_API_URL + Config.POST_USER_REGISTER;
            Utils.psLog(URL);

            userName = txtName.getText().toString().trim();
            email = txtEmail.getText().toString().trim();

            HashMap<String, String> params = new HashMap<>();
            params.put("username", txtName.getText().toString().trim());
            params.put("email",    txtEmail.getText().toString().trim());
            params.put("password", txtPassword.getText().toString().trim());
            params.put("phonenumber", txtPhone.getText().toString().trim());
            params.put("allergy_info", txtAllergyInfo.getText().toString().trim());
            params.put("about_me", "");
            doSubmit(URL, params);

        }
    }

    private void doSubmit(String postURL, HashMap<String, String> params) {
        prgDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String status = response.getString("status");
                            Utils.psLog(response.toString());
                            if (status.equals(jsonStatusSuccessString)) {

                                String user_id = response.getString("data");

                                if(getActivity() != null) {
                                    //Save Login User Info
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt("_login_user_id1", Integer.parseInt(user_id));
                                    editor.putString("_login_user_name1", userName);
                                    editor.putString("_login_user_email1", email);
                                    editor.putString("_login_user_phone1", txtPhone.getText().toString().trim());
                                    editor.putString("_login_user_allergy1", txtAllergyInfo.getText().toString().trim());
                                    editor.putString("_login_user_about_me1", "");
                                    editor.putString("_login_user_photo1", "default_user_profile.png");
                                    editor.apply();

                                    // Update Menu
//                                    Utils.activity.bindMenu();
                                    //Utils.activity.loadProfileImage("default_user_profile.png");
                                    // Show profile Menu
//                                    if (getActivity() instanceof MainActivity) {
//                                        ((MainActivity) getActivity()).replaceFragment(new ProfileFragment(), getResources().getString(R.string.profile), false);
//                                    }

                                    prgDialog.cancel();
                                    showSuccessPopup();
                                }

                            } else {
                                prgDialog.cancel();
                                Utils.psLog("Register Fail");
                                try {
                                    showFailPopup(response.getString("data"));
                                } catch (Exception e) {
                                    showFailPopup("");
                                }
                            }
                        } catch (JSONException e) {
                            prgDialog.cancel();
                            Utils.psLog("Register Fail");
                            showFailPopup("");
                        } catch (Exception e){
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    prgDialog.cancel();
                    Utils.psLog("Error: " + error.getMessage());
                }catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }
            }
        });

        req.setShouldCache(false);
        // add the request object to the queue to be executed
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(req);

    }

    private void showFailPopup(String msg) {
        if(getActivity() != null) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
//            builder.setTitle(R.string.register);
//            builder.setMessage(R.string.login_fail);
//            builder.setPositiveButton(R.string.OK, null);
//            builder.show();

            final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_error);
//            getActivity().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView textMsg  =  dialog.findViewById(R.id.text_msg);
            TextView textOk   =  dialog.findViewById(R.id.text_ok);

            dialog.show();

            if(msg.equalsIgnoreCase(""))
                textMsg.setText(getResources().getString(R.string.register_fail));
            else
                textMsg.setText(msg);

            textOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    private void showSuccessPopup() {
        if(getActivity() != null) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
//            builder.setTitle(R.string.register);
//            builder.setMessage(R.string.register_success);
//            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (getActivity() instanceof MainActivity) {
//                        getActivity().getSupportFragmentManager().popBackStackImmediate();
//                        ((MainActivity) getActivity()).replaceFragment(new VerifyFragment(), getResources().getString(R.string.verify_title), true);
//                    } else if (getActivity() instanceof UserRegisterActivity) {
//                        //getActivity().finish();
//                        startActivity(new Intent(getActivity(), VerifyActivity.class));
//                    }
//                    Utils.psLog("OK clicked.");
//                }
//            });
//            builder.show();


            final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_success);
//            getActivity().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView textMsg  =  dialog.findViewById(R.id.text_msg);
            TextView textOk   =  dialog.findViewById(R.id.text_ok);

            dialog.show();

            textMsg.setText(getResources().getString(R.string.register_success));

            textOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).replaceFragment(new VerifyFragment(), getResources().getString(R.string.verify_title), true);
                    } else if (getActivity() instanceof UserRegisterActivity) {
                        //getActivity().finish();
                        startActivity(new Intent(getActivity(), VerifyActivity.class));
                    }
                }
            });

        }

    }

    private void setupHyperlink() {
        TextView linkTextView = view.findViewById(R.id.text_privacy_2);
        TextView privacyText = view.findViewById(R.id.text_privacy_3);
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
}

