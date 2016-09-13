package com.project_maga_salakuna.magasalakuna.View;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.project_maga_salakuna.magasalakuna.Controller.ViewPagerAdaptor;
import com.project_maga_salakuna.magasalakuna.R;

import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.PathOverlay;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Intent intent;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    NavigationView navigationView;
    ViewPagerAdaptor viewPagerAdaptor;

    private MapView mMapView;
    private IMapController mMapController;
    private double xCoordinates;
    private double yCoordinates;
    private double currentxCoordinates;
    private double currentyCoordinates;
    private String name;
    //MyItemizedOverlay myItemizedOverlay = null;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ProgressDialog progressDialog;
    private boolean locationSet = false;
    private Context context;
    private PathOverlay myPath;
    private GeoPoint shopLocation;
    private GeoPoint userLocation;
    RoadManager roadManager;
    Road road;
    private TextView distanceTxt;
    private TextView duratioTxt;
    View headerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        intent = this.getIntent();
        viewPagerAdaptor = new ViewPagerAdaptor(getSupportFragmentManager());

        viewPagerAdaptor.addFragments(new HomeFragment(),"Home");
        viewPagerAdaptor.addFragments(new CheckInFragment(),"Check Ins");
        viewPagerAdaptor.addFragments(new FriendsFragment(),"Friends");
        viewPagerAdaptor.addFragments(new EventsFragment(),"Events");
        viewPager.setAdapter(viewPagerAdaptor );
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);

        /*progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        Fragment frag = homeFragment;
        mMapView = (MapView) frag.getView().findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        context = this;

        shopLocation = new GeoPoint(xCoordinates, yCoordinates);
        mMapController = mMapView.getController();
        mMapController.setZoom(14);
        mMapController.setCenter(shopLocation);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(15);
        mMapController.setCenter(shopLocation);
        roadManager = new OSRMRoadManager(this);
        getLocation();*/



        navigationView = (NavigationView) findViewById(R.id.navogation_view);
        headerView = navigationView.inflateHeaderView(R.layout.drawerheader);
        imageView = (ImageView) headerView.findViewById(R.id.headerimage);
        /*
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
        }*/


    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
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
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    public void logout(MenuItem item){
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
