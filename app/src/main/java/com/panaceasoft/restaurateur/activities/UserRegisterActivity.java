package com.panaceasoft.restaurateur.activities;

import android.content.Intent;
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

public class UserRegisterActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private SpannableString registerString;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        initData();

        initToolbar();
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData() {
        try {
            registerString = Utils.getSpannableString(getApplicationContext(), getString(R.string.register_title));
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
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
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

            toolbarTitle.setText(registerString);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    public void successfullyRegisterd(){
        Intent in = new Intent();

        setResult(RESULT_OK, in);
        this.finish();
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Utils.psLog("OnActivityResult");
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    successfullyRegisterd();
                }
            }
        }catch (Exception e){
            Utils.psErrorLogE("Error in main.", e);
        }
    }


}
