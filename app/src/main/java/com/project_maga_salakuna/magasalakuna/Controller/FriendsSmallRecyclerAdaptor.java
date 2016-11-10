package com.project_maga_salakuna.magasalakuna.Controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project_maga_salakuna.magasalakuna.Model.User;
import com.project_maga_salakuna.magasalakuna.R;

import java.util.ArrayList;

/**
 * Created by lakshan on 11/3/16.
 */
public class FriendsSmallRecyclerAdaptor extends RecyclerView.Adapter<FriendsSmallRecyclerAdaptor.RecyclerViewHolder> implements View.OnClickListener{
    ArrayList<User> friendList = null;

    public FriendsSmallRecyclerAdaptor(ArrayList<User> friendList) {
        this.friendList = friendList;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_row_layout_small,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        User user = friendList.get(position);
        holder.nameTxt.setText(user.getFirstName() + " "+ user.getLastName() );
        if(friendList.get(position).getPicture() !=null){
            byte[] decodedString = Base64.decode(user.getPicture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.photoview.setBackgroundResource(0);
            holder.photoview.setImageBitmap(decodedByte);
        }
        holder.lastseentxt.setText(timeConversion(user.getTime()));

    }
    @Override
    public int getItemCount() {
        return friendList.size();
    }
    @Override
    public void onClick(View view) {

    }
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView photoview = null;
        TextView nameTxt = null;
        TextView lastseentxt = null;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            photoview = (ImageView) itemView.findViewById(R.id.photoView);
            nameTxt = (TextView) itemView.findViewById(R.id.fullnameTxt);
            lastseentxt = (TextView) itemView.findViewById(R.id.lstseentxt);
        }
    }
    public String timeConversion(long time){
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - time;
        String output="";
        if (diff < 1000*60*5){
            output += String.valueOf("Now");
        }else if (diff < 1000*60*60){
            output += String.valueOf(diff/(1000*60)) + "m";
        }else if (diff < 1000*60*60*24){
            output += String.valueOf(diff/(1000*60*60)) + "h";
        }else if (diff < 1000*60*60*24*31){
            output += String.valueOf(diff/(1000*60*60*24)) + "d";
        }else{
            output += String.valueOf("long ago");
        }
        return output;
    }

}

