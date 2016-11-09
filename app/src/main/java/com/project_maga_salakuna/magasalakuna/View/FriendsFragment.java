package com.project_maga_salakuna.magasalakuna.View;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project_maga_salakuna.magasalakuna.Controller.FriendListRecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Controller.FriendRequestsRecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Controller.FriendsLargeRecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Controller.JSONParser;
import com.project_maga_salakuna.magasalakuna.Model.User;
import com.project_maga_salakuna.magasalakuna.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    public ArrayList<User> friendrequestList =null;
    public ArrayList<User> friendtList =null;
    View view = null;
    Activity activity;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView friendsrecyclerView;
    private RecyclerView.Adapter friendsadapter;
    private RecyclerView.LayoutManager friendslayoutManager;
    SwipeRefreshLayout swipeContainer;
    JSONParser jsonParser = new JSONParser();
    private static final String SEARCH_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/searchfriendrequests.php";
    private static final String SEARCH_FRIEND_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/friendlist.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        activity = getActivity();
        friendrequestList = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.friendrequestsrecyclervirew);
        friendsrecyclerView = (RecyclerView) view.findViewById(R.id.friendsrecyclervirew);
        configureSwipeLayout();
        new SearchFriendRequests().execute();
        new SearchFriends().execute();
        return view;
    }
    class SearchFriendRequests extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
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

                friendrequestList = new ArrayList<>();
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
                        friendrequestList.add(user);
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
                Toast.makeText(activity, "No Friend Requests", Toast.LENGTH_LONG).show();
            }
            adapter = new FriendRequestsRecyclerAdaptor(friendrequestList,getActivity());
            recyclerView.setAdapter(adapter);
            if (swipeContainer.isRefreshing()){
                swipeContainer.setRefreshing(false);
            }
            //adapter.notifyDataSetChanged();
        }
    }
    public void configureSwipeLayout(){
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
    public void fetchTimelineAsync() {
        new SearchFriendRequests().execute();
        new SearchFriends().execute();
    }
    class SearchFriends extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            friendslayoutManager = new LinearLayoutManager(getContext());
            friendsrecyclerView.setLayoutManager(friendslayoutManager);
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
                        SEARCH_FRIEND_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);

                friendtList = new ArrayList<>();
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
                        friendtList.add(user);
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
                Toast.makeText(getContext(), "No Friends", Toast.LENGTH_LONG).show();
            }
            friendsadapter = new FriendsLargeRecyclerAdaptor(friendtList,null,getContext());
            friendsrecyclerView.setAdapter(friendsadapter);
            friendsrecyclerView.setNestedScrollingEnabled(true);
            //friendsadapter.notifyDataSetChanged();
        }
    }



}
