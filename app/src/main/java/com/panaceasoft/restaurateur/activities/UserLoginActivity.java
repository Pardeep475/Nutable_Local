package com.panaceasoft.restaurateur.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.utilities.Utils;

public class UserLoginActivity extends AppCompatActivity {

    private SpannableString loginString;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        initData();
        initToolbar();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData() {
        try {
            loginString = Utils.getSpannableString(getApplicationContext(), getString(R.string.login_title));
        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initToolbar() {
        /*------------------------------------------------------------------------------------------------
      Start Block - Private Variables
     ------------------------------------------------------------------------------------------------*/
        Toolbar toolbar        = findViewById(R.id.toolbar);
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

        toolbarTitle.setText(loginString);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/


}