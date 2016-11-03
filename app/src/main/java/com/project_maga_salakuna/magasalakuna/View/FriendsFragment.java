package com.project_maga_salakuna.magasalakuna.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project_maga_salakuna.magasalakuna.Controller.JSONParser;
import com.project_maga_salakuna.magasalakuna.Controller.RecyclerAdaptor;
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
    public ArrayList<User> friendList=null;
    public ArrayList<User> searchList=null;
    View view = null;
    Activity activity;
    MaterialSearchView searchView;
    Toolbar searchBar;
    Boolean isMenuInflated = false;
    private ProgressDialog pDialog;
    String searchString="";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    JSONParser jsonParser = new JSONParser();

    private static final String SEARCH_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/searchfriends.php";

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
        String []suggestions = {"Amal","Kamal", "Nimal", "Pasindu", "Lakshan", "pasindu"};
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        activity = getActivity();
        friendList = new ArrayList<>();
        searchList = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.friendsrecyclervirew);
        searchBar = (Toolbar) view.findViewById(R.id.searchtoolbar);
//        searchView = (MaterialSearchView) view.findViewById(R.id.search_view);
//        searchView.setVoiceSearch(true);
//        searchView.setVoiceIcon(getResources().getDrawable(R.drawable.ic_action_voice_search));
//        searchView.showVoice(true);
//        searchView.setSuggestions(suggestions);
//        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchString = query;
//                new SearchFriends().execute();
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Toast.makeText(getContext(), "query text CHANGED", Toast.LENGTH_SHORT);
//                return false;
//            }
//        });
//        // Inflate the layout for this fragment
//        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
//            @Override
//            public void onSearchViewShown() {
//                Toast.makeText(getContext(), "SearchView Opened", Toast.LENGTH_SHORT);
//            }
//
//            @Override
//            public void onSearchViewClosed() {
//                Toast.makeText(getContext(), "SearchView Closed", Toast.LENGTH_SHORT);
//            }
//        });
        return view;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }





    class SearchFriends extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Searching...");
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
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("name", searchString));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        SEARCH_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
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
                Toast.makeText(activity, file_url, Toast.LENGTH_LONG).show();
            }
            adapter = new RecyclerAdaptor(searchList);
            recyclerView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
        }
    }



}
