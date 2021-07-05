package com.panaceasoft.restaurateur.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.adapters.CategoryAdapter;
import com.panaceasoft.restaurateur.adapters.ItemAdapter;
import com.panaceasoft.restaurateur.adapters.NewsViewPagerAdapter;
import com.panaceasoft.restaurateur.adapters.ShopCategoryAdapter;
import com.panaceasoft.restaurateur.adapters.UserAdapter;
import com.panaceasoft.restaurateur.listeners.ClickListener;
import com.panaceasoft.restaurateur.listeners.RecyclerTouchListener;
import com.panaceasoft.restaurateur.models.CategoryRowData;
import com.panaceasoft.restaurateur.models.PAttributesData;
import com.panaceasoft.restaurateur.models.PCategoryData;
import com.panaceasoft.restaurateur.models.PImageData;
import com.panaceasoft.restaurateur.models.PItemData;
import com.panaceasoft.restaurateur.models.PNewsData;
import com.panaceasoft.restaurateur.models.PReviewData;
import com.panaceasoft.restaurateur.models.PShopData;
import com.panaceasoft.restaurateur.models.PSubCategoryData;
import com.panaceasoft.restaurateur.models.TableUserData;
import com.panaceasoft.restaurateur.utilities.CacheRequest;
import com.panaceasoft.restaurateur.utilities.DBHandler;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectedShopActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView detailImage;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    //    private List<CategoryRowData> categoryRowDataList = new ArrayList<>();
    private int selectedShopID;
    private PShopData pShop;
    private MenuItem menuItem;
    private int basketCount = 0;
    private DBHandler db = new DBHandler(this);
    private Picasso p;
    private CoordinatorLayout mainLayout;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private NewsViewPagerAdapter newAdapter;
    private ItemAdapter mAdapter;
    private ArrayList<PNewsData> newsData;

    private Button btnCartCount;
    SharedPreferences pref;

    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;

    private List<PItemData> pItemDataList;
    List<PCategoryData> pCategoryDataList;

    SharedPreferences prefs;

    ArrayList<TableUserData.Datum> tableUserList;

    FloatingActionButton floatUser;

    double shopLat = 0.0, shopLng = 0.0;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     * *------------------------------------------------------------------------------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_shop);

        initUI();

        loadData();

//        saveSelectedShopInfo(pShop);

        bindData();

//        loadCategoryGrid();

//        loadNewsSlider();
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("heredetailOn","heredetailOn");
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                if (data.getStringExtra("close_activity").equals("YES")) {
                    finish();
                }
                basketCount = db.getBasketCountByShopId(selectedShopID);

                if (basketCount > 0) {
                    btnCartCount.setText(""+basketCount);
//                    menuItem.setIcon(Utils.buildCounterDrawable(basketCount, R.drawable.ic_shopping_cart_white, this));
                } else {
//                    menuItem.setVisible(false);
                }
            }

        }

    }

    @Override
    protected void onDestroy() {
        try {

            mRecyclerView.addOnItemTouchListener(null);
            collapsingToolbar = null;
            toolbar = null;
            detailImage.setImageResource(0);
            detailImage = null;
            mLayoutManager = null;
            pItemDataList.clear();
            mAdapter = null;
            pItemDataList = null;
            pCategoryDataList.clear();
            pCategoryDataList = null;

//            p.shutdown();
            Utils.unbindDrawables(mainLayout);

            super.onDestroy();
        } catch (Exception e) {
            super.onDestroy();
        }

    }

    @Override
    public void onClick(View v) {
        Utils.psLog("Click Click Click >>>>> ");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if (dots != null) {
            for (int i = 0; i < dotsCount; i++) {
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
            }

            if (dots.length >= position) {
                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void initUI() {

        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in init data.", e);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        tableUserList = new ArrayList<>();

        initToolbar();
        initCollapsingToolbarLayout();
        mainLayout = findViewById(R.id.coordinator_layout);
        btnCartCount = findViewById(R.id.btn_cart_text);

        floatUser  =  findViewById(R.id.float_user);

        p = new Picasso.Builder(this).build();

        prgDialog = new ProgressDialog(SelectedShopActivity.this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
    }

    private void initCollapsingToolbarLayout() {
        try {
            collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initCollapsingToolbarLayout.", e);
        }
    }

    private void initToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setTitle("");

            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.back_icon, this.getTheme());
            toolbar.setNavigationIcon(drawable);

            toolbar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }

    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void loadData() {

        pItemDataList = new ArrayList<>();
        pCategoryDataList = new ArrayList<>();

        selectedShopID = getIntent().getIntExtra("selected_shop_id", 0);

        detailImage = findViewById(R.id.detail_image);

        basketCount = db.getBasketCountByShopId(selectedShopID);

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (pref.getInt("_login_user_id", 0) != 0) {
            if (basketCount > 0) {
                btnCartCount.setText(""+basketCount);
            }
        }

        try {
            if(!prefs.getBoolean("_login_user_table_enter", false)) {
                doEnterTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        if (pref.getInt("_login_user_id", 0) != 0 && pref.getBoolean("_login_user_table_enter", false)) {
            if(pref.getString("shop_id", "").equalsIgnoreCase(String.valueOf(selectedShopID))) {
                floatUser.setVisibility(View.VISIBLE);
            } else {
                floatUser.setVisibility(View.GONE);
            }
        } else {
            floatUser.setVisibility(View.GONE);
        }

        requestData(Config.APP_API_URL + Config.GET_SHOP_ALL + selectedShopID + "/item/all");


        floatUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogTableUser != null && dialogTableUser.isShowing()) {
                    dialogTableUser.dismiss();
                } else {
                    doShowTableUser();
                }
            }
        });

    }


    private void requestData(String uri) {
        Utils.psLog("API URL requestData : " + uri);
//        mAdapter.loading = true;

        CacheRequest cacheRequest = new CacheRequest(0, uri, new Response.Listener<NetworkResponse>(){
            @Override
            public void onResponse(NetworkResponse response) {
                try {

                    pItemDataList.clear();
                    pCategoryDataList.clear();

                    String jsonString ;
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    JSONObject jsonObject = new JSONObject(jsonString);

                    Utils.psLog(jsonString);
                    String jsonStatusSuccessString = getResources().getString(R.string.json_status_success);

                    try {
                        String status = jsonObject.getString("status");
                        if (status.equals(jsonStatusSuccessString)) {

                            Gson gson = new Gson();
                            Type listType = new TypeToken<PShopData>() {}.getType();

                            PShopData pShopData = gson.fromJson(jsonObject.getString("data"), listType);
                            if(pShopData != null) {

                                if(pShopData.lat != null && !pShopData.lat.equalsIgnoreCase("")) {
                                    shopLat = Double.valueOf(pShopData.lat);
                                }

                                if(pShopData.lng != null && !pShopData.lng.equalsIgnoreCase("")) {
                                    shopLng = Double.valueOf(pShopData.lng);
                                }

                                for(int i = 0; i < pShopData.categories.size(); i++) {
                                    pCategoryDataList.add(pShopData.categories.get(i));
                                    Log.e("herecat","herecat"+pCategoryDataList.size());
//                                    boolean newItemTitle = true;
                                    for(int j = 0; j < pShopData.categories.get(i).sub_categories.size(); j++) {
                                        for(int k = 0; k < pShopData.categories.get(i).sub_categories.get(j).items.size(); k++) {
//                                            if(newItemTitle) {
//
//                                                PItemData pItemData = new PItemData(pShopData.id, pShopData.categories.get(i).id, pShopData.categories.get(i).sub_categories.get(j).id,
//                                                        -1, 0, "", "", pShopData.categories.get(i).name, "", 0,
//                                                        "", 0, "", "", "", "", "", "", "",
//                                                        "", null, null, null);
//
//                                                pItemDataList.add(pItemData);
//
//                                                pItemDataList.add(pShopData.categories.get(i).sub_categories.get(j).items.get(k));
//
//                                            } else {
                                            pItemDataList.add(pShopData.categories.get(i).sub_categories.get(j).items.get(k));
//                                            }
//                                            newItemTitle = false;
                                        }
                                    }
                                }
                            }

                            pShop = pShopData;
                            saveSelectedShopInfo(pShop);
                            newsData = pShop.feeds;

                            Utils.bindImageRound(SelectedShopActivity.this, p, detailImage, pShop.cover_image_file, 1);

                            detailImage.startAnimation(AnimationUtils.loadAnimation(SelectedShopActivity.this, R.anim.fade_in));

                            loadCategoryGrid();

                            loadShopItem();

//                            loadNewsSlider();
                        } else {
                            stopLoading();
                            Utils.psLog("Error in loading Sub Categories.");
                        }
                    } catch (JSONException e) {
                        stopLoading();
                        e.printStackTrace();
                    } catch (Exception e){
                        Utils.psErrorLog("Error in loading.", e);
                    }

                } catch (UnsupportedEncodingException | JSONException e) {
                    stopLoading();
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Utils.psLog(error.getMessage());
                }catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }
            }
        });


        cacheRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(cacheRequest);

    }

    private void stopLoading() {
//        mAdapter.loading = false;
    }

    private void initData() {

        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in init data.", e);
        }

        try {
            detailImage = findViewById(R.id.detail_image);
            pShop = GlobalData.shopdata;
            selectedShopID = pShop.id;
            newsData = pShop.feeds;

            basketCount = db.getBasketCountByShopId(selectedShopID);

            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            if (pref.getInt("_login_user_id", 0) != 0) {
                if (basketCount > 0) {
                    btnCartCount.setText(""+basketCount);
                }
            }
            Utils.psLog("News Title : " + pShop.feeds.get(0).title);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }

        try {
            doEnterTable();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void bindData() {
        try {
            if (collapsingToolbar != null) {
                collapsingToolbar.setTitle("");
                makeCollapsingToolbarLayoutLooksGood(collapsingToolbar);
            }


            Utils.psLog("Shipping : " + pShop.flat_rate_shipping);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/
    ViewPager viewPager;
    TabLayout tabLayout;
    public void loadShopItem() {
        try {
//            viewPager  =  findViewById(R.id.viewPager);
            tabLayout  =  findViewById(R.id.tabs_view_pager);

            tabLayout.removeAllTabs();

            for(int i = 0; i < pCategoryDataList.size(); i++) {
                tabLayout.addTab(tabLayout.newTab().setText(pCategoryDataList.get(i).name));
            }

            tabLayout.getTabAt(0).select();

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mAdapter.getFilter().filter(String.valueOf(pCategoryDataList.get(tabLayout.getSelectedTabPosition()).id));
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            Log.e("herecatitem","herecatitem");
//            ShopCategoryAdapter shopCategoryAdapter = new ShopCategoryAdapter(SelectedShopActivity.this, pCategoryDataList);
//            viewPager.setAdapter(shopCategoryAdapter);
//            tabLayout.setupWithViewPager(viewPager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     * *------------------------------------------------------------------------------------------------
     */
    public void loadCategoryGrid() {
        try {
            mRecyclerView = findViewById(R.id.my_recycler_view);

            mRecyclerView.setHasFixedSize(true);

//            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mLayoutManager = new LinearLayoutManager(SelectedShopActivity.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new ItemAdapter(this, pItemDataList, mRecyclerView, p);
            mRecyclerView.setAdapter(mAdapter);


            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    onItemClicked(position);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            mRecyclerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in loadCategoryGrid.", e);
        }
    }

    public void openActivity(int selected_item_id){
        final Intent intent;
        intent = new Intent(this, DetailActivity.class);
        Utils.psLog("Selected Shop ID : " + selectedShopID);
        intent.putExtra("selected_item_id", selected_item_id);
        intent.putExtra("selected_shop_id", selectedShopID);
        intent.putExtra("selected_shop_lat", shopLat);
        intent.putExtra("selected_shop_lng", shopLng);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public void onItemClicked(int position) {
        if(pItemDataList != null) {
            if(pItemDataList.size() >= position) {
                if(pItemDataList.get(position).shop_id != -1)
                    this.openActivity(pItemDataList.get(position).id);
            }
        }
    }

//    public void loadNewsSlider() {
//        try {
//            ViewPager news_images = findViewById(R.id.pager_introduction);
//            if (newsData.size() > 1) {
//
//                detailImage.setVisibility(View.GONE);
//                pager_indicator = findViewById(R.id.viewPagerCountDots);
//                newAdapter = new NewsViewPagerAdapter(this, newsData, p);
//                news_images.setAdapter(newAdapter);
//
//                news_images.setCurrentItem(0);
//                news_images.addOnPageChangeListener(this);
//
//                setupSliderPagination();
//                news_images.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
//
//            } else {
//
//                news_images.setVisibility(View.GONE);
//
////            p.load(Config.APP_IMAGES_URL + pShop.cover_image_file)
////                    //.transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
////                    .resize(MAX_WIDTH, MAX_HEIGHT)
////                    .onlyScaleDown()
////                    .into(detailImage);
//
//                Utils.bindImage(this, p, detailImage, pShop.cover_image_file, 1);
//
//                detailImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
//
//            }
//        } catch (Exception e) {
//            Utils.psErrorLog("loadNewsSlider", e);
//        }
//    }

    private void setupSliderPagination() {
        try {
            if (newAdapter != null) {
                dotsCount = newAdapter.getCount();
                if (dotsCount > 0) {

                    dots = new ImageView[dotsCount];

                    for (int i = 0; i < dotsCount; i++) {
                        dots[i] = new ImageView(this);
                        dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                        params.setMargins(4, 0, 4, 0);

                        pager_indicator.addView(dots[i], params);
                    }

                    dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("Error in dots. ", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/


    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void makeCollapsingToolbarLayoutLooksGood(CollapsingToolbarLayout collapsingToolbarLayout) {
        try {
            final Field field = collapsingToolbarLayout.getClass().getDeclaredField("mCollapsingTextHelper");
            field.setAccessible(true);

            final Object object = field.get(collapsingToolbarLayout);
            final Field tpf = object.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);

            ((TextPaint) tpf.get(object)).setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            ((TextPaint) tpf.get(object)).setColor(getResources().getColor(R.color.colorAccent));
        } catch (Exception ignored) {
        }
    }

    private void saveSelectedShopInfo(PShopData ct) {
        try {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("_id", ct.id);
            editor.putString("_name", ct.name);
            editor.putString("_cover_image", ct.cover_image_file);
            editor.putString("_address", ct.address);
            editor.putString("_shop_region_lat", ct.lat);
            editor.putString("_shop_region_lng", ct.lng);
            editor.apply();
        } catch (Exception e) {
            Utils.psErrorLogE("Error in saveSelectedShopInfo.", e);
        }
    }

    private void showCartEmpty() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_error);
//                getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView textMsg = dialog.findViewById(R.id.text_msg);
        TextView textOk = dialog.findViewById(R.id.text_ok);

        dialog.show();

        textMsg.setText(getResources().getString(R.string.cart_empty));

        textOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    Dialog dialogEnterTable;
    private void doEnterTable() {
        dialogEnterTable = new Dialog(this, R.style.Theme_Dialog);
        dialogEnterTable.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEnterTable.setCancelable(false);
        dialogEnterTable.setContentView(R.layout.custom_dialog_table_enter);

        dialogEnterTable.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView textAdd          =  dialogEnterTable.findViewById(R.id.text_add_table);
        TextView textBrowse       =  dialogEnterTable.findViewById(R.id.text_browse_table);
        final EditText edtTableId =  dialogEnterTable.findViewById(R.id.edit_table_id);

        dialogEnterTable.show();

        textAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref.getInt("_login_user_id", 0) != 0) {
                    if(!edtTableId.getText().toString().trim().equalsIgnoreCase("")) {
                        if(getLocation() && _latitude != 0.0 && _longitude != 0.0) {
                            if(getDistance(shopLat, shopLng) <= 10.0) {
                                doEnterTableLogin(edtTableId.getText().toString().trim());
                            } else {
                                showFailPopup("You are not in the restaurant");
                            }
                        } else {
                            showFailPopup("Please wait while GPS fetching the current location");
                        }
                    } else {
                        showFailPopup(getResources().getString(R.string.enter_table_error));
                    }
                } else {
                    showNeedLogin();
                }
            }
        });

        textBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEnterTable.dismiss();
            }
        });
    }


    private void doEnterTableLogin(String tableId) {

        final String URL = Config.APP_API_URL + Config.TABLE_LOGIN;
        Utils.psLog(URL);

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id",  String.valueOf(pref.getInt("_login_user_id", 0)));
        params.put("shop_id",  String.valueOf(selectedShopID));
        params.put("table_id", tableId);

        doTableSubmit(URL, params, tableId);

    }


    private void doTableSubmit(String postURL, HashMap<String, String> params, final String table_id) {
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
                                prgDialog.cancel();

                                dialogEnterTable.dismiss();

                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("shop_id", String.valueOf(selectedShopID));
                                editor.putString("table_id", table_id);
                                editor.putString("shop_lat", String.valueOf(shopLat));
                                editor.putString("shop_lng", String.valueOf(shopLng));
                                editor.putBoolean("_login_user_table_enter", true);
                                editor.apply();


                                floatUser.setVisibility(View.VISIBLE);
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
        VolleySingleton.getInstance(SelectedShopActivity.this).addToRequestQueue(req);

    }




    public void doBill(View view) {
        if (pref.getInt("_login_user_id", 0) != 0) {
            if(!prefs.getString("shop_id", "").equalsIgnoreCase("") &&
                    prefs.getString("shop_id", "").equalsIgnoreCase(String.valueOf(selectedShopID))) {
                Intent intent = new Intent(getApplicationContext(), PaymentOptionActivity.class);
                intent.putExtra("selected_shop_id", selectedShopID);
                //startActivity(intent);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            } else {
                showFailPopup(getResources().getString(R.string.error_restaurant_login));
            }
        } else {
            showNeedLogin();
        }
    }

    public void doOrderHistory(View view) {
        if (basketCount > 0) {
          /*  if(!prefs.getString("shop_id", "").equalsIgnoreCase("") &&
                    prefs.getString("shop_id", "").equalsIgnoreCase(String.valueOf(selectedShopID))) {*/
                Intent intent = new Intent(getApplicationContext(), BasketActivity.class);
                intent.putExtra("selected_shop_id", selectedShopID);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
           /* } else {
                showFailPopup(getResources().getString(R.string.error_restaurant_login));
            }*/
        } else {
            showCartEmpty();
        }
    }

    public void doCallWaiter(View view) {

        if (pref.getInt("_login_user_id", 0) != 0) {
            if(!prefs.getString("shop_id", "").equalsIgnoreCase("")) {
                if(prefs.getString("shop_id", "").equalsIgnoreCase(String.valueOf(selectedShopID))) {

                    final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.custom_dialog_call_waiter);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                    TextView textCallWaiter   = dialog.findViewById(R.id.text_call_waiter);
                    TextView textCancel       = dialog.findViewById(R.id.text_cancel);
                    final EditText editReq    = dialog.findViewById(R.id.edit_req);

                    dialog.show();


                    textCallWaiter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(editReq.getText().toString().trim().equalsIgnoreCase("")) {
                                showFailPopup(getResources().getString(R.string.enter_req_error));
                            } else {
                                doWaiterCall(editReq.getText().toString().trim());
                                dialog.dismiss();
                            }
                        }
                    });

                    textCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                } else {
                    showFailPopup(getResources().getString(R.string.error_restaurant_login));
                }
            } else {
                showFailPopup(getResources().getString(R.string.error_table_login));
            }
        } else {
            showNeedLogin();
        }
    }

    boolean isDialog = false;
    private void showNeedLogin() {
        try {
            if(!isDialog) {
                isDialog = true;
                final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.custom_dialog_error);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView textMsg = dialog.findViewById(R.id.text_msg);
                TextView textOk = dialog.findViewById(R.id.text_ok);

                dialog.show();

                textMsg.setText(getResources().getString(R.string.login_error_first));

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

    private void showFailPopup(String msg) {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_error);

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


    private void doWaiterCall(String message) {

        final String URL = Config.APP_API_URL + Config.CALL_WAITER;
        Utils.psLog(URL);

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id",  String.valueOf(pref.getInt("_login_user_id", 0)));
        params.put("shop_id",  String.valueOf(selectedShopID));
        params.put("table_id", pref.getString("table_id", ""));
        params.put("message",  message);

        doSubmit(URL, params);

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
                                prgDialog.cancel();


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
        VolleySingleton.getInstance(SelectedShopActivity.this).addToRequestQueue(req);

    }



    Dialog dialogTableUser;
    int selectPos = 0;
    private void doTableUserDialog() {
        selectPos = 0;
        dialogTableUser = new Dialog(this, R.style.Theme_Dialog_New);
        dialogTableUser.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTableUser.setCancelable(true);
        dialogTableUser.setContentView(R.layout.custom_dialog_table_user);

        dialogTableUser.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView recyclerView = dialogTableUser.findViewById(R.id.recycler_user);
        recyclerView.setLayoutManager(new GridLayoutManager(SelectedShopActivity.this, 3));
        recyclerView.setHasFixedSize(true);

        UserAdapter userAdapter = new UserAdapter(SelectedShopActivity.this, tableUserList);
        recyclerView.setAdapter(userAdapter);


        dialogTableUser.show();

    }


    private void doShowTableUser() {

        final String URL = Config.APP_API_URL + Config.GET_PEOPLE_ON_TABLE + String.valueOf(selectedShopID) + "/table_id/" + pref.getString("table_id", "") ;
        Utils.psLog(URL);

        prgDialog.show();

        requestDataTable(URL);
    }


    boolean apiCall = false;
    private void requestDataTable(String uri) {
        Utils.psLog("API URL requestData : " + uri);

        apiCall = true;

        CacheRequest cacheRequest = new CacheRequest(0, uri, new Response.Listener<NetworkResponse>(){
            @Override
            public void onResponse(NetworkResponse response) {
                if(apiCall) {
                    apiCall = false;
                    try {
                        String jsonString;

                        jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonObject = new JSONObject(jsonString);

                        Utils.psLog(jsonString);
                        prgDialog.dismiss();
                        try {
                            String status = jsonObject.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<TableUserData.Datum>>() {
                                }.getType();

                                TableUserData tableUserData = gson.fromJson(jsonString, TableUserData.class);
                                if (tableUserData != null) {
                                    if (tableUserList.size() > 0) {
                                        tableUserList.clear();
                                    }

                                    tableUserList.addAll(tableUserData.getData());

                                    doTableUserDialog();
                                } else {
                                    showFailPopup("Error in loading data");
                                }
                            } else {
                                showFailPopup("Error in loading data");
                                Utils.psLog("Error in loading Sub Categories.");
                            }
                        } catch (JSONException e) {
                            showFailPopup("Error in loading data");
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.psErrorLog("Error in loading.", e);
                        }

                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                        prgDialog.show();
                    }
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    prgDialog.show();
                    Utils.psLog(error.getMessage());
                }catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }
            }
        });

        cacheRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(cacheRequest);

    }



}
