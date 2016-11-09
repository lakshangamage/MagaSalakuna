package com.project_maga_salakuna.magasalakuna.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.project_maga_salakuna.magasalakuna.Controller.FriendRequestsRecyclerAdaptor;
import com.project_maga_salakuna.magasalakuna.Controller.JSONParser;
import com.project_maga_salakuna.magasalakuna.Model.CheckIn;
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
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class GroupMapViewActivity extends AppCompatActivity {
    private String groupid;
    private String pic;
    private String groupname;
    private CircularImageView circularImageView;
    private Toolbar toolbar;
    ArrayList<User> memberList;
    JSONParser jsonParser = new JSONParser();
    private static final String SEARCH_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/getgroupmembers.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private Context context = this;
    FolderOverlay poiMarkers;
    private MapView mMapView;
    private IMapController mMapController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_map_view);
        configureToolbar();
        groupid = getIntent().getStringExtra("id");
        pic = getIntent().getStringExtra("pic");
        groupname = getIntent().getStringExtra("name");
        setImage();
        new getMembers().execute();
    }
    class getMembers extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", groupid));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        SEARCH_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);

                memberList = new ArrayList<>();
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
                        double longitude = ((JSONObject) (users.get(i))).getDouble("longitude");
                        double lattitude = ((JSONObject) (users.get(i))).getDouble("lattitude");
                        long time = ((JSONObject) (users.get(i))).getLong("time");
                        user = new User(id, firstname, lastname, email, phone, picture, longitude, lattitude, time);
                        memberList.add(user);
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
                Toast.makeText(context, "No Group Members", Toast.LENGTH_LONG).show();
            }
            updateMap(memberList);
        }
    }

    private void updateMap(ArrayList<User> users) {
            mMapView = (MapView)findViewById(R.id.mapview);
            mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
            mMapView.setBuiltInZoomControls(true);
            mMapView.setMultiTouchControls(true);
            mMapController = mMapView.getController();
            mMapController.setZoom(18);
            poiMarkers = new FolderOverlay(context);

            for (User user : users) {
                Marker poiMarker = new Marker(mMapView);
                poiMarker.setTitle(user.getFirstName() + " " + user.getLastName());

                poiMarker.setSnippet(timeConversion(user.getTime()));
                poiMarker.setPosition(new GeoPoint(user.getLslattitude(), user.getLslongitude()));
                poiMarkers.add(poiMarker);
            }
        GeoPoint userLocation =new GeoPoint(HomeFragment.currentxCoordinates,HomeFragment.currentyCoordinates);
            Marker endMarker = new Marker(mMapView);
            endMarker.setPosition(userLocation);
            endMarker.setTitle("You Are Here");
            endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            poiMarkers.add(endMarker);
            mMapView.getOverlays().add(poiMarkers);
            mMapController.setCenter(userLocation);
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
    public void configureToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(groupname);
        setSupportActionBar(toolbar);
    }
    public void setImage(){
        if(pic !=null){
            circularImageView = (CircularImageView) findViewById(R.id.headerimage);
            byte[] decodedString = Base64.decode(pic, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            circularImageView.setBackgroundResource(0);
            circularImageView.setImageBitmap(decodedByte);
        }
    }
}
