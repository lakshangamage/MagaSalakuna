package com.project_maga_salakuna.magasalakuna.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.project_maga_salakuna.magasalakuna.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    View view = null;
    Activity activity;
    MaterialSearchView searchView;
    Toolbar searchBar;
    Boolean isMenuInflated = false;
    private ProgressDialog pDialog;
    String searchString="";

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
        String []suggestions = {"Amal","Kamal", "Nimal", "Pasindu", "Lakshan"};
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        activity = getActivity();
        searchBar = (Toolbar) view.findViewById(R.id.searchtoolbar);
        searchView = (MaterialSearchView) view.findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        searchView.setVoiceIcon(getResources().getDrawable(R.drawable.ic_action_voice_search));
        searchView.showVoice(true);
        searchView.setSuggestions(suggestions);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchString = query;
                new SearchFriends().execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getContext(), "query text CHANGED", Toast.LENGTH_SHORT);
                return false;
            }
        });
        // Inflate the layout for this fragment
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Toast.makeText(getContext(), "SearchView Opened", Toast.LENGTH_SHORT);
            }

            @Override
            public void onSearchViewClosed() {
                Toast.makeText(getContext(), "SearchView Closed", Toast.LENGTH_SHORT);
            }
        });
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isMenuInflated){
            searchBar.inflateMenu(R.menu.searchmenu);
            //inflater.inflate(R.menu.searchmenu,searchBar.getMenu());
            //inflater.inflate(R.menu.searchmenu, menu);
            MenuItem item = searchBar.getMenu().findItem(R.id.action_search);
            searchView.setMenuItem(item);
            isMenuInflated = true;
        }

    }



    class SearchFriends extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    JSONObject user = json.getJSONObject("user");
                    String firstname = user.getString("firstname");
                    String lastname = user.getString("lastname");
                    String email = user.getString("email");
                    String phone = user.getString("phone");
                    String picture = user.getString("picture");
                    
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

        }
    }



}
