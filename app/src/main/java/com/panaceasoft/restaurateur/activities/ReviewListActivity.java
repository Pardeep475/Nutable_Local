package com.panaceasoft.restaurateur.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.adapters.ReviewAdapter;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.Picasso;

public class ReviewListActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/


    private ListView listView;
    private Toolbar toolbar;
    private int selectedItemId;
    private int selectedCityId;
    private SharedPreferences pref;
    private SpannableString reviewListString;
    private Picasso p;

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
        setContentView(R.layout.activity_review_list);

        initData();

        initUI();

        bindData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){

            if(resultCode == RESULT_OK){
                bindData();
            }
        }
        Utils.psLog(" Result : " + requestCode + " : " + resultCode);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent();
        in.putExtra("close_activity", "NO");
        setResult(RESULT_OK, in);

        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    @Override
    public void onDestroy() {

        try {
            toolbar = null;
//            p.shutdown();

            super.onDestroy();
        }catch(Exception e){
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
        initFAB();
        initList();
    }

    private void initList() {
        try {
            listView = findViewById(R.id.review_list);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initList.", e);
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

            toolbarTitle.setText(reviewListString);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    private void initFAB() {
        try {
            FloatingActionButton fab = findViewById(R.id.fab_new_review);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pref.getInt("_login_user_id", 0) != 0) {
                        Intent intent = new Intent(getApplicationContext(), ReviewEntry.class);
                        intent.putExtra("selected_item_id", selectedItemId);
                        intent.putExtra("selected_shop_id", selectedCityId);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);

                    } else {
//                        Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
//                        startActivity(intent);
                        showNeedLogin();
                    }

                }
            });
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initFAB.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/


    private void initData() {
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            selectedItemId = getIntent().getIntExtra("selected_item_id", 0);
            selectedCityId = getIntent().getIntExtra("selected_shop_id", 0);
            reviewListString = Utils.getSpannableString(getApplicationContext(), getString(R.string.review_list));
            p = new Picasso.Builder(this).build();
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
            ReviewAdapter adapter = new ReviewAdapter(this, GlobalData.itemData.reviews, p);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/


    boolean isDialog = false;
    private void showNeedLogin() {
        try {

            if(!isDialog) {
                isDialog = true;
                final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.custom_dialog_error);
//                getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView textMsg = dialog.findViewById(R.id.text_msg);
                TextView textOk = dialog.findViewById(R.id.text_ok);

                dialog.show();

                textMsg.setText(getResources().getString(R.string.login_required));

                textOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isDialog = false;
                        dialog.dismiss();
                    }
                });
            }

        } catch (Exception e) {
            Utils.psErrorLog("showNeedLogin", e);
            isDialog = false;
        }
    }

}
