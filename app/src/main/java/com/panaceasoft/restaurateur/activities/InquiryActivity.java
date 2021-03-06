package com.panaceasoft.restaurateur.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
import android.widget.Button;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;


public class InquiryActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtMessage;
    private SharedPreferences pref;
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;
    private SpannableString inquiry;
    private CoordinatorLayout mainLayout;
    TextView toolbarTitle;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry);

        initUI();

        initData();

        bindData();
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
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData() {
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            inquiry = Utils.getSpannableString(getApplicationContext(), getString(R.string.inquiry));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLog("Error in initData," ,e );
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI() {

        try {
            initToolbar();
            initProgressDialog();
            mainLayout = findViewById(R.id.coordinator_layout);
            mainLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtName = findViewById(R.id.input_name);
            txtName.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtEmail = findViewById(R.id.input_email);
            txtEmail.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtMessage = findViewById(R.id.input_message);
            txtMessage.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            Button btnSubmit = findViewById(R.id.button_submit);
            btnSubmit.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

            if(Config.SHOW_ADMOB) {
                AdView mAdView = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }else{
                AdView mAdView = findViewById(R.id.adView);
                mAdView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
        }
    }

    private void initToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if(getSupportActionBar() != null) {
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

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {
        toolbarTitle.setText(inquiry);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    public void doInquiry(View view) {
        if (inputValidation()) {

            final String URL = Config.APP_API_URL + Config.POST_ITEM_INQUIRY + GlobalData.itemData.id;
            Utils.psLog(URL);

            HashMap<String, String> params = new HashMap<>();
            params.put("name", txtName.getText().toString());
            params.put("email", txtEmail.getText().toString());
            params.put("message", txtMessage.getText().toString());
            params.put("shop_id", String.valueOf(pref.getInt("_id", 0)));
            doSubmit(URL, params);
        }
    }

    public void doSubmit(String URL, final HashMap<String, String> params) {
        prgDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                          //  pb.setVisibility(view.GONE);

                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {
                                Utils.psLog(status);

                                showSuccessPopup();

                            } else {
                                showFailPopup("");
                                Utils.psLog("Error in loading.");
                            }

                            prgDialog.cancel();

                        } catch (JSONException e) {
                            prgDialog.cancel();
                            e.printStackTrace();
                        } catch (Exception e){
                            prgDialog.cancel();
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    prgDialog.cancel();
                    Utils.psLog(error.getMessage());
                }catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }

            }
        });

        req.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }

    public void showSuccessPopup() {
//        AlertDialog.Builder builder =
//                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
//        builder.setTitle(R.string.inquiry);
//        builder.setMessage(R.string.inquiry_success);
//        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//                Utils.psLog("OK clicked.");
//            }
//        });
//        builder.show();

        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_success);
//            getActivity().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView textMsg  =  dialog.findViewById(R.id.text_msg);
        TextView textOk   =  dialog.findViewById(R.id.text_ok);

        dialog.show();

        textMsg.setText(getResources().getString(R.string.inquiry_success));

        textOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

    }

    public void showFailPopup(String msg) {
//        AlertDialog.Builder builder =
//                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
//        builder.setTitle(R.string.inquiry);
//        builder.setMessage(R.string.inquiry_fail);
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

    public boolean inputValidation() {

        if (txtName.getText().toString().equals("")) {
//            Toast.makeText(getApplicationContext(), R.string.name_validation_message,
//                    Toast.LENGTH_LONG).show();
            showFailPopup(getResources().getString(R.string.name_validation_message));
            return false;
        }

        if (txtEmail.getText().toString().equals("")) {
//            Toast.makeText(getApplicationContext(), R.string.email_validation_message,
//                    Toast.LENGTH_LONG).show();
            showFailPopup(getResources().getString(R.string.email_validation_message));
            return false;
        } else {
            if (!Utils.isEmailFormatValid(txtEmail.getText().toString())) {
//                Toast.makeText(getApplicationContext(), R.string.email_format_validation_message,
//                        Toast.LENGTH_LONG).show();
                showFailPopup(getResources().getString(R.string.email_format_validation_message));
                return false;
            }
        }

        if (txtMessage.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), R.string.inquiry_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/


}
