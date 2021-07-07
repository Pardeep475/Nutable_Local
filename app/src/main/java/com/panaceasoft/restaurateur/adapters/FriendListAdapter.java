package com.panaceasoft.restaurateur.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.models.FriendListData;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{
    ArrayList<FriendListData> mListFriend;
    public FriendListAdapter(ArrayList<FriendListData> listFriend){
        mListFriend=listFriend;
    }

    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_split_friend, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, int position) {
           holder.mTextName.setText(mListFriend.get(position).getUsername());
           holder.mTextMobile.setText(mListFriend.get(position).getPhone());
           //holder.mTextNameFirstLetter.setText(mListFriend.get(position).getUsername().charAt(0));
    }

    @Override
    public int getItemCount() {
        return mListFriend.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextNameFirstLetter;
        TextView mTextName;
        TextView mTextMobile;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextMobile = itemView.findViewById(R.id.textFriendPhone);
            mTextName = itemView.findViewById(R.id.textFriendName);
            mTextNameFirstLetter = itemView.findViewById(R.id.textFirstLetter);
        }
    }
}
