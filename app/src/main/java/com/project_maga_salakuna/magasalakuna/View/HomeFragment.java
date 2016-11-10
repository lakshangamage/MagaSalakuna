package com.project_maga_salakuna.magasalakuna.View;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project_maga_salakuna.magasalakuna.Controller.FriendListRecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Controller.JSONParser;
import com.project_maga_salakuna.magasalakuna.Model.CheckIn;
import com.project_maga_salakuna.magasalakuna.Model.CircleView;
import com.project_maga_salakuna.magasalakuna.Model.User;
import com.project_maga_salakuna.magasalakuna.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private boolean mapUpdated = false;
    private MapView mMapView;
    private IMapController mMapController;
    public static double currentxCoordinates;
    public static double currentyCoordinates;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ProgressDialog progressDialog;
    private boolean locationSet = false;
    private GeoPoint userLocation;
    private Activity activity;
    private CircleView circleView;
    private TextView distanceText;
    private SeekBar seekBar;
    FloatingActionButton fab;
    FloatingActionButton refreshfab;
    public ArrayList<CheckIn> checkIns;
    public ArrayList<User> friendList= new ArrayList<>();
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    private static final String SEARCH_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/friendlist.php";
    private static final String updateURL = "http://176.32.230.51/pathmila.com/maga_salakuna/lastseen.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    FolderOverlay poiMarkers;
    boolean locationFound = false;
    public ArrayList<User> currentUsersInRange = new ArrayList<>();
    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();
        poiMarkers = new FolderOverlay(activity);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        checkIns = new ArrayList<>();
        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        //mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.checkinbtnicon));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(activity, AddCheckInActivity.class);
                startActivity(intent);
            }
        });
        refreshfab = (FloatingActionButton) view.findViewById(R.id.refreshfab);
        refreshfab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.refreshbtn));
        refreshfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                new SearchFriends().execute();
            }
        });
        mMapController = mMapView.getController();
        mMapController.setZoom(18);
        new SearchFriends().execute();
        circleView = (CircleView) view.findViewById(R.id.circle_drawer_view);
        seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        distanceText = (TextView) view.findViewById(R.id.distanceText);
        seekBar.setMax( circleView.getBounds() );
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                circleView.resizeCircle(i);
                GeoPoint circleCentrePoint = geoPointFromScreenCoords(circleView.getMiddleX(),circleView.getMiddleY(),mMapView);
                GeoPoint farestPoint = geoPointFromScreenCoords(circleView.getRightVal(),circleView.getMiddleY(),mMapView);
                double maxDistance = distFrom(circleCentrePoint.getLatitude(),circleCentrePoint.getLongitude(),farestPoint.getLatitude(),farestPoint.getLongitude());
                int maxDis = (int) maxDistance;
                if (maxDis>=1000){
                    distanceText.setText(String.valueOf(maxDis/1000) + "km");
                }else{
                    distanceText.setText(String.valueOf(maxDis) + "m");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                distanceText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distanceText.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void getLocation() {
        locationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationFound = true;
                currentxCoordinates = location.getLatitude();
                currentyCoordinates = location.getLongitude();
                hideDialog();
//                Toast toast = Toast.makeText(getContext(), "Location Changed", Toast.LENGTH_SHORT);
//                toast.show();
                new UpdateLoationRemote().execute();
                updateMap();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 10);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        progressDialog.setMessage("Waiting for Location...");
        showDialog();
        Timer timer = new Timer();
        if (!locationFound) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!locationSet) {
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
                    }
                }
            }, 5000);
        }

    }
    private void updateMap() {
        if (!locationSet) {
            userLocation = new GeoPoint(currentxCoordinates, currentyCoordinates);
            /*myItemizedOverlay.addItem(userLocation, "You", "You");
            myPath.addPoint(userLocation);
            myPath.addPoint(userLocation);*/
            //mMapView.getOverlays().add(myPath);
            mMapView.getOverlays().remove(poiMarkers);
            poiMarkers = new FolderOverlay(activity);

            for (User user : currentUsersInRange) {
                Marker poiMarker = new Marker(mMapView);
                poiMarker.setTitle(user.getFirstName() + " " + user.getLastName());
                if(user.getPicture() !=null){
                    byte[] decodedString = Base64.decode(user.getPicture(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Drawable d = new BitmapDrawable(getResources(), decodedByte);
                    poiMarker.setImage(d);
                }
                poiMarker.setIcon(getResources().getDrawable(R.drawable.mappointer));
                poiMarker.setSnippet(timeConversion(user.getTime()));
                poiMarker.setPosition(new GeoPoint(user.getLslattitude(), user.getLslongitude()));
                poiMarkers.add(poiMarker);
            }
            Marker endMarker = new Marker(mMapView);
            endMarker.setPosition(userLocation);
            endMarker.setTitle("You Are Here");
            endMarker.setIcon(getResources().getDrawable(R.drawable.mypinpointer));
            endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            poiMarkers.add(endMarker);
            mMapView.getOverlays().add(poiMarkers);
            mMapController.setCenter(userLocation);
        }
        locationSet = true;
    }
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    class GetCheckins extends AsyncTask<String, String, String> {

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
                    }
                    return json.getString(TAG_MESSAGE);
                } else {
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
//            if (file_url != null) {
//                Toast.makeText(activity, file_url, Toast.LENGTH_LONG).show();
//            }
            if (!mapUpdated) {

                getLocation();
                mapUpdated = true;
                // Inflate the layout for this fragment
            } else {
                locationSet = false;
                updateMap();
            }
        }
    }
    class RefreshMap extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Looking for near by friends...");
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
                    }

                    System.out.println("Ckeck ins size: ===========================" + checkins.length());
                    return json.getString(TAG_MESSAGE);
                } else {
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
            if (file_url != null) {
                Toast.makeText(activity, file_url, Toast.LENGTH_LONG).show();
            }
            if (!mapUpdated) {

                getLocation();
                mapUpdated = true;
                // Inflate the layout for this fragment
            } else {
                locationSet = false;
                updateMap();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            Timer timer = new Timer();
            if (!locationFound) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!locationSet) {
                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                                    PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                            PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
                        }
                    }
                }, 5000);
            }
        }
    }
    public void refreshMapRange(){
        GeoPoint circleCentrePoint = geoPointFromScreenCoords(circleView.getMiddleX(),circleView.getMiddleY(),mMapView);
        GeoPoint farestPoint = geoPointFromScreenCoords(circleView.getRightVal(),circleView.getMiddleY(),mMapView);
        double maxDistance = distFrom(circleCentrePoint.getLatitude(),circleCentrePoint.getLongitude(),farestPoint.getLatitude(),farestPoint.getLongitude());
        userLocation = new GeoPoint(currentxCoordinates, currentyCoordinates);
        mMapView.getOverlays().remove(poiMarkers);
        poiMarkers = new FolderOverlay(activity);
        currentUsersInRange = new ArrayList<>();
        for (User user : friendList) {
            double userdis = distFrom(circleCentrePoint.getLatitude(),circleCentrePoint.getLongitude(),user.getLslattitude(),user.getLslongitude());
            if (userdis <= maxDistance) {
                currentUsersInRange.add(user);
                Marker poiMarker = new Marker(mMapView);
                poiMarker.setTitle(user.getFirstName() + " " + user.getLastName());
                if (user.getPicture() != null) {
                    byte[] decodedString = Base64.decode(user.getPicture(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Drawable d = new BitmapDrawable(getResources(), decodedByte);
                    poiMarker.setImage(d);
                }
                poiMarker.setIcon(getResources().getDrawable(R.drawable.mappointer));
                poiMarker.setSnippet(timeConversion(user.getTime()));
                poiMarker.setPosition(new GeoPoint(user.getLslattitude(), user.getLslongitude()));
                poiMarkers.add(poiMarker);
            }
        }
        Marker endMarker = new Marker(mMapView);
        endMarker.setPosition(userLocation);
        endMarker.setTitle("You Are Here");
        endMarker.setTextLabelBackgroundColor(Color.TRANSPARENT);
        endMarker.setIcon(getResources().getDrawable(R.drawable.mypinpointer));
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        poiMarkers.add(endMarker);
        mMapView.getOverlays().add(poiMarkers);
        mMapView.invalidate();
    }
    private GeoPoint geoPointFromScreenCoords(int x, int y, MapView vw){
        if (x < 0 || y < 0 || x > vw.getWidth() || y > vw.getHeight()){
            return null; // coord out of bounds
        }
        // Get the top left GeoPoint
        Projection projection = vw.getProjection();
        GeoPoint geoPointTopLeft = (GeoPoint) projection.fromPixels(0, 0);
        Point topLeftPoint = new Point();
        // Get the top left Point (includes osmdroid offsets)
        projection.toPixels(geoPointTopLeft, topLeftPoint);
        // get the GeoPoint of any point on screen
        GeoPoint rtnGeoPoint = (GeoPoint) projection.fromPixels(x, y);
        return rtnGeoPoint;
    }
    public double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (float) (earthRadius * c);

        return dist;
    }
//    public void updateLocationInRemote(){
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                new UpdateLoationRemote().execute();
//            }
//        },5000,60000*5);
//    }
    public class UpdateLoationRemote extends AsyncTask<String, String, String> {
        String id;
        long time = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            id = MainActivity.id;
            time = System.currentTimeMillis();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", id));
                params.add(new BasicNameValuePair("xCordinates", String.valueOf(currentxCoordinates)));
                params.add(new BasicNameValuePair("yCordinates", String.valueOf(currentyCoordinates)));
                params.add(new BasicNameValuePair("time", String.valueOf(time)));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        updateURL, "POST", params);

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

        }
    }
    class SearchFriends extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
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
                        double longitude = ((JSONObject) (users.get(i))).getDouble("longitude");
                        double lattitude = ((JSONObject) (users.get(i))).getDouble("latitude");
                        long time = ((JSONObject) (users.get(i))).getLong("time");
                        user = new User(id, firstname, lastname, email, phone, picture, longitude, lattitude, time);
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
                Toast.makeText(getContext(), "No Friends", Toast.LENGTH_LONG).show();
            }//pDialog.dismiss();
//            if (file_url != null) {
//                Toast.makeText(activity, file_url, Toast.LENGTH_LONG).show();
//            }

//            if (!mapUpdated) {
//
//                getLocation();
//                mapUpdated = true;
//                // Inflate the layout for this fragment
//            } else {
//                locationSet = false;
//                updateMap();
//            }
            if (!locationFound){
                getLocation();
            }
            refreshMapRange();
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.refresh(currentUsersInRange);
            pDialog.dismiss();
        }
    }
    public String timeConversion(long time){
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - time;
        String output="Last Seen ";
        if (diff < 1000*60*5){
            output += String.valueOf("Now");
        }else if (diff < 1000*60*60){
            output += String.valueOf(diff/(1000*60)) + "m ago";
        }else if (diff < 1000*60*60*24){
            output += String.valueOf(diff/(1000*60*60)) + "h ago";
        }else if (diff < 1000*60*60*24*31){
            output += String.valueOf(diff/(1000*60*60*24)) + "days ago";
        }else{
            output += String.valueOf("more than a month ago");
        }
        return output;
    }

}
