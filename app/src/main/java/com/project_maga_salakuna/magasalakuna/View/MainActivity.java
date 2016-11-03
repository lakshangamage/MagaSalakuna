package com.project_maga_salakuna.magasalakuna.View;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project_maga_salakuna.magasalakuna.Controller.JSONParser;
import com.project_maga_salakuna.magasalakuna.Controller.RecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Controller.ViewPagerAdaptor;
import com.project_maga_salakuna.magasalakuna.Model.CheckIn;
import com.project_maga_salakuna.magasalakuna.Model.User;
import com.project_maga_salakuna.magasalakuna.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Intent intent;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    NavigationView navigationView;
    ViewPagerAdaptor viewPagerAdaptor;
    public static String id;
    public static String firstname;
    public static String lastname;
    public static String photo;
    private static final String SEARCH_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/getcheckins.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private ProgressDialog pDialog;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public ArrayList<User> friendList=null;
    public ArrayList<User> searchList=null;
    Context context = this;
    JSONParser jsonParser = new JSONParser();
    private String searchString;
    View headerView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ArrayList<CheckIn> checkIns;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureToolbar();
        //configureDrawer();
        configureTabLayout();
        //configureSearchView();
        //configureDrawerHeader();
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    public void logout(MenuItem item){
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbarmenu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        //SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return true;
    }
    public void configureToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
    }
    public void configureDrawer(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView = (NavigationView) findViewById(R.id.navogation_view);
        recyclerView = (RecyclerView) findViewById(R.id.friendsrecyclervirew);
        headerView = navigationView.inflateHeaderView(R.layout.drawerheader);
        imageView = (ImageView) headerView.findViewById(R.id.headerimage);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View drawerView) {
                if(drawerView.equals(navigationView)) {
                    getSupportActionBar().setTitle(getTitle());
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    actionBarDrawerToggle.syncState();
                }
            }
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                if(drawerView.equals(navigationView)) {
                    getSupportActionBar().setTitle(getString(R.string.app_name));
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    actionBarDrawerToggle.syncState();
                }
            }
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Avoid normal indicator glyph behaviour. This is to avoid glyph movement when opening the right drawer
                //super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }
    public void configureTabLayout(){
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdaptor = new ViewPagerAdaptor(getSupportFragmentManager());
        viewPagerAdaptor.addFragments(new HomeFragment(),"Home");
        viewPagerAdaptor.addFragments(new CheckInFragment(),"Check Ins");
        viewPagerAdaptor.addFragments(new FriendsFragment(),"Friends");
        viewPagerAdaptor.addFragments(new EventsFragment(),"Events");
        viewPager.setAdapter(viewPagerAdaptor);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);
    }
    public void configureDrawerHeader(){
        intent = this.getIntent();
        id = intent.getStringExtra("id");
        firstname = intent.getStringExtra("firstname");
        lastname = intent.getStringExtra("lastname");
        photo = intent.getStringExtra("picture");
        Bundle params = new Bundle();
        params.putString("fields", "id,first_name, last_name, email,gender,cover,picture.type(large)");
        if(intent.getStringExtra("loginmethod").equals("email")){
            TextView headerfirstname = (TextView) headerView.findViewById(R.id.headerfirstname);
            TextView headerlastname = (TextView) headerView.findViewById(R.id.headerlastname);
            headerfirstname.setText(intent.getStringExtra("firstname"));
            headerlastname.setText(intent.getStringExtra("lastname"));
            String picstring = intent.getStringExtra("picture");
            byte[] decodedString = Base64.decode(picstring, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setBackgroundResource(0);
            imageView.setImageBitmap(decodedByte);

        }else{
            new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            if (response != null) {
                                try {
                                    JSONObject data = response.getJSONObject();
                                    if (data.has("picture")) {
                                        String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                        new DownloadImageTask((ImageView) findViewById(R.id.headerimage))
                                                .execute(profilePicUrl);

                                        TextView headerfirstname = (TextView) headerView.findViewById(R.id.headerfirstname);
                                        TextView headerlastname = (TextView) headerView.findViewById(R.id.headerlastname);
                                        headerfirstname.setText(data.getString("first_name"));
                                        headerlastname.setText(data.getString("last_name"));
                                        // set profile image to imageview using Picasso or Native methods
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).executeAsync();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(drawerLayout == null || navigationView == null || recyclerView == null || actionBarDrawerToggle == null) {
            configureDrawer();
            configureDrawerHeader();
            new SearchFriends().execute();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        for(int i = 0; i< menu.size(); i++)
            menu.getItem(i).setVisible(!drawerLayout.isDrawerOpen(navigationView));
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_friends_near:
                actionBarDrawerToggle.onOptionsItemSelected(item);
                if(drawerLayout.isDrawerOpen(recyclerView))
                    drawerLayout.closeDrawer(recyclerView);
                else drawerLayout.openDrawer(recyclerView);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    class SearchFriends extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Refreshing...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        SEARCH_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);


                if (success == 1) {
                    JSONArray checkins = json.getJSONArray("checkins");
                    JSONArray users = json.getJSONArray("users");
                    User user = null;
                    CheckIn checkIn = null;
                    checkIns = new ArrayList<>();
                    searchList = new ArrayList<>();
                    for (int i = 0; i < checkins.length(); i++) {
                        String uid = ((JSONObject) (checkins.get(i))).getString("uid");
                        String checkinid = ((JSONObject) (checkins.get(i))).getString("id");
                        String status = ((JSONObject) (checkins.get(i))).getString("status");
                        String at = ((JSONObject) (checkins.get(i))).getString("at");
                        double longitude = ((JSONObject) (checkins.get(i))).getDouble("longitude");
                        double lattitude = ((JSONObject) (checkins.get(i))).getDouble("lattitude");
                        String timestamp = ((JSONObject) (checkins.get(i))).getString("timestamp");

                        String id = ((JSONObject) (users.get(i))).getString("id");
                        String firstname = ((JSONObject) (users.get(i))).getString("first_name");
                        String lastname = ((JSONObject) (users.get(i))).getString("last_name");
                        String email = ((JSONObject) (users.get(i))).getString("email");
                        String phone = ((JSONObject) (users.get(i))).getString("phone");
                        String picture = ((JSONObject) (users.get(i))).getString("picture");
                        user = new User(id, firstname, lastname, email, phone, picture);
                        checkIn = new CheckIn(checkinid, uid, status, timestamp, at, longitude, lattitude, user);
                        checkIns.add(checkIn);
                        searchList.add(user);
                    }
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

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(context, file_url, Toast.LENGTH_LONG).show();
            }
            adapter = new RecyclerAdaptor(searchList);
            recyclerView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
        }
    }
    public void refresh(){
        new SearchFriends().execute();
    }
}
