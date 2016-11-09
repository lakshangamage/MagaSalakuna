package com.project_maga_salakuna.magasalakuna.View;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.project_maga_salakuna.magasalakuna.Controller.FriendListRecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Controller.FriendRequestsRecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Controller.JSONParser;
import com.project_maga_salakuna.magasalakuna.Controller.SelectedFriendsRecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Model.User;
import com.project_maga_salakuna.magasalakuna.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity {
    Context context = this;
    public ArrayList<User> friendList=null;
    public ArrayList<User> tempfriendList=null;
    public ArrayList<User> selectedfriendList=new ArrayList<>();
    private Toolbar toolbar;
    private CircularImageView circularImageView;
    private EditText nametext;
    private RecyclerView selectedRecyclerView;
    private RecyclerView friendsRecyclerView;
    private RecyclerView.Adapter friendsadapter;
    private RecyclerView.Adapter selectedfriendsadapter;
    private RecyclerView.LayoutManager friendslayoutManager;
    private RecyclerView.LayoutManager selectedfriendslayoutManager;
    private EditText searchText;
    private ProgressDialog pDialog;
    String profileimage;
    int previousLength = 3;
    JSONParser jsonParser = new JSONParser();
    private static final String SEARCH_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/friendlist.php";
    private static final String CREATE_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/creategroups.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        configureToolbar();
        configureGui();
        configureSelectedRecyclerView();
        new SearchFriends().execute();
        configureSearchText();
    }
    public void configureToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Group");
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addgroupmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_create_group:
                new CreateGroup().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void configureImage(){
        circularImageView = (CircularImageView) findViewById(R.id.headerimage);
        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 1);
            }
        });
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap imagemap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(picturePath),300,300,false);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagemap.compress(Bitmap.CompressFormat.JPEG, 40, stream); //compress to which format you want.
            byte[] byte_arr = stream.toByteArray();
            profileimage = Base64.encodeToString(byte_arr,Base64.DEFAULT);
            circularImageView.setBackgroundResource(0);
            circularImageView.setImageBitmap(imagemap);
        }
    }
    public boolean requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        2);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        return true;
    }
    private void configureGui(){
        configureImage();
        nametext = (EditText) findViewById(R.id.editText);
        friendsRecyclerView = (RecyclerView) findViewById(R.id.friendsrecyclervirew);
    }
    class SearchFriends extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            friendslayoutManager = new LinearLayoutManager(context);
            friendsRecyclerView.setLayoutManager(friendslayoutManager);
            friendsRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    // Disallow the touch request for parent scroll on touch of child view
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            });
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", MainActivity.id));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        SEARCH_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);

                friendList = new ArrayList<>();
                if (success == 1) {
                    JSONArray users = json.getJSONArray("users");
                    User user = null;
                    for (int i = 0; i< users.length();i++){
                        String id = ((JSONObject)(users.get(i))).getString("id");
                        String firstname = ((JSONObject)(users.get(i))).getString("first_name");
                        String lastname = ((JSONObject)(users.get(i))).getString("last_name");
                        String email = ((JSONObject)(users.get(i))).getString("email");
                        String phone = ((JSONObject)(users.get(i))).getString("phone");
                        String picture = ((JSONObject)(users.get(i))).getString("picture");
                        user = new User(id, firstname,lastname,email,phone,picture);
                        friendList.add(user);
                    }
                    return String.valueOf(json.getInt(TAG_SUCCESS));
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

        protected void onPostExecute(String file_url) {
            if (file_url.equals("0")){
                Toast.makeText(context, "No Friends", Toast.LENGTH_LONG).show();
            }
            friendsadapter = new FriendListRecyclerAdaptor(friendList,context);
            friendsRecyclerView.setAdapter(friendsadapter);
            friendsRecyclerView.setNestedScrollingEnabled(true);
            //friendsadapter.notifyDataSetChanged();
        }
    }
    public void addFriendToGroup(User user){
        selectedfriendList.add(user);
        selectedfriendsadapter.notifyItemInserted(selectedfriendList.size()-1);
        selectedfriendsadapter.notifyItemRangeChanged(selectedfriendList.size()-1,selectedfriendList.size());
    }
    public boolean isSelected(User user){
        return selectedfriendList.contains(user);
    }
    public void removeFriendfromGroup(User user) {
        int position = selectedfriendList.indexOf(user);
        selectedfriendList.remove(user);
        selectedfriendsadapter.notifyItemRemoved(position);
        selectedfriendsadapter.notifyItemRangeChanged(position, selectedfriendList.size());
    }
    private void configureSelectedRecyclerView(){
        selectedRecyclerView = (RecyclerView) findViewById(R.id.selectedfriendsrecyclervirew);
        selectedfriendslayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        selectedRecyclerView.setLayoutManager(selectedfriendslayoutManager);
        selectedfriendsadapter = new SelectedFriendsRecyclerAdaptor(selectedfriendList,context);
        selectedRecyclerView.setAdapter(selectedfriendsadapter);
    }
    private void configureSearchText(){
        searchText = (EditText) findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 3){
                    if (charSequence.length() > previousLength){
                        previousLength++;
                        for (User user: tempfriendList){
                            if (!user.getFirstName().toLowerCase().contains(charSequence)){
                                tempfriendList.remove(user);
                            }
                        }
                    }else {
                        if (previousLength!=3)
                            previousLength--;
                        tempfriendList = new ArrayList<User>();
                        for (User user: friendList){
                            if (user.getFirstName().toLowerCase().contains(charSequence)){
                                tempfriendList.add(user);
                            }
                        }
                    }

                }else {
                    tempfriendList = new ArrayList<User>();
                    tempfriendList.addAll(friendList);
                }
                friendsadapter = new FriendListRecyclerAdaptor(tempfriendList,context);
                friendsRecyclerView.setAdapter(friendsadapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    class CreateGroup extends AsyncTask<String, String, String> {
        String name;
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Creating Group...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            name = nametext.getText().toString();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                ArrayList<String> idList = new ArrayList<>();
                idList.add(MainActivity.id);
                for (User user : selectedfriendList){
                    idList.add(user.getId());
                }
                JSONObject request = new JSONObject();
                request.put("name",name);
                request.put("picture", profileimage);
                JSONArray jsonArray = new JSONArray(idList);
                request.put("members", jsonArray);
                String requestString = request.toString();
                params.add(new BasicNameValuePair("group", requestString));
                Log.d("request!", "starting");
                // getting product details by making HTTP request
                jsonParser = new JSONParser();
                JSONObject json = null;
                json = jsonParser.makeHttpRequest(
                        CREATE_URL, "POST_JSON", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());
                // json success tag
                success = json.getInt(TAG_SUCCESS);
                return json.getString(TAG_MESSAGE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            Toast.makeText(context, file_url, Toast.LENGTH_LONG).show();
            finish();
            //friendsadapter.notifyDataSetChanged();
        }
    }
}
