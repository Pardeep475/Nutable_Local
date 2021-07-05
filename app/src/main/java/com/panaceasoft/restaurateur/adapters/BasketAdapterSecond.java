package com.panaceasoft.restaurateur.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.BasketActivity;
import com.panaceasoft.restaurateur.listeners.ClickListener;
import com.panaceasoft.restaurateur.models.BasketData;
import com.panaceasoft.restaurateur.utilities.DBHandler;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

/**
 * Created by Panacea-Soft on 28/9/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class BasketAdapterSecond extends RecyclerView.Adapter<BasketAdapterSecond.ViewHolder> {
    private final Activity activity;
    private LayoutInflater inflater;
    private final List<BasketData> basketData;
    private final Context mContext;
    private int itemQty = 0;
    private final int loginUserId;
    private final DBHandler db;
    private Double totalAmount = 0.0;
    private final int selectedShopId;
    private final Picasso p;
    private int lastPosition = -1;
    ClickListener listeners;

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtItemTitle;
        private TextView txtItemPrice;
        private final TextView txtItemSubTotal;
        private TextView txtItemQty;
        private TextView txtQty;
        private ImageView btnDelete;
        private ImageView btnIncrease;
        private ImageView btnDecrease;
        private TextView txtAttr;
        ImageView imgItemPhoto;



        ViewHolder(View itemView) {
            super(itemView);
            txtItemTitle = itemView.findViewById(R.id.item_title);


            txtItemPrice = itemView.findViewById(R.id.item_price);


            txtItemSubTotal = itemView.findViewById(R.id.item_sub_total);
            txtAttr = itemView.findViewById(R.id.item_attr);
            txtItemQty = itemView.findViewById(R.id.item_qty);
            txtQty = itemView.findViewById(R.id.qty);
            btnDelete = itemView.findViewById(R.id.delete_btn);
            btnIncrease = itemView.findViewById(R.id.increase_btn);
            btnDecrease = itemView.findViewById(R.id.decrease_btn);
            imgItemPhoto = itemView.findViewById(R.id.thumbnail);

        }
    }


    public BasketAdapterSecond(Activity activity, List<BasketData> basketData, int loginUserId, DBHandler dbHandler, int shopId, Picasso p, ClickListener clickListener) {
        this.activity = activity;
        this.basketData = basketData;
        this.db = dbHandler;
        this.loginUserId = loginUserId;
        this.selectedShopId = shopId;
        mContext = this.activity.getApplicationContext();
        this.p = p;
        listeners=clickListener;

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.basket_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.txtItemTitle.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));
        holder.txtItemPrice.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.txtItemSubTotal.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.txtAttr.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.txtItemQty.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));


        if (activity != null) {
            holder.txtItemTitle.setTypeface(Utils.getTypeFace(activity, Utils.Fonts.ROBOTO));
            holder.txtItemPrice.setTypeface(Utils.getTypeFace(activity, Utils.Fonts.ROBOTO));
            holder.txtItemSubTotal.setTypeface(Utils.getTypeFace(activity, Utils.Fonts.ROBOTO));
            holder.txtAttr.setTypeface(Utils.getTypeFace(activity, Utils.Fonts.ROBOTO));
            holder.txtItemQty.setTypeface(Utils.getTypeFace(activity, Utils.Fonts.ROBOTO));
            holder.txtQty.setTypeface(Utils.getTypeFace(activity, Utils.Fonts.ROBOTO));
        }


        holder.btnDelete.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));
        holder.btnIncrease.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemQty = db.getQTYByKeyIds(basketData.get(position).getId(), basketData.get(position).getShopId()) + 1;

                String qtyStr = String.valueOf(itemQty);
                holder.txtItemQty.setText(qtyStr);
                //String tmpItemSubTotal = "Sub Total : " + String.valueOf(String.format(Locale.US, "%.2f", (Float.valueOf(basketData.get(position).getUnitPrice()) * itemQty)) + basketData.get(position).getCurrencySymbol());
                String tmpItemSubTotal = "Sub Total : " + Utils.format(Double.valueOf(basketData.get(position).getUnitPrice()) * itemQty) + basketData.get(position).getCurrencySymbol();
                holder.txtItemSubTotal.setText(tmpItemSubTotal);

                db.updateBasketByIds(new BasketData(
                        basketData.get(position).getItemId(),
                        basketData.get(position).getShopId(),
                        loginUserId,
                        basketData.get(position).getName(),
                        basketData.get(position).getDesc(),
                        String.valueOf(basketData.get(position).getUnitPrice()),
                        basketData.get(position).getDiscountPercent(),
                        itemQty,
                        basketData.get(position).getImagePath(),
                        basketData.get(position).getCurrencySymbol(),
                        basketData.get(position).getCurrencyShortForm(),
                        basketData.get(position).getSelectedAttributeNames(),
                        basketData.get(position).getSelectedAttributeIds()
                ), basketData.get(position).getId(), basketData.get(position).getShopId());

                refreshTotalAmount();

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doDeleteItem(v, position);

            }
        });

        holder.btnDecrease.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.getQTYByKeyIds(basketData.get(position).getId(), basketData.get(position).getShopId()) != 1) {

                    itemQty = db.getQTYByKeyIds(basketData.get(position).getId(), basketData.get(position).getShopId()) - 1;

                    String qtyStr = String.valueOf(itemQty);
                    holder.txtItemQty.setText(qtyStr);
                    String tmpItemSubTotal = "Sub Total : " + String.valueOf(String.format(Locale.US, "%.2f", (Float.valueOf(basketData.get(position).getUnitPrice()) * itemQty)) + basketData.get(position).getCurrencySymbol());
                    holder.txtItemSubTotal.setText(tmpItemSubTotal);

                    db.updateBasketByIds(new BasketData(
                            basketData.get(position).getItemId(),
                            basketData.get(position).getShopId(),
                            loginUserId,
                            basketData.get(position).getName(),
                            basketData.get(position).getDesc(),
                            String.valueOf(basketData.get(position).getUnitPrice()),
                            basketData.get(position).getDiscountPercent(),
                            itemQty,
                            basketData.get(position).getImagePath(),
                            basketData.get(position).getCurrencySymbol(),
                            basketData.get(position).getCurrencyShortForm(),
                            basketData.get(position).getSelectedAttributeNames(),
                            basketData.get(position).getSelectedAttributeIds()
                    ), basketData.get(position).getId(), basketData.get(position).getShopId());

                    refreshTotalAmount();
                }

            }
        });

        BasketData basket = basketData.get(position);

        holder.txtItemTitle.setText(basket.getName());

        String itemPriceStr = this.mContext.getResources().getString(R.string.price) + " " + Utils.format(Double.valueOf(basket.getUnitPrice())) + basket.getCurrencySymbol();
        holder.txtItemPrice.setText(itemPriceStr);

        double calcuatedSubTotal = Float.parseFloat(basket.getUnitPrice()) * basket.getQty();
        String itemSubTotalStr = this.mContext.getResources().getString(R.string.sub_total) + " " + Utils.format(calcuatedSubTotal) + basket.getCurrencySymbol();
        holder.txtItemSubTotal.setText(itemSubTotalStr);

        String attrString = this.mContext.getResources().getString(R.string.attribute) + " " + basket.selected_attribute_names;
        holder.txtAttr.setText(attrString);

        String itemQtyStr = String.valueOf(basket.getQty());
        holder.txtItemQty.setText(itemQtyStr);
        itemQty = basket.getQty();


        if (basket.getImagePath() != null) {
            Utils.bindImage(activity, p, holder.imgItemPhoto, basket.getImagePath(), 2);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listeners.onClick(v,position);
            }
        });
    }

    public BasketData getItem(int position) {
        return basketData.get(position);
    }

    @Override
    public int getItemCount() {

        return basketData.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            @SuppressLint("PrivateResource") Animation animation = AnimationUtils.loadAnimation(activity, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        } else {
            lastPosition = position;
        }
    }

    private void doDeleteItem(View v, final int position) {

        Utils.psLog("Delete Basket Item");
        new AlertDialog.Builder(v.getRootView().getContext())
                .setTitle(Utils.activity.getString(R.string.app_name))
                .setMessage(Utils.activity.getString(R.string.want_to_remove))
                .setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        db.deleteBasketByKeyIds(basketData.get(position).getId(), basketData.get(position).getShopId());
                        basketData.remove(position);
                        notifyDataSetChanged();
                        refreshTotalAmount();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }

    private void refreshTotalAmount() {
        try {
            List<BasketData> basket = db.getAllBasketDataByShopId(selectedShopId);
            for (BasketData basketData : basket) {
                totalAmount += basketData.getQty() * Float.parseFloat(basketData.getUnitPrice());
            }
//            totalAmount = Double.valueOf(String.format(Locale.US, "%.2f", totalAmount));
            ((BasketActivity) activity).updateTotalAmount(totalAmount);
            totalAmount = 0.0;
        } catch (Exception e) {
            Utils.psErrorLog("Error refreshTotalAmount.", e);
        }
    }


}