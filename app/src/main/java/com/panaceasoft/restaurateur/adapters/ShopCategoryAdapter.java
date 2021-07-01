
package com.panaceasoft.restaurateur.adapters;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.models.PCategoryData;
import com.panaceasoft.restaurateur.models.PShopData;

import java.util.ArrayList;
import java.util.List;

public class ShopCategoryAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    List<PCategoryData> pCategoryData = new ArrayList<>();

    public ShopCategoryAdapter(Context context, List<PCategoryData> pCategoryData) {
        mContext = context;
        this.pCategoryData = pCategoryData;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return pCategoryData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_shop_category_pager, container, false);

        TextView textShopTitle = itemView.findViewById(R.id.item_shop_name);
        textShopTitle.setText(pCategoryData.get(position).name);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public float getPageWidth(int position) {
        return 0.4f;
    }

}