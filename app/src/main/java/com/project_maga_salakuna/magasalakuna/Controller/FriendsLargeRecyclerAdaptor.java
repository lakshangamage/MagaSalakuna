package com.project_maga_salakuna.magasalakuna.Controller;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project_maga_salakuna.magasalakuna.Model.Friends;
import com.project_maga_salakuna.magasalakuna.Model.User;
import com.project_maga_salakuna.magasalakuna.R;
import com.project_maga_salakuna.magasalakuna.View.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lakshan on 9/14/16.
 */
public class FriendsLargeRecyclerAdaptor extends RecyclerView.Adapter<FriendsLargeRecyclerAdaptor.RecyclerViewHolder> implements View.OnClickListener{
    ArrayList<User> searchList = null;
    ArrayList<Friends> friendList = null;
    Context context = null;

    private static final String SEARCH_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/addFriend.php";
    private static final String acceptURL = "http://176.32.230.51/pathmila.com/maga_salakuna/acceptFriend.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public FriendsLargeRecyclerAdaptor(ArrayList<User> searchList, ArrayList<Friends> friendList, Context context) {
        this.searchList = searchList;
        this.context = context;
        this.friendList = friendList;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view,context, searchList, friendList);
        return recyclerViewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.nameTxt.setText(searchList.get(position).getFirstName() + " "+ searchList.get(position).getLastName() );
        holder.emailTxt.setText(searchList.get(position).getEmail());
        holder.phoneTxt.setText(searchList.get(position).getPhone());
        if(searchList.get(position).getPicture() !=null){
            byte[] decodedString = Base64.decode(searchList.get(position).getPicture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.photoview.setBackgroundResource(0);
            holder.photoview.setImageBitmap(decodedByte);
        }
        String id = searchList.get(position).getId();
        if (id.equals(MainActivity.id)) return;
        for (Friends friend :
                friendList) {
            if (friend.getFriend2().equals(id)){
                if (friend.getAccepted() == 0){
                    holder.addBtn.setTextColor(Color.parseColor("#a84d4d"));
                    holder.addBtn.setText("Request Sent");
                    //button.setBackground(null);
                    holder.addBtn.setBackgroundColor(0xffffff);
                    holder.addBtn.setEnabled(false);
                }else{
                    holder.addBtn.setTextColor(Color.parseColor("#249e00"));
                    holder.addBtn.setText(Html.fromHtml("&#x2713") + " Friends");
                    //button.setBackground(null);
                    holder.addBtn.setBackgroundColor(0xffffff);
                    holder.addBtn.setEnabled(false);
                }
            }else if(friend.getFriend1().equals(id)){
                if (friend.getAccepted() == 0){
                    holder.addBtn.setText("Confirm");
                }else{
                    holder.addBtn.setTextColor(Color.parseColor("#249e00"));
                    holder.addBtn.setText(Html.fromHtml("&#x2713") + " Friends");
                    //button.setBackground(null);
                    holder.addBtn.setBackgroundColor(0xffffff);
                    holder.addBtn.setEnabled(false);
                }
            }
        }

    }
    @Override
    public int getItemCount() {
        return searchList.size();
    }
    @Override
    public void onClick(View view) {

    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView photoview = null;
        TextView nameTxt = null;
        TextView emailTxt = null;
        TextView phoneTxt = null;
        Button addBtn = null;
        ArrayList<User> searchList = null;
        ArrayList<Friends> friendList = null;
        Context context = null;
        public RecyclerViewHolder(View itemView, Context context, ArrayList<User> searchList, ArrayList<Friends> friendList) {
            super(itemView);
            this.searchList = searchList;
            this.context =context;
            this.friendList = friendList;
            photoview = (ImageView) itemView.findViewById(R.id.photoView);
            nameTxt = (TextView) itemView.findViewById(R.id.fullnameTxt);
            emailTxt = (TextView) itemView.findViewById(R.id.emailTxtView);
            phoneTxt = (TextView) itemView.findViewById(R.id.phoneTxt);
            addBtn = (Button) itemView.findViewById(R.id.addfriendbtn);
            addBtn.setOnClickListener(this);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View view) {
            if (view.getId() == addBtn.getId()){
                User user = searchList.get(getAdapterPosition());
                Button button = (Button) view;
                if (button.getText().equals("Confirm")){
                    new ConfirmFriend(user.getId(),context,button).execute();
                }else{
                    new AddFriend(user.getId(),context,button).execute();
                }
            }
        }
    }

    public class AddFriend extends AsyncTask<String, String, String> {
        String id;
        JSONParser jsonParser = new JSONParser();
        Context context;
        String friendId;
        Button button;
        public AddFriend(String friendId, Context context, Button button){
            this.friendId = friendId;
            this.context = context;
            this.button = button;
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
                params.add(new BasicNameValuePair("friend1", id));
                params.add(new BasicNameValuePair("friend2", friendId));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        SEARCH_URL, "POST", params);

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
            button.setTextColor(Color.parseColor("#a84d4d"));
            button.setText("Request Sent");
            //button.setBackground(null);
            button.setBackgroundColor(0xffffff);
            button.setEnabled(false);
        }
    }
    public class ConfirmFriend extends AsyncTask<String, String, String> {
        String id;
        JSONParser jsonParser = new JSONParser();
        Context context;
        String friendId;
        Button button;
        public ConfirmFriend(String friendId, Context context, Button button){
            this.friendId = friendId;
            this.context = context;
            this.button = button;
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
            button.setTextColor(Color.parseColor("#249e00"));
            button.setText(Html.fromHtml("&#x2713") + " Friends");
            //button.setBackground(null);
            button.setBackgroundColor(0xffffff);
            button.setEnabled(false);
        }
    }
}
