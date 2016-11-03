package com.project_maga_salakuna.magasalakuna.View;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.project_maga_salakuna.magasalakuna.Controller.JSONParser;
import com.project_maga_salakuna.magasalakuna.Controller.FriendsLargeRecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Model.User;
import com.project_maga_salakuna.magasalakuna.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private static final String SEARCH_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/searchfriends.php";
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
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        configureToolbar();
        handleIntent();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent();
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
    class SearchFriends extends AsyncTask<String, String, String> {
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Searching Friends...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("name", searchString));
                Log.d("request!", "starting");
                JSONObject json = jsonParser.makeHttpRequest(SEARCH_URL, "POST", params);
                Log.d("Login attempt", json.toString());
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray users = json.getJSONArray("users");
                    User user = null;
                    searchList = new ArrayList<>();
                    for (int i = 0; i< users.length();i++){
                        String id = ((JSONObject)(users.get(i))).getString("id");
                        String firstname = ((JSONObject)(users.get(i))).getString("first_name");
                        String lastname = ((JSONObject)(users.get(i))).getString("last_name");
                        String email = ((JSONObject)(users.get(i))).getString("email");
                        String phone = ((JSONObject)(users.get(i))).getString("phone");
                        String picture = ((JSONObject)(users.get(i))).getString("picture");
                        user = new User(id, firstname,lastname,email,phone,picture);
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
            adapter = new FriendsLargeRecyclerAdaptor(searchList);
            recyclerView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
        }
    }
    private void handleIntent(){
        friendList = new ArrayList<>();
        searchList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.friendsrecyclervirew);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchString = intent.getStringExtra(SearchManager.QUERY);
            new SearchFriends().execute();
//            Toast toast = Toast.makeText(this, searchString, Toast.LENGTH_SHORT);
//            toast.show();
        }
    }
}
