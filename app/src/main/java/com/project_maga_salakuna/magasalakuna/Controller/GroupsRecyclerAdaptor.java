package com.project_maga_salakuna.magasalakuna.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project_maga_salakuna.magasalakuna.Model.Group;
import com.project_maga_salakuna.magasalakuna.R;
import com.project_maga_salakuna.magasalakuna.View.AddGroupActivity;
import com.project_maga_salakuna.magasalakuna.View.GroupMapViewActivity;

import java.util.ArrayList;

/**
 * Created by lakshan on 11/8/16.
 */
public class GroupsRecyclerAdaptor extends RecyclerView.Adapter<GroupsRecyclerAdaptor.RecyclerViewHolder> implements View.OnClickListener{
    ArrayList<Group> groupList = null;
    Context context = null;
    public GroupsRecyclerAdaptor(ArrayList<Group> groupList, Context context) {
        this.context = context;
        this.groupList = groupList;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_row_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, context, groupList);
        return recyclerViewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.nameTxt.setText(group.getName());
        if(group.getPicture() !=null){
            byte[] decodedString = Base64.decode(group.getPicture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.photoview.setBackgroundResource(0);
            holder.photoview.setImageBitmap(decodedByte);
        }
    }
    @Override
    public int getItemCount() {
        return groupList.size();
    }
    @Override
    public void onClick(View view) {

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView photoview = null;
        TextView nameTxt = null;
        ArrayList<Group> groupList = null;
        Context context = null;
        public RecyclerViewHolder(View itemView, Context context, ArrayList<Group> groupList) {
            super(itemView);
            this.context =context;
            this.groupList = groupList;
            photoview = (ImageView) itemView.findViewById(R.id.photoView);
            nameTxt = (TextView) itemView.findViewById(R.id.fullnameTxt);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, GroupMapViewActivity.class);
            String id = groupList.get(getAdapterPosition()).getId();
            String name = groupList.get(getAdapterPosition()).getName();
            String pic = groupList.get(getAdapterPosition()).getPicture();
            intent.putExtra("id",id);
            intent.putExtra("name", name);
            intent.putExtra("pic",pic);
            context.startActivity(intent);
        }
    }
}

