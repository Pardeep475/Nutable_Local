package com.panaceasoft.restaurateur.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.SearchResultActivity;
import com.panaceasoft.restaurateur.listeners.SelectListener;
import com.panaceasoft.restaurateur.models.PShopData;
import com.panaceasoft.restaurateur.uis.PSPopupSingleSelectView;
import com.panaceasoft.restaurateur.utilities.CacheRequest;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 8/6/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class SearchFragment extends Fragment {

    private TextView txt_search;
    private int selectedShopId;
    private int selectedVal = 0;
    private String selectedShopName;
    private String selectCityString;
    private NumberPicker numberPicker ;
    private String jsonStatusSuccessString;
    private String[] numberPickerValues;
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------
    public SearchFragment() {

    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initData();

        initUI(view);

        return view;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initUI(View view) {

        jsonStatusSuccessString = getResources().getString(R.string.json_status_success);

//        LinearLayout popupContainer = view.findViewById(R.id.choose_container);
//        popupContainer.removeAllViews();
//
//        Log.e("heresearch","heresearch"+selectCityString+" "+GlobalData.shopDatas.size());
//
//        PSPopupSingleSelectView psPopupSingleSelectView = new PSPopupSingleSelectView(getActivity(), selectCityString, GlobalData.shopDatas, "");
//        psPopupSingleSelectView.setOnSelectListener(new SelectListener() {
//            @Override
//            public void Select(View view, int position, CharSequence text) {
//
//            }
//
//            @Override
//            public void Select(View view, int position, CharSequence text, int id) {
//                selectedShopId = id;
//                selectedShopName = text.toString();
//            }
//
//            @Override
//            public void Select(View view, int position, CharSequence text, int id, float additionalPrice) {
//
//            }
//        });
//        popupContainer.addView(psPopupSingleSelectView);

        numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e("herechange","herechange"+newVal+" "+numberPickerValues[newVal]+" "+GlobalData.shopDatas.get(newVal).id);
                selectedVal = newVal;
                if(selectedVal != 0) {
                    selectedShopId = GlobalData.shopDatas.get(newVal - 1).id;
                    selectedShopName = numberPickerValues[newVal];
                }
            }
        });

        loadData();

        Button btn_search = view.findViewById(R.id.button_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    if(selectedVal != 0) {
                        prepareForSearch();
                    } else {
                        showFailPopup();
                    }
                }
            }
        });

        txt_search = view.findViewById(R.id.input_search);

        if(Config.SHOW_ADMOB) {
            AdView mAdView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            AdView mAdView = view.findViewById(R.id.adView);
            mAdView.setVisibility(View.GONE);
        }

        btn_search.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        txt_search.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initData(){
        selectCityString = getResources().getString(R.string.select_shop);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init Data
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void prepareForSearch() {
        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra("selected_shop_id", selectedShopId + "");
        intent.putExtra("search_keyword", txt_search.getText().toString().trim());
        intent.putExtra("selected_shop_name", selectedShopName);
        startActivity(intent);
        if(getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void loadData() {
        if (GlobalData.shopDatas.size() > 1) {
            numberPickerValues = new String[GlobalData.shopDatas.size()+1];
            numberPickerValues[0] = "Please select the shop";
            for(int i = 0; i < GlobalData.shopDatas.size(); i++) {
                numberPickerValues[i+1] = GlobalData.shopDatas.get(i).name;
            }
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(GlobalData.shopDatas.size()-1);
            numberPicker.setDisplayedValues(numberPickerValues);
            numberPicker.setValue(0);
        } else {
            //Utils.psLog("Global Data is nothing so need to call API.");
            //startLoading();
            requestData(Config.APP_API_URL + Config.GET_ALL);
        }
    }

    private void requestData(String uri) {
        try {
            CacheRequest cacheRequest = new CacheRequest(0, uri, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try {
                        final String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonObject = new JSONObject(jsonString);
                        Log.e("here","here"+jsonObject);
                        String status = jsonObject.getString("status");
                        if (status.equals(jsonStatusSuccessString)) {

                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<PShopData>>() {
                            }.getType();

                            ArrayList<PShopData> pShopDataList = gson.fromJson(jsonObject.getString("data"), listType);

                            if (pShopDataList != null) {
                                GlobalData.shopDatas.clear();
                                GlobalData.shopDatas.addAll(pShopDataList);
                            }
                            numberPickerValues = new String[GlobalData.shopDatas.size()+1];
                            numberPickerValues[0] = "Please select the shop";
                            for(int i = 0; i < GlobalData.shopDatas.size(); i++) {
                                numberPickerValues[i+1] = GlobalData.shopDatas.get(i).name;
                            }
                            numberPicker.setMinValue(0);
                            numberPicker.setMaxValue(GlobalData.shopDatas.size()-1);
                            numberPicker.setDisplayedValues(numberPickerValues);
                            numberPicker.setValue(0);
                        } else {
                            Utils.psLog("Error in loading ShopList.");
                        }
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        Utils.psErrorLog("Error in loading.", e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            cacheRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(cacheRequest);
        } catch (Exception e) {
            Utils.psErrorLog("Error in loading.", e);
        }

    }

    private void showFailPopup() {
        try {
            final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_error);
//                getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView textMsg = dialog.findViewById(R.id.text_msg);
            TextView textOk = dialog.findViewById(R.id.text_ok);

            dialog.show();

            textMsg.setText("Please select the shop");

            textOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            Utils.psErrorLog("showFailPopup", e);
        }
    }

}