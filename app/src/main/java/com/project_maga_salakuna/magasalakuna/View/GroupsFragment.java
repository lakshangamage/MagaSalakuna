package com.project_maga_salakuna.magasalakuna.View;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project_maga_salakuna.magasalakuna.Controller.FriendRequestsRecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Controller.GroupsRecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Controller.JSONParser;
import com.project_maga_salakuna.magasalakuna.Model.Group;
import com.project_maga_salakuna.magasalakuna.Model.User;
import com.project_maga_salakuna.magasalakuna.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {
    FloatingActionButton fab;
    Activity activity;
    ArrayList<Group> groupList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    SwipeRefreshLayout swipeContainer;
    JSONParser jsonParser = new JSONParser();
    private static final String SEARCH_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/grouplist.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    View view;
    public GroupsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_groups, container, false);
        configureFab();
        activity = getActivity();
        groupList = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.friendsrecyclervirew);
        configureSwipeLayout();
        new SearchGroups().execute();
        return view;
    }
    public void configureFab(){
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.newgroupbtn));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(getContext(), AddGroupActivity.class);
                startActivity(intent);
            }
        });
    }
    class SearchGroups extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
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

                groupList = new ArrayList<>();
                if (success == 1) {
                    JSONArray groups = json.getJSONArray("groups");
                    Group group = null;
                    for (int i = 0; i< groups.length();i++) {
                        String groupName = ((JSONObject) (groups.get(i))).getString("name");
                        String groupPicture = ((JSONObject) (groups.get(i))).getString("grouppic");
                        String groupId = ((JSONObject) (groups.get(i))).getString("groupid");
//                        ArrayList<User> userList = new ArrayList<>();
//                        JSONArray users = ((JSONObject) (groups.get(i))).getJSONArray("members");
//                        User user = null;
//                        for (int j = 0; i < users.length(); i++) {
//                            String id = ((JSONObject) (users.get(j))).getString("id");
//                            String firstname = ((JSONObject) (users.get(j))).getString("first_name");
//                            String lastname = ((JSONObject) (users.get(j))).getString("last_name");
//                            String email = ((JSONObject) (users.get(j))).getString("email");
//                            String phone = ((JSONObject) (users.get(j))).getString("phone");
//                            String picture = ((JSONObject) (users.get(j))).getString("picture");
//                            double longitude = ((JSONObject) (users.get(i))).getDouble("longitude");
//                            double lattitude = ((JSONObject) (users.get(i))).getDouble("lattitude");
//                            long time = ((JSONObject) (users.get(i))).getLong("time");
//                            user = new User(id, firstname, lastname, email, phone, picture, longitude, lattitude, time);
//                            userList.add(user);
//                        }
                        group = new Group(groupName,groupId,groupPicture,null);
                        groupList.add(group);
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
            adapter = new GroupsRecyclerAdaptor(groupList,getActivity());
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
        new SearchGroups().execute();
    }
}
