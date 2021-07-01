package com.panaceasoft.restaurateur.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.fragments.AboutFragment;
import com.panaceasoft.restaurateur.fragments.FavouritesListFragment;
import com.panaceasoft.restaurateur.fragments.NotificationFragment;
import com.panaceasoft.restaurateur.fragments.ProfileFragment;
import com.panaceasoft.restaurateur.fragments.ReservationListFragment;
import com.panaceasoft.restaurateur.fragments.ScanFragment;
import com.panaceasoft.restaurateur.fragments.SearchFragment;
import com.panaceasoft.restaurateur.fragments.ShopsListFragment;
import com.panaceasoft.restaurateur.fragments.ShopsMapFragment;
import com.panaceasoft.restaurateur.fragments.TransactionFragment;
import com.panaceasoft.restaurateur.fragments.UserForgotPasswordFragment;
import com.panaceasoft.restaurateur.fragments.UserLoginFragment;
import com.panaceasoft.restaurateur.fragments.UserRegisterFragment;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;


/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private & Public Variables
     **------------------------------------------------------------------------------------------------*/
    private Toolbar toolbar = null;
    private ActionBarDrawerToggle drawerToggle = null;
    private DrawerLayout drawerLayout = null;
    private NavigationView navigationView = null;
    private int currentMenuId = 0;
    private FABActions fabActions;
    private SharedPreferences pref;
    private FloatingActionButton fab;
    private SpannableString appNameString;
    private SpannableString profileString;
    private SpannableString registerString;
    private SpannableString forgotPasswordString;
    private SpannableString searchKeywordString;
    private SpannableString favouriteItemString;
    private SpannableString reservationString;
    //private Picasso p;
    public Fragment fragment = null;
    private SpannableString aboutString;
    private SpannableString notiSettingString;
    private SpannableString transactionString;

    private boolean mToolBarNavigationListenerIsRegistered = false;

    FrameLayout layContent;
    TextView toolbarTitle;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private & PublicVariables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUtils();

        initUI();

        initData();

        bindData();

        FirebaseMessaging.getInstance().subscribeToTopic("restaurateur");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Utils.psLog("OnActivityResult");
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    refreshProfileData();

                    if(data.getStringExtra("close_activity").equals("YES")){
                        finish();
                    }
                }
            } else if (requestCode == 0) { // for refresh favourite list
                fragment.onActivityResult(requestCode, resultCode, data);
            } else {
                Utils.psLog("OnActivityResult1");
                Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (fragment1 != null) {
                    if (fragment1 instanceof UserLoginFragment) {
                        Utils.psLog("OnActivityResultFb");
                        fragment1.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
        }catch (Exception e){
            Utils.psErrorLogE("Error in main.", e);
        }

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            new AlertDialog.Builder(this)
//                    .setTitle(getResources().getString(R.string.app_name))
//                    .setMessage("Do you really want to quit?")
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            finish();
//                            System.exit(0);
//                        }})
//                    .setNegativeButton(android.R.string.no, null).show();
//        }
//        return true;
//    }


    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Utils Class
     **------------------------------------------------------------------------------------------------*/

    private void initUtils() {
        new Utils(this);
        VolleySingleton.getInstance(this);
        FirebaseAnalytics.getInstance(this);

        MobileAds.initialize(this, getResources().getString(R.string.banner_ad_unit_id));
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Utils Class
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI(){
        initToolbar();
        initDrawerLayout();
        initNavigationView();
        initFAB();
    }

    private void initToolbar() {
        toolbar      = findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);

        toolbar.setTitle("");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initDrawerLayout() {

        layContent  =  findViewById(R.id.content_lay);
        drawerLayout = findViewById(R.id.drawer_layout);

        if (drawerLayout != null && toolbar != null) {
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                    float slideX = drawerView.getWidth() * slideOffset;
                    Log.e("offset","offset"+slideOffset+" "+slideX);
                    layContent.setTranslationX(slideX);
                }
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            };

            drawerLayout.addDrawerListener(drawerToggle);
            drawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    drawerToggle.syncState();
                }
            });

        }
    }

    LinearLayout layLogin;
    Button navLogout;
    TextView navLogin;
    private void initNavigationView() {

        navigationView                = findViewById(R.id.nav_view);
        layLogin                      = navigationView.findViewById(R.id.lay_login);
        TextView navHome              = navigationView.findViewById(R.id.nav_home);
        TextView navSearch            = navigationView.findViewById(R.id.nav_search);
        navLogin                      = navigationView.findViewById(R.id.nav_login);
        TextView navReservation       = navigationView.findViewById(R.id.nav_reservation);
        TextView navProfile           = navigationView.findViewById(R.id.nav_profile);
        TextView navSavedCard         = navigationView.findViewById(R.id.nav_saved_card);
        TextView navFavItem           = navigationView.findViewById(R.id.nav_fav);
        TextView navTransHistory      = navigationView.findViewById(R.id.nav_transaction_history);
        TextView navReserveHistory    = navigationView.findViewById(R.id.nav_reserve_history);
        navLogout                     = navigationView.findViewById(R.id.button_logout);


        navReservation.setVisibility(View.GONE);
        navReserveHistory.setVisibility(View.GONE);
        navFavItem.setVisibility(View.GONE);

        navHome.setOnClickListener(this);
        navSearch.setOnClickListener(this);
        navLogin.setOnClickListener(this);
        navReservation.setOnClickListener(this);
        navProfile.setOnClickListener(this);
        navSavedCard.setOnClickListener(this);
        navFavItem.setOnClickListener(this);
        navTransHistory.setOnClickListener(this);
        navReserveHistory.setOnClickListener(this);
        navLogout.setOnClickListener(this);

    }

    private void initFAB() {
        fab = findViewById(R.id.fab);
        disableFAB();
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClicked(view);
            }
        });
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData(){
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        }catch(Exception e){
            Utils.psErrorLogE("Error in getting notification flag data.", e);
        }

        replaceFragment(new ScanFragment(), "", false);

        try {
            appNameString = Utils.getSpannableString(getApplicationContext(), getString(R.string.app_name));
            profileString = Utils.getSpannableString(getApplicationContext(), getString(R.string.profile));
            registerString = Utils.getSpannableString(getApplicationContext(), getString(R.string.register_title));
            forgotPasswordString = Utils.getSpannableString(getApplicationContext(), getString(R.string.forgot_pwd_title));
            searchKeywordString = Utils.getSpannableString(getApplicationContext(), getString(R.string.search_keyword));
            favouriteItemString = Utils.getSpannableString(getApplicationContext(), getString(R.string.favourite_item));
            reservationString = Utils.getSpannableString(getApplicationContext(), getString(R.string.my_reservation));
            aboutString = Utils.getSpannableString(getApplicationContext(), getString(R.string.about_app));
            notiSettingString = Utils.getSpannableString(this, getString(R.string.push_noti_setting));
            transactionString = Utils.getSpannableString(this, getString(R.string.transaction));

        }catch(Exception e){
            Utils.psErrorLogE("Error in init Data.", e);
        }

    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------f
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {

        bindMenu();

    }

    // This function will change the menu based on the user is logged in or not.
    public void bindMenu() {
        if (pref.getInt("_login_user_id", 0) != 0) {
            navLogin.setVisibility(View.GONE);
            layLogin.setVisibility(View.VISIBLE);
        } else {
            navLogin.setVisibility(View.VISIBLE);
            layLogin.setVisibility(View.GONE);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/


    @Override
    public void onClick(View v) {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        switch (v.getId()) {
            case R.id.nav_home:
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (fragment != null) {
                    if(!(fragment instanceof ScanFragment)) {
                        replaceFragment(new ScanFragment(), "", false);
                    }
                }

                break;
            case R.id.nav_search:
                replaceFragment(new SearchFragment(), getResources().getString(R.string.search_keyword_title), false);
                break;
            case R.id.nav_login:
                replaceFragment(new UserLoginFragment(), getResources().getString(R.string.login_title), true);
                break;
            case R.id.nav_reservation:
                replaceFragment(new ReservationListFragment(), getResources().getString(R.string.reservation), true);
                break;
            case R.id.nav_profile:
                replaceFragment(new ProfileFragment(), getResources().getString(R.string.profile), false);
                break;
            case R.id.nav_saved_card:
                break;
            case R.id.nav_fav:
                replaceFragment(new FavouritesListFragment(), getResources().getString(R.string.fav_item), true);
                break;
            case R.id.nav_transaction_history:
                replaceFragment(new TransactionFragment(), getResources().getString(R.string.transaction), true);
                break;
            case R.id.nav_reserve_history:
                break;
            case R.id.button_logout:
                doLogout();
                break;
        }
    }

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    private void disableFAB() {
        fab.setVisibility(View.GONE);
    }

    private void enableFAB() {
        fab.setVisibility(View.VISIBLE);
    }

    private void updateFABIcon(int icon) {
        fab.setImageResource(icon);
    }

    public void updateFABAction(FABActions action) {
        fabActions = action;
    }

    private void navigationMenuChanged(MenuItem menuItem) {
        try {
            openFragment(menuItem.getItemId());
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
        }catch (Exception e) {
            Utils.psErrorLog("navigationMenuChanged", e);
        }
    }

    private void updateFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment, String title,  boolean enable) {

        enableViews(enable);
        toolbarTitle.setText(title);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.content_frame, fragment);
        if(!title.equalsIgnoreCase(""))
            transaction.addToBackStack(title);
        transaction.commit();
    }

    public void changeToolbar(String title,  boolean enable) {

        enableViews(enable);
        toolbarTitle.setText(title);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment != null) {
                goBack();
            } else {
                super.onBackPressed();
            }
        }

    }

    public void goBack() {

        Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(fragment1 instanceof ProfileFragment) {
            return;
        }

        getSupportFragmentManager().popBackStackImmediate();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            if(fragment instanceof ScanFragment) {
                changeToolbar("", false);
            } else if(fragment instanceof UserLoginFragment) {
                changeToolbar(getResources().getString(R.string.login), true);
            } else if(fragment instanceof UserRegisterFragment) {
                changeToolbar(getResources().getString(R.string.register), true);
            } else if(fragment instanceof UserForgotPasswordFragment) {
                changeToolbar(getResources().getString(R.string.forgot_password_title), true);
            } else if(fragment instanceof ProfileFragment) {
                changeToolbar(getResources().getString(R.string.profile), false);
            } else if(fragment instanceof ShopsListFragment) {
                changeToolbar("", false);
            } else if(fragment instanceof SearchFragment) {
                changeToolbar(getResources().getString(R.string.search_keyword_title), false);
            }
        }
    }

    private void enableViews(boolean enable) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if(enable) {
//You may not want to open the drawer on swipe from the left in this case
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
// Remove hamburger
            drawerToggle.setDrawerIndicatorEnabled(false);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.back_icon, this.getTheme());
            drawerToggle.setHomeAsUpIndicator(drawable);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if(!mToolBarNavigationListenerIsRegistered) {

                drawerToggle.setToolbarNavigationClickListener(null);

                drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't have to be onBackPressed
//                        onBackPressed();
//                        changeToolbarTitle(getResources().getString(R.string.title_home));
                        goBack();
                    }
                });
                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
//You must regain the power of swipe for the drawer.
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
// Remove back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Show hamburger
//            toggle.setDrawerIndicatorEnabled(true);
            drawerToggle.setDrawerIndicatorEnabled(false);
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu_icon, this.getTheme());
            drawerToggle.setHomeAsUpIndicator(drawable);

            drawerToggle.setToolbarNavigationClickListener(null);

            drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                        drawerLayout.closeDrawer(Gravity.START);
                    } else {
                        drawerLayout.openDrawer(Gravity.START);
                    }
                }
            });

            // Remove the/any drawer toggle listener
//            toggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }

        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }



    private void doLogout() {
        pref.edit().remove("_login_user_id").apply();
        pref.edit().remove("_login_user_name").apply();
        pref.edit().remove("_login_user_email").apply();
        pref.edit().remove("_login_user_about_me").apply();
        pref.edit().remove("_login_user_photo").apply();
        pref.edit().remove("_login_user_phone").apply();
        pref.edit().remove("_login_user_billing_address").apply();
        pref.edit().remove("_login_user_delivery_address").apply();
        bindMenu();

        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            e.printStackTrace();
        }

        replaceFragment(new ScanFragment(), "", false);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    public void fabClicked(View view) {
        if (fabActions == FABActions.PROFILE) {
            final Intent intent;
            intent = new Intent(this, EditProfileActivity.class);
            startActivityForResult(intent, 1);
        }else if(fabActions == FABActions.SHOPLIST) {
            if(fragment instanceof ShopsListFragment) {
                ((ShopsListFragment)fragment).showSearchPopup();
            }
        }else if(fabActions == FABActions.SHOPMAP) {
            if(fragment instanceof ShopsMapFragment) {
                ((ShopsMapFragment)fragment).showFavPopup();
            }
        }
    }

    public void openShopList() {
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean notiFlag = getIntent().getBooleanExtra("show_noti", false);
            if (notiFlag) {
                savePushMessage(getIntent().getStringExtra("msg"));
                replaceFragment(new NotificationFragment(), getResources().getString(R.string.noption_title), true);
            } else {
                replaceFragment(new ShopsListFragment(), "", false);
            }
        }catch(Exception e){
            Utils.psErrorLogE("Error in getting notification flag data.", e);
        }
    }

    public void openFragment(int menuId) {

        switch (menuId) {
            case R.id.nav_home:
            case R.id.nav_home_login:
                //disableFAB();
                enableFAB();
                updateFABIcon(R.drawable.ic_search);

                String mapKey = "map";
                if (Config.START_MODE.equals(mapKey)) {
                    fragment = new ShopsMapFragment();
                    updateFABAction(FABActions.SHOPMAP);
                } else {
                    fragment = new ShopsListFragment();
                    updateFABAction(FABActions.SHOPLIST);
                }
//                toolbar.setTitle(appNameString);

                break;
            case R.id.nav_home_map_login:
            case R.id.nav_home_map:
                enableFAB();
                updateFABIcon(R.drawable.ic_search);
                fragment = new ShopsMapFragment();
                updateFABAction(FABActions.SHOPMAP);
                break;

            case R.id.nav_register:
                fragment = new UserRegisterFragment();
//                toolbar.setTitle(registerString);
                break;

            case R.id.nav_forgot:
                fragment = new UserForgotPasswordFragment();
                toolbar.setTitle(forgotPasswordString);
                break;

            case R.id.nav_logout:
                doLogout();
                break;

            case R.id.nav_search_keyword:
            case R.id.nav_search_keyword_login:
                disableFAB();
                fragment = new SearchFragment();
//                toolbar.setTitle(searchKeywordString);
                break;

            case R.id.nav_push_noti:
            case R.id.nav_push_noti_login:
                disableFAB();
                fragment = new NotificationFragment();
//                toolbar.setTitle(notiSettingString);
                break;

            case R.id.nav_transaction:
                disableFAB();
                fragment = new TransactionFragment();
//                toolbar.setTitle(transactionString);
                break;

            case R.id.nav_favourite_item_login:
                disableFAB();
                fragment = new FavouritesListFragment();
//                toolbar.setTitle(favouriteItemString);
                break;

            case R.id.nav_my_reservation_login:
                disableFAB();
                fragment = new ReservationListFragment();
                Utils.psLog("Reservation Click");
//                toolbar.setTitle(reservationString);
                break;

            case R.id.nav_about:
            case R.id.nav_about_login:
                disableFAB();
                fragment = new AboutFragment();
//                toolbar.setTitle(aboutString);
                break;


            default:
                break;
        }

        if (currentMenuId != menuId && menuId != R.id.nav_logout) {
            currentMenuId = menuId;

            updateFragment(fragment);

            try {
                navigationView.getMenu().findItem(menuId).setChecked(true);
            } catch (Exception e) {
                Utils.psErrorLog("Error in find menu item. ", e);
            }
        }


    }

    // Neet to check
    public void refreshProfileData() {

        if (fragment instanceof ProfileFragment) {

            Utils.psLog("Refresh Data.");
            ((ProfileFragment) fragment).bindData();
        }
    }

    public void refreshProfile(){
//        openFragment(R.id.nav_profile_login);
        getSupportFragmentManager().popBackStackImmediate();

        replaceFragment(new ProfileFragment(), getResources().getString(R.string.profile), false);
    }

    public void refreshNotification(){
        try {
            fragment = new NotificationFragment();

            updateFragment(fragment);
            if (pref.getInt("_login_user_id", 0) != 0) {
                currentMenuId = R.id.nav_push_noti_login;
            }else{
                currentMenuId = R.id.nav_push_noti;
            }

            navigationView.getMenu().findItem(currentMenuId).setChecked(true);
        } catch (Exception e) {
            Utils.psErrorLogE("Refresh Notification View Error. " , e);
        }

    }

    public void savePushMessage(String msg) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("_push_noti_message", msg);
        editor.apply();

        Utils.psLog("push noti at mainactivity : " + msg);
    }

    public void showDownPicasso(){
//        try{
//            this.p.shutdown();
//        }catch(Exception e){
//            Utils.psErrorLogE("Error in Shutdown picasso.", e);
//        }

    }


//    public void loadProfileImage(String path) {
//
//        if(!path.equals("")){
//
//            p =new Picasso.Builder(this)
//                    .memoryCache(new LruCache(1))
//                    .build();
//
//            final String fileName = path;
//            Utils.psLog("file name : " + fileName);
//
//            Target target = new Target() {
//
//                @Override
//                public void onPrepareLoad(Drawable arg0) {
//                    Utils.psLog("Prepare Image to load.");
//                }
//
//                @Override
//                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                    Utils.psLog("inside onBitmapLoaded ");
//
//                    try {
//                        File file;
//                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//                        File directory = cw.getDir("imageDir", Context.MODE_APPEND);
//                        file = new File(directory,fileName);
//                        FileOutputStream ostream = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
//                        ostream.close();
//                        Utils.psLog("Success Image Loaded.");
//
//                        refreshProfile();
//
//                        // After download finished the profile image
//                        // shutdown the Picasso threads
//                        showDownPicasso();
//
//                    } catch (Exception e) {
//                        Utils.psErrorLogE(e.getMessage(), e);
//                    }
//
//                }
//
//                @Override
//                public void onBitmapFailed(Drawable arg0) {
//                    Utils.psLog("Fail Fail Fail");
//
//                }
//            };
//
//            Utils.psLog("profile photo : " + Config.APP_IMAGES_URL + path);
//
//            p.load(Config.APP_IMAGES_URL + path)
//                    .into(target);
//        }
//
//    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Enum
     **------------------------------------------------------------------------------------------------*/
    public enum FABActions {
        PROFILE,
        SHOPLIST,
        SHOPMAP
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Enum
     **------------------------------------------------------------------------------------------------*/


}
