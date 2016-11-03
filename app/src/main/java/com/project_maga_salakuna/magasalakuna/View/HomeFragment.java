package com.project_maga_salakuna.magasalakuna.View;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project_maga_salakuna.magasalakuna.Controller.JSONParser;
import com.project_maga_salakuna.magasalakuna.Controller.RecyclerAdaptor;
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
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.PathOverlay;

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
    private SeekBar seekBar;
    FloatingActionButton fab;
    FloatingActionButton refreshfab;
    public ArrayList<CheckIn> checkIns;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    private static final String SEARCH_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/getcheckins.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        checkIns = new ArrayList<>();
        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
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
                Intent intent = new Intent(activity, AddCheckInActivity.class);
                startActivity(intent);
            }
        });
        mMapController = mMapView.getController();
        mMapController.setZoom(18);
        new GetCheckins().execute();
        circleView = (CircleView) view.findViewById(R.id.circle_drawer_view);
        seekBar = (SeekBar) view.findViewById(R.id.seekbar);

        seekBar.setMax( circleView.getBounds() );
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                circleView.resizeCircle(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                currentxCoordinates = location.getLatitude();
                currentyCoordinates = location.getLongitude();
                //hideDialog();
//                Toast toast = Toast.makeText(getContext(), "Location Changed", Toast.LENGTH_SHORT);
//                toast.show();
                updateMap(checkIns);
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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
//        progressDialog.setMessage("Waiting for Location...");
//        showDialog();
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if(!locationSet){
//                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
//                            PackageManager.PERMISSION_GRANTED &&
//                            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                                    PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
//                }
//            }
//        },10000);

    }

    private void updateMap(ArrayList<CheckIn> checkIns) {
        if (!locationSet) {
            userLocation = new GeoPoint(currentxCoordinates, currentyCoordinates);
            /*myItemizedOverlay.addItem(userLocation, "You", "You");
            myPath.addPoint(userLocation);
            myPath.addPoint(userLocation);*/
            //mMapView.getOverlays().add(myPath);
            FolderOverlay poiMarkers = new FolderOverlay(activity);

            for (CheckIn checkIn : checkIns) {
                Marker poiMarker = new Marker(mMapView);
                poiMarker.setTitle(checkIn.getUser().getFirstName() + " " + checkIn.getUser().getLastName());
                poiMarker.setSnippet(checkIn.getStatus() + " @ " + checkIn.getAt());
                poiMarker.setPosition(new GeoPoint(checkIn.getLattitude(), checkIn.getLongitude()));
                poiMarkers.add(poiMarker);
            }
            Marker endMarker = new Marker(mMapView);
            endMarker.setPosition(userLocation);
            endMarker.setTitle("You Are Here");
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
                updateMap(checkIns);
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
                updateMap(checkIns);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }
    }
}
