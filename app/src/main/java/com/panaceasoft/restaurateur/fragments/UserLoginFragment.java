package com.panaceasoft.restaurateur.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.MainActivity;
import com.panaceasoft.restaurateur.activities.UserForgotPasswordActivity;
import com.panaceasoft.restaurateur.activities.UserLoginActivity;
import com.panaceasoft.restaurateur.activities.UserRegisterActivity;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
//import com.stripe.android.compat.AsyncTask;
import android.os.AsyncTask;

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

/**
 * Created by Panacea-Soft on 8/1/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class UserLoginFragment extends Fragment {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */

    private View view;
    private EditText txtEmail;
    private EditText txtPassword;
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;
    private RelativeLayout mainLayout;

    CallbackManager callbackManager;

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
        view = inflater.inflate(R.layout.fragment_user_login, container, false);

        initData();

        initUI();

        setupHyperlink();

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

        txtEmail = this.view.findViewById(R.id.input_email);
        txtEmail.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));

        txtPassword           = this.view.findViewById(R.id.input_password);
        Button btnLogin       = this.view.findViewById(R.id.button_login);
        Button btnFbLogin     = this.view.findViewById(R.id.button_login_fb);
        TextView textForgot   = this.view.findViewById(R.id.text_forgot);
        TextView textRegister = this.view.findViewById(R.id.text_register);


        callbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = this.view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
//        loginButton.setReadPermissions("public_profile");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Utils.psLog("OnActivityResultFbSuceess"+loginResult.getAccessToken().getToken());
                doFbLogin(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                // App code
                Utils.psLog("OnActivityResultFbCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Utils.psLog("OnActivityResultFbError"+exception+" "+exception.toString()+" "+exception.getMessage());
            }
        });

        btnFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("here","here");
                doLogin();
            }
        });

        textForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("here","here1");
                doForgot();
            }
        });

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("here","here2");
                doRegister();
            }
        });

        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void doLogin() {

        if (inputValidation()) {

            final String URL = Config.APP_API_URL + Config.POST_USER_LOGIN;
            Utils.psLog(URL);

            HashMap<String, String> params = new HashMap<>();
            params.put("email", txtEmail.getText().toString().trim());
            params.put("password", txtPassword.getText().toString().trim());

            doSubmit(URL, params);

        }

    }

    private void doFbLogin(String fbToken) {

        final String URL = Config.APP_API_URL + Config.POST_FB_LOGIN;
        Utils.psLog(URL+" "+fbToken);

        HashMap<String, String> params = new HashMap<>();
        params.put("access_token", fbToken);

        doFbSubmit(URL, params);

    }

    private void doForgot() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).replaceFragment(new UserForgotPasswordFragment(), getResources().getString(R.string.forgot_pwd_title), true);
        } else if (getActivity() instanceof UserLoginActivity) {
            startActivity(new Intent(getActivity(), UserForgotPasswordActivity.class));
        }
    }

    private void doRegister() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).replaceFragment(new UserRegisterFragment(), getResources().getString(R.string.register_title), true);
        } else if (getActivity() instanceof UserLoginActivity) {
            startActivity(new Intent(getActivity(), UserRegisterActivity.class));
            //Intent intent = new Intent(getActivity(), UserRegisterActivity.class);
            //getActivity().start(intent, 0);
        }
    }

    private void doSubmit(String postURL, HashMap<String, String> params) {
        prgDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Utils.psLog(" .... Starting User Login Callback .... ");

                            String status = response.getString("status");
                            Utils.psLog("Response" + response);
                            if (status.equals(jsonStatusSuccessString)) {

                                JSONObject dat = response.getJSONObject("data");
                                String user_id = dat.getString("id");
                                String user_name = dat.getString("username");
                                String email = dat.getString("email");
                                String about_me = dat.getString("about_me");
                                //String is_banned = dat.getString("is_banned");
                                String user_profile_photo = dat.getString("profile_photo");
                                String user_phone = dat.getString("phone");
                                String user_delivery_address = dat.getString("delivery_address");
                                String user_billing_address = dat.getString("billing_address");

                                String user_allergy_info = dat.getString("allergy_info");

                                if (getActivity() != null) {
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt("_login_user_id", Integer.parseInt(user_id));
                                    editor.putString("_login_user_name", user_name);
                                    editor.putString("_login_user_email", email);
                                    editor.putString("_login_user_about_me", about_me);
                                    editor.putString("_login_user_photo", user_profile_photo);
                                    editor.putString("_login_user_phone", user_phone);
                                    editor.putString("_login_user_delivery_address", user_delivery_address);
                                    editor.putString("_login_user_billing_address", user_billing_address);

                                    if(user_allergy_info != null) {
                                        if(user_allergy_info.equalsIgnoreCase("null")) {
                                            editor.putString("_login_user_allergy", "");
                                        } else {
                                            editor.putString("_login_user_allergy", user_allergy_info);
                                        }
                                    } else {
                                        editor.putString("_login_user_allergy", "");
                                    }

                                    editor.apply();

                                    // Update Menu
                                    Utils.activity.bindMenu();

                                    Utils.psLog("User Profile Photo : " + user_profile_photo);
                                    if (!user_profile_photo.equals("")) {
                                        loadProfileImage(user_profile_photo);
                                        //prgDialog.cancel();
                                    } else {
                                        prgDialog.cancel();
                                        if (getActivity() instanceof MainActivity) {
                                            ((MainActivity) getActivity()).refreshProfile();
                                        }

                                        if (getActivity() instanceof UserLoginActivity) {
                                            getActivity().finish();
                                        }
                                    }
                                }
                            } else {
                                Utils.psLog("Login Fail");
                                prgDialog.cancel();
                                try {
                                    showFailPopup(response.getString("data"));
                                } catch (Exception e) {
                                    showFailPopup("");
                                }
                            }
                        } catch (JSONException e) {
                            prgDialog.cancel();
                            Utils.psLog("Login Fail : " + e.getMessage());
                            e.printStackTrace();
                            showFailPopup("");
                        } catch (Exception e) {
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

    private void loadProfileImage(String path) {
        if (!path.equals("")) {
            new downloadProfileImage().execute(Config.APP_IMAGES_URL + path, path);
        }
    }

    private boolean inputValidation() {

        if (getActivity() != null) {
            if (txtEmail.getText().toString().equals("")) {
//                Toast.makeText(getActivity().getApplicationContext(), R.string.email_validation_message,
//                        Toast.LENGTH_LONG).show();
                showFailPopup(getResources().getString(R.string.email_validation_message));
                return false;
            }

            if (txtPassword.getText().toString().equals("")) {
//                Toast.makeText(getActivity().getApplicationContext(), R.string.password_validation_message,
//                        Toast.LENGTH_LONG).show();
                showFailPopup(getResources().getString(R.string.password_validation_message));
                return false;
            }

            return true;
        } else {
            return false;
        }

    }

    private void doFbSubmit(String postURL, HashMap<String, String> params) {
        prgDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Utils.psLog(" .... Starting User Login Callback .... ");
                            Utils.psLog("Response" + response);
                            String status = response.getString("status");

                            if (status.equals(jsonStatusSuccessString)) {
                                JSONObject dat = response.getJSONObject("data");
                                String user_id = dat.getString("id");
                                String user_name = dat.getString("username");
                                String email = dat.getString("email");
                                String about_me = dat.getString("about_me");
                                //String is_banned = dat.getString("is_banned");
                                String user_profile_photo = dat.getString("profile_photo");
                                String user_phone = dat.getString("phone");
                                String user_delivery_address = dat.getString("delivery_address");
                                String user_billing_address = dat.getString("billing_address");

                                String user_allergy_info = dat.getString("allergy_info");

                                if (getActivity() != null) {
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt("_login_user_id", Integer.parseInt(user_id));
                                    editor.putString("_login_user_name", user_name);
                                    editor.putString("_login_user_email", email);
                                    editor.putString("_login_user_about_me", about_me);
                                    editor.putString("_login_user_photo", user_profile_photo);
                                    editor.putString("_login_user_phone", user_phone);
                                    editor.putString("_login_user_delivery_address", user_delivery_address);
                                    editor.putString("_login_user_billing_address", user_billing_address);

                                    if(user_allergy_info != null) {
                                        if(user_allergy_info.equalsIgnoreCase("null")) {
                                            editor.putString("_login_user_allergy", "");
                                        } else {
                                            editor.putString("_login_user_allergy", user_allergy_info);
                                        }
                                    } else {
                                        editor.putString("_login_user_allergy", "");
                                    }

                                    editor.apply();

                                    // Update Menu
                                    Utils.activity.bindMenu();

                                    Utils.psLog("User Profile Photo : " + user_profile_photo);
                                    if (!user_profile_photo.equals("")) {
                                        loadProfileImage(user_profile_photo);
                                        //prgDialog.cancel();
                                    } else {
                                        prgDialog.cancel();
                                        if (getActivity() instanceof MainActivity) {
                                            ((MainActivity) getActivity()).refreshProfile();
                                        }

                                        if (getActivity() instanceof UserLoginActivity) {
                                            getActivity().finish();
                                        }
                                    }
                                }
                            } else {
                                Utils.psLog("Login Fail");
                                prgDialog.cancel();
                                try {
                                    showFailPopup(response.getString("data"));
                                } catch (Exception e) {
                                    showFailPopup("");
                                }
                            }
                        } catch (JSONException e) {
                            prgDialog.cancel();
                            Utils.psLog("Login Fail : " + e.getMessage());
                            e.printStackTrace();
                            showFailPopup("");
                        } catch (Exception e) {
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
        if (getActivity() != null) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
//            builder.setTitle(R.string.login);
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
                textMsg.setText(getResources().getString(R.string.login_fail));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.psLog("OnActivityResultFbLogin");
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupHyperlink() {
        TextView linkTextView = view.findViewById(R.id.text_privacy_2);
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }


}
