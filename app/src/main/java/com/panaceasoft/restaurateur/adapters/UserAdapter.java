package com.panaceasoft.restaurateur.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.models.TableUserData;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Activity activity;
    private List<TableUserData.Datum> userDataList;

    static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView textUserName;

        UserViewHolder(View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.text_user_name);
        }
    }

    public UserAdapter(Context context, List<TableUserData.Datum> userDataList) {
        this.activity = (Activity) context;
        this.userDataList = userDataList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        holder.textUserName.setText(userDataList.get(position).getUsername());
        holder.textUserName.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

    }

    @Override
    public int getItemCount() {
        if (userDataList != null) {
            return userDataList.size();
        }
        return 0;
    }

}
