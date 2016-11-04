package com.project_maga_salakuna.magasalakuna.Controller;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project_maga_salakuna.magasalakuna.Model.User;
import com.project_maga_salakuna.magasalakuna.R;
import com.project_maga_salakuna.magasalakuna.View.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lakshan on 11/4/16.
 */
public class FriendRequestsRecyclerAdaptor extends RecyclerView.Adapter<FriendRequestsRecyclerAdaptor.RecyclerViewHolder> implements View.OnClickListener{
    ArrayList<User> friendList = null;
    Context context = null;
    private static final String acceptURL = "http://176.32.230.51/pathmila.com/maga_salakuna/acceptFriend.php";
    private static final String cancelURL    = "http://176.32.230.51/pathmila.com/maga_salakuna/cancelFriend.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public FriendRequestsRecyclerAdaptor(ArrayList<User> friendList, Context context) {
        this.context = context;
        this.friendList = friendList;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_row_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, context, friendList);
        return recyclerViewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.nameTxt.setText(friendList.get(position).getFirstName() + " "+ friendList.get(position).getLastName() );
        holder.emailTxt.setText(friendList.get(position).getEmail());
        if(friendList.get(position).getPicture() !=null){
            byte[] decodedString = Base64.decode(friendList.get(position).getPicture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.photoview.setBackgroundResource(0);
            holder.photoview.setImageBitmap(decodedByte);
        }
        String id = friendList.get(position).getId();
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
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView photoview = null;
        TextView nameTxt = null;
        TextView emailTxt = null;
        Button addBtn = null;
        Button rejectBtn = null;
        ArrayList<User> friendList = null;
        Context context = null;
        public RecyclerViewHolder(View itemView, Context context, ArrayList<User> friendList) {
            super(itemView);
            this.context =context;
            this.friendList = friendList;
            photoview = (ImageView) itemView.findViewById(R.id.photoView);
            nameTxt = (TextView) itemView.findViewById(R.id.fullnameTxt);
            emailTxt = (TextView) itemView.findViewById(R.id.emailTxtView);
            addBtn = (Button) itemView.findViewById(R.id.addfriendbtn);
            rejectBtn = (Button) itemView.findViewById(R.id.rejectfriendbtn);
            rejectBtn.setOnClickListener(this);
            addBtn.setOnClickListener(this);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View view) {
            if (view.getId() == addBtn.getId()){
                User user = friendList.get(getAdapterPosition());
                Button button = (Button) view;
                new ConfirmFriend(user.getId(),context).execute();
                removeItem(getAdapterPosition());
            }else if (view.getId() == rejectBtn.getId()){
                User user = friendList.get(getAdapterPosition());
                Button button = (Button) view;
                new RejectFriend(user.getId(),context).execute();
                removeItem(getAdapterPosition());
            }
        }
    }

    public class ConfirmFriend extends AsyncTask<String, String, String> {
        String id;
        JSONParser jsonParser = new JSONParser();
        Context context;
        String friendId;
        public ConfirmFriend(String friendId, Context context){
            this.friendId = friendId;
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            id = MainActivity.id;
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("friend1", friendId));
                params.add(new BasicNameValuePair("friend2", id));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        acceptURL, "POST", params);

                // check your log for json response
                Log.d("Adding attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);


                if (success == 1) {
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    //Toast.makeText(Login.this, "Invalid login details", Toast.LENGTH_LONG).show();
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        protected void onPostExecute(String file_url) {
            if (file_url != null){
                Toast.makeText(context, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }

    public class RejectFriend extends AsyncTask<String, String, String> {
        String id;
        JSONParser jsonParser = new JSONParser();
        Context context;
        String friendId;
        public RejectFriend(String friendId, Context context){
            this.friendId = friendId;
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            id = MainActivity.id;
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("friend1", friendId));
                params.add(new BasicNameValuePair("friend2", id));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        cancelURL, "POST", params);

                // check your log for json response
                Log.d("Adding attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);


                if (success == 1) {
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    //Toast.makeText(Login.this, "Invalid login details", Toast.LENGTH_LONG).show();
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        protected void onPostExecute(String file_url) {
            if (file_url != null){
                Toast.makeText(context, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}
