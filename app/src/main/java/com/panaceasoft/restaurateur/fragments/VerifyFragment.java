package com.panaceasoft.restaurateur.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.MainActivity;
import com.panaceasoft.restaurateur.activities.UserLoginActivity;
import com.panaceasoft.restaurateur.activities.UserRegisterActivity;
import com.panaceasoft.restaurateur.activities.VerifyActivity;
import com.panaceasoft.restaurateur.utilities.PinEntryEditText;
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


public class VerifyFragment extends Fragment {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */

    private View view;
    PinEntryEditText txtPinEntry;
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;
    private RelativeLayout mainLayout;
    private TextView textResend;

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
        view = inflater.inflate(R.layout.fragment_pin, container, false);

        initData();

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
     * Start Block - Init Data Functions
     * *------------------------------------------------------------------------------------------------
     */

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


    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void initUI() {

        mainLayout = this.view.findViewById(R.id.main_layout);
        mainLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        Button btnVerify  = this.view.findViewById(R.id.button_verify);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("here","here");
                doVerify();
            }
        });

        txtPinEntry = this.view.findViewById(R.id.txt_pin_entry);
        txtPinEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 6) {
                    doVerify();
                }
            }
        });

        textResend = this.view.findViewById(R.id.text_send_code);
        textResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSendCode();
            }
        });

        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        doSendCode();
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void doVerify() {
        if (inputValidation()) {
            final String URL = Config.APP_API_URL + Config.POST_USER_VERIFY;
            Utils.psLog(URL);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            HashMap<String, String> params = new HashMap<>();
            params.put("phone_number",   prefs.getString("_login_user_phone1", ""));
            params.put("country_code",   "+91");
            params.put("verification_code", txtPinEntry.getText().toString().trim());
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

                                if(getActivity() != null) {
                                    //Save Login User Info
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt("_login_user_id", prefs.getInt("_login_user_id1", 0));
                                    editor.putString("_login_user_name", prefs.getString("_login_user_name1", ""));
                                    editor.putString("_login_user_email", prefs.getString("_login_user_email1", ""));
                                    editor.putString("_login_user_phone", prefs.getString("_login_user_phone1", ""));
                                    editor.putString("_login_user_about_me", prefs.getString("_login_user_about_me1", ""));
                                    editor.putString("_login_user_photo", prefs.getString("_login_user_photo1", ""));
                                    editor.putString("_login_user_allergy", prefs.getString("_login_user_allergy1", ""));
                                    editor.apply();

                                    prgDialog.cancel();

                                    editor.putInt("_login_user_id1", 0);
                                    editor.putString("_login_user_name1", "");
                                    editor.putString("_login_user_email1", "");
                                    editor.putString("_login_user_phone1", "");
                                    editor.putString("_login_user_about_me1", "");
                                    editor.putString("_login_user_photo1", "");
                                    editor.putString("_login_user_allergy1", "");
                                    editor.apply();

                                    Utils.activity.bindMenu();

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


    private void doSendCode() {

        final String URL = Config.APP_API_URL + Config.POST_USER_RESEND;
        Utils.psLog(URL);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        Utils.psLog(prefs.getString("_login_user_phone1", ""));

        HashMap<String, String> params = new HashMap<>();
        params.put("phone_number",   prefs.getString("_login_user_phone1", ""));
        params.put("country_code",   "+91");
        params.put("type", "sms");

        prgDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String status = response.getString("status");
                            Utils.psLog(response.toString());
                            if (status.equals(jsonStatusSuccessString)) {

                                if(getActivity() != null) {
                                    //Save Login User Info
                                    prgDialog.cancel();
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


    private void goBack() {
        ((MainActivity) getActivity()).replaceFragment(new ScanFragment(), "", false);
//        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.content_frame);
//        if (fragment != null) {
//            if (!(fragment instanceof ScanFragment)) {
//                getActivity().getSupportFragmentManager().popBackStackImmediate();
//                goBack();
//            }
//        }
    }

    private boolean inputValidation() {

        if (getActivity() != null) {
            if (txtPinEntry.getText().toString().length() < 6) {
//                Toast.makeText(getActivity().getApplicationContext(), R.string.pin_enter_error,
//                        Toast.LENGTH_LONG).show();
                showFailPopup(getResources().getString(R.string.pin_enter_error));
                return false;
            }

            return true;
        } else {
            return false;
        }

    }

    private void showSuccessPopup() {
        if(getActivity() != null) {
//            AlertDialog.Builder builder =new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
//            builder.setTitle(R.string.verify);
//            builder.setMessage(R.string.verify_success);
//            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (getActivity() instanceof MainActivity) {
//                        goBack();
//                    } else if (getActivity() instanceof VerifyActivity) {
//                        //getActivity().finish();
//                       ((VerifyActivity) getActivity()).successfullyVerified();
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

            textMsg.setText(getResources().getString(R.string.verify_success));

            textOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (getActivity() instanceof MainActivity) {
                        goBack();
                    } else if (getActivity() instanceof VerifyActivity) {
                        //getActivity().finish();
                        ((VerifyActivity) getActivity()).successfullyVerified();
                    }
                }
            });
        }

    }

    private void showFailPopup(String msg) {
        if (getActivity() != null) {
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
                textMsg.setText(getResources().getString(R.string.verification_failed));
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

    /**
     * ------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */


    @SuppressLint("StaticFieldLeak")
    private class downloadProfileImage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            URL url;
            try {
                url = new URL(params[0]);

                URLConnection conection = url.openConnection();
                conection.connect();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                if (getActivity() != null) {
                    File file;
                    ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                    File directory = cw.getDir("imageDir", Context.MODE_APPEND);
                    file = new File(directory, params[1]);

                    OutputStream output = new FileOutputStream(file.getAbsolutePath());

                    byte data[] = new byte[1024];

                    int count;
                    while ((count = input.read(data)) != -1) {

                        // writing data to file
                        output.write(data, 0, count);
                    }

                }

            } catch (Exception ee) {
                ee.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            // After download finished the profile image
            // shutdown the Picasso threads
            Utils.activity.showDownPicasso();

            prgDialog.cancel();
            if (getActivity() instanceof UserLoginActivity) {
                getActivity().finish();
            } else {
                Utils.activity.refreshProfile();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
