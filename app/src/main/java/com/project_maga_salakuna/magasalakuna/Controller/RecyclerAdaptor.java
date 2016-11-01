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
 * Created by lakshan on 9/14/16.
 */
public class RecyclerAdaptor extends RecyclerView.Adapter<RecyclerAdaptor.RecyclerViewHolder> implements View.OnClickListener{
    ArrayList<User> friendList = null;

    public RecyclerAdaptor(ArrayList<User> friendList) {
        this.friendList = friendList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.nameTxt.setText(friendList.get(position).getFirstName() + " "+ friendList.get(position).getLastName() );
        holder.emailTxt.setText(friendList.get(position).getEmail());
        holder.phoneTxt.setText(friendList.get(position).getPhone());
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

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView photoview = null;
        TextView nameTxt = null;
        TextView emailTxt = null;
        TextView phoneTxt = null;
        ImageButton addBtn = null;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            photoview = (ImageView) itemView.findViewById(R.id.photoView);
            nameTxt = (TextView) itemView.findViewById(R.id.fullnameTxt);
            emailTxt = (TextView) itemView.findViewById(R.id.emailTxtView);
            phoneTxt = (TextView) itemView.findViewById(R.id.phoneTxt);
            addBtn = (ImageButton) itemView.findViewById(R.id.addfriendbtn);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }
}
