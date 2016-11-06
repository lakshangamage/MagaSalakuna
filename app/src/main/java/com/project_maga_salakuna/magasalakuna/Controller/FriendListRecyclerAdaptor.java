package com.project_maga_salakuna.magasalakuna.Controller;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project_maga_salakuna.magasalakuna.Model.User;
import com.project_maga_salakuna.magasalakuna.R;
import com.project_maga_salakuna.magasalakuna.View.AddGroupActivity;
import com.project_maga_salakuna.magasalakuna.View.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lakshan on 11/5/16.
 */
public class FriendListRecyclerAdaptor extends RecyclerView.Adapter<FriendListRecyclerAdaptor.RecyclerViewHolder> implements View.OnClickListener{
    ArrayList<User> friendList = null;
    Context context = null;
    AddGroupActivity addGroupActivity;
    public FriendListRecyclerAdaptor(ArrayList<User> friendList, Context context) {
        this.context = context;
        this.friendList = friendList;
        addGroupActivity= (AddGroupActivity) context;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_select_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, context, friendList);
        return recyclerViewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        User user = friendList.get(position);
        holder.nameTxt.setText(user.getFirstName() + " "+ user.getLastName() );
        if(user.getPicture() !=null){
            byte[] decodedString = Base64.decode(user.getPicture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.photoview.setBackgroundResource(0);
            holder.photoview.setImageBitmap(decodedByte);
        }
        if (isSelected(user)){
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(true);
            holder.checkBox.setOnCheckedChangeListener(holder);
        }
    }
    @Override
    public int getItemCount() {
        return friendList.size();
    }
    @Override
    public void onClick(View view) {

    }

    public void addFriend(User user){
        addGroupActivity.addFriendToGroup(user);
    }
    public void removeFriend(User user){
        addGroupActivity.removeFriendfromGroup(user);
    }
    public boolean isSelected(User user){
        return addGroupActivity.isSelected(user);
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener{
        ImageView photoview = null;
        TextView nameTxt = null;
        CheckBox checkBox = null;
        ArrayList<User> friendList = null;
        Context context = null;
        public RecyclerViewHolder(View itemView, Context context, ArrayList<User> friendList) {
            super(itemView);
            this.context =context;
            this.friendList = friendList;
            photoview = (ImageView) itemView.findViewById(R.id.photoView);
            nameTxt = (TextView) itemView.findViewById(R.id.fullnameTxt);
            checkBox = (CheckBox) itemView.findViewById(R.id.selectfriendchck);
            checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (compoundButton.getId() == checkBox.getId()){
                User user = friendList.get(getAdapterPosition());
                if (compoundButton.isChecked()){
                    addFriend(user);
                }else{
                    removeFriend(user);
                }
            }
        }
    }
}
