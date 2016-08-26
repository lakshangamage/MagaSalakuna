package com.project_maga_salakuna.magasalakuna.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project_maga_salakuna.magasalakuna.R;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    View view = null;
    Activity activity;
    MaterialSearchView searchView;
    Toolbar searchBar;
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
        searchView = (MaterialSearchView) view.findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        searchView.setSuggestions(suggestions);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // Inflate the layout for this fragment
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

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
        searchBar = (Toolbar) view.findViewById(R.id.searchtoolbar);
        searchBar.inflateMenu(R.menu.searchmenu);
        //inflater.inflate(R.menu.searchmenu,searchBar.getMenu());
        //inflater.inflate(R.menu.searchmenu, menu);
        MenuItem item = searchBar.getMenu().findItem(R.id.action_search);
        searchView.setMenuItem(item);
    }
}
