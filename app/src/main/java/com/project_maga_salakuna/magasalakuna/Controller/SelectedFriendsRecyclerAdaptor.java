package com.project_maga_salakuna.magasalakuna.Controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.project_maga_salakuna.magasalakuna.Model.User;
import com.project_maga_salakuna.magasalakuna.R;
import com.project_maga_salakuna.magasalakuna.View.AddGroupActivity;

import java.util.ArrayList;

/**
 * Created by lakshan on 11/5/16.
 */
public class SelectedFriendsRecyclerAdaptor extends RecyclerView.Adapter<SelectedFriendsRecyclerAdaptor.RecyclerViewHolder> implements View.OnClickListener{
    ArrayList<User> friendList = null;
    Context context = null;
    public SelectedFriendsRecyclerAdaptor(ArrayList<User> friendList, Context context) {
        this.context = context;
        this.friendList = friendList;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_friend_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, context, friendList);
        return recyclerViewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if(friendList.get(position).getPicture() !=null){
            byte[] decodedString = Base64.decode(friendList.get(position).getPicture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.photoview.setBackgroundResource(0);
            holder.photoview.setImageBitmap(decodedByte);
        }
    }
    @Override
    public int getItemCount() {
        return friendList.size();
    }
    @Override
    public void onClick(View view) {

    }

    public void removeItem(int position){
        friendList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,friendList.size());
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        CircularImageView photoview = null;
        ArrayList<User> friendList = null;
        Context context = null;
        public RecyclerViewHolder(View itemView, Context context, ArrayList<User> friendList) {
            super(itemView);
            this.context =context;
            this.friendList = friendList;
            photoview = (CircularImageView) itemView.findViewById(R.id.photoView);
        }
    }
}
