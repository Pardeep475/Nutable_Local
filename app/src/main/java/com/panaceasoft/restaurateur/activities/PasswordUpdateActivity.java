package com.panaceasoft.restaurateur.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class PasswordUpdateActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private EditText etNewPassword;
    private EditText etConfirmNewPassword;
    private int userId;
    private SharedPreferences pref;
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;
    private SpannableString passwordChangeString;
    private CoordinatorLayout mainLayout;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_update);

        initData();

        initUI();

        bindData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.menu_password_update, menu);
        }catch (Exception e) {
            Utils.psErrorLog("onCreateOptionsMenu", e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return  id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    @Override
    public void onDestroy() {

        try {
            toolbar = null;
            pref = null;
            prgDialog.cancel();
            prgDialog = null;

            Utils.unbindDrawables(mainLayout);
            mainLayout = null;

            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/


    private void initUI() {
        initToolbar();
        initProgressDialog();
        mainLayout = findViewById(R.id.coordinator_layout);
        etNewPassword = findViewById(R.id.input_password);
        etConfirmNewPassword = findViewById(R.id.input_password_confirm);
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

    private void initToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            TextView toolbarTitle  = toolbar.findViewById(R.id.toolbar_title);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if(getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }

            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.back_icon, this.getTheme());
            toolbar.setNavigationIcon(drawable);

            toolbarTitle.setText(passwordChangeString);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData(){
        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            passwordChangeString = Utils.getSpannableString(getApplicationContext(), getString(R.string.password_change));
        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
            userId = pref.getInt("_login_user_id", 0);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*///-------------------------------------------------------------------------------------------------------------------------------------

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
    private void doSubmit(String postURL, HashMap<String, String> params) {
        prgDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            Utils.psLog("Return Status." + status);
                            if (status.equals(jsonStatusSuccessString)) {

                                prgDialog.cancel();

                                showSuccessMessage(response.getString("data"));

                            } else {
                                prgDialog.cancel();
                                Utils.psLog("Error in loading.");
                            }

                        } catch (JSONException e) {
                            prgDialog.cancel();
                            showFailPopup("");
                            e.printStackTrace();
                        } catch (Exception e){
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    prgDialog.cancel();
                    VolleyLog.e("Error: ", error.getMessage());
                }catch (Exception e) {
                    prgDialog.cancel();
                    Utils.psErrorLog("onErrorResponse", e);
                }
            }
        });

        req.setShouldCache(false);
        // add the request object to the queue to be executed
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }

    private boolean inputValidation() {

        if (etNewPassword.getText().toString().trim().equals("")) {
//            Toast.makeText(getApplicationContext(), R.string.password_validation_message,
//                    Toast.LENGTH_LONG).show();
            showFailPopup(getResources().getString(R.string.password_validation_message));
            return false;
        }

        if (etConfirmNewPassword.getText().toString().trim().equals("")) {
//            Toast.makeText(getApplicationContext(), R.string.password_validation_message,
//                    Toast.LENGTH_LONG).show();
            showFailPopup(getResources().getString(R.string.password_validation_message));
            return false;
        }

        if (!etNewPassword.getText().toString().trim().equals(etConfirmNewPassword.getText().toString().trim())) {
//            Toast.makeText(getApplicationContext(), R.string.password_not_equal,
//                    Toast.LENGTH_LONG).show();
            showFailPopup(getResources().getString(R.string.password_not_equal));
            return false;
        }
        return true;
    }

    private void showSuccessMessage(String success_status) {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_success);
//            getActivity().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView textMsg  =  dialog.findViewById(R.id.text_msg);
        TextView textOk   =  dialog.findViewById(R.id.text_ok);

        dialog.show();

        textMsg.setText(success_status);

        textOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();
            }
        });
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    public void doUpdate(View view) {
        if (inputValidation()) {
            final String URL = Config.APP_API_URL + Config.PUT_USER_UPDATE + userId;
            Utils.psLog(URL);

            HashMap<String, String> params = new HashMap<>();
            params.put("platformName", "android");
            params.put("password", etNewPassword.getText().toString().trim());
            doSubmit(URL, params);
        }
    }

    public void showFailPopup(String msg) {
//        AlertDialog.Builder builder =
//                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
//        builder.setTitle(R.string.register);
//        builder.setMessage(R.string.profile_edit_fail);
//        builder.setPositiveButton(R.string.OK, null);
//        builder.show();

        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_error);
//            getActivity().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView textMsg  =  dialog.findViewById(R.id.text_msg);
        TextView textOk   =  dialog.findViewById(R.id.text_ok);

        dialog.show();

        if(msg.equalsIgnoreCase(""))
            textMsg.setText(getResources().getString(R.string.profile_edit_fail));
        else
            textMsg.setText(msg);

        textOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

}
