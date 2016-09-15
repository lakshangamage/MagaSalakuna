package com.project_maga_salakuna.magasalakuna.View;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project_maga_salakuna.magasalakuna.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.PathOverlay;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private boolean mapUpdated = false;
    private MapView mMapView;
    private IMapController mMapController;
    private double xCoordinates;
    private double yCoordinates;
    public static double currentxCoordinates;
    public static double currentyCoordinates;
    private String name;
    //MyItemizedOverlay myItemizedOverlay = null;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ProgressDialog progressDialog;
    private boolean locationSet = false;
    private PathOverlay myPath;
    private GeoPoint shopLocation;
    private GeoPoint userLocation;
    RoadManager roadManager;
    Road road;
    private TextView distanceTxt;
    private TextView duratioTxt;
    private Activity activity;
    FloatingActionButton fab;
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
        mMapController = mMapView.getController();
        mMapController.setZoom(18);
        if (!mapUpdated) {

            getLocation();
            mapUpdated=true;
            // Inflate the layout for this fragment
        }else{
            locationSet=false;
            updateMap();
        }
        return view;
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void getLocation(){
        locationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentxCoordinates = location.getLatitude();
                currentyCoordinates = location.getLongitude();
                hideDialog();
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
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET
            }, 10);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        progressDialog.setMessage("Waiting for Location...");
        showDialog();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!locationSet){
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                }
            }
        },10000);

    }
    private void updateMap() {
        if(!locationSet){
            userLocation = new GeoPoint(currentxCoordinates, currentyCoordinates);
            /*myItemizedOverlay.addItem(userLocation, "You", "You");
            myPath.addPoint(userLocation);
            myPath.addPoint(userLocation);*/
            //mMapView.getOverlays().add(myPath);
            Marker endMarker = new Marker(mMapView);
            endMarker.setPosition(userLocation);
            endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mMapView.getOverlays().add(endMarker);
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
}
