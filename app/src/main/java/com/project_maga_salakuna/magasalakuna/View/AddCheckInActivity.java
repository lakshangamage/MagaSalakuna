package com.project_maga_salakuna.magasalakuna.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.project_maga_salakuna.magasalakuna.Controller.JSONParser;
import com.project_maga_salakuna.magasalakuna.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class AddCheckInActivity extends AppCompatActivity {
    Button checkinbtn = null;
    ImageView imageView = null;
    AutoCompleteTextView attext = null;
    EditText statusTxt  = null;
    ArrayList<POI> pois = null;
    ArrayAdapter<String> adapter;
    ArrayList<String> stringPois = null;
    NominatimPOIProvider poiProvider = null;
    Context context = null;
    ProgressDialog pDialog = null;
    JSONParser jsonParser = new JSONParser();

    private static final String SEARCH_URL = "http://176.32.230.51/pathmila.com/maga_salakuna/addcheckin.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public double xCordinates;
    public double yCordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_check_in);
        statusTxt = (EditText) findViewById(R.id.statusTxt);
        attext = (AutoCompleteTextView) findViewById(R.id.attxt);
        imageView = (ImageView) findViewById(R.id.profpicbtn);
        checkinbtn = (Button) findViewById(R.id.checkinbtn);
        if(MainActivity.photo != null){
            byte[] decodedString = Base64.decode(MainActivity.photo, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setBackgroundResource(0);
            imageView.setImageBitmap(decodedByte);
        }
        context = this;
        attext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getNearBy().execute();
            }
        });
        checkinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddCheckIn().execute();
            }
        });
    }


    public class getNearBy extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Searching...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            poiProvider = new NominatimPOIProvider(null);
            pois = poiProvider.getPOICloseTo(new GeoPoint(HomeFragment.currentxCoordinates,HomeFragment.currentyCoordinates), "restaurants", 20, 0.5);
            System.out.println("POIS size = "+pois.size());
            stringPois = new ArrayList<>();
            for(POI poi : pois){
                //System.out.println("In");
                if(poi!=null){
                    //System.out.println("Not null");
                    stringPois.add(poi.mDescription);
                }

            }
            System.out.println(stringPois);
            adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, stringPois);

            return "Finished";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            attext.setAdapter(adapter);
            pDialog.dismiss();
        }
    }

    class AddCheckIn extends AsyncTask<String, String, String> {

        boolean failure = false;
        String status;
        String at;
        String id;
        public double xCordinates;
        public double yCordinates;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Ckecking In...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            status = statusTxt.getText().toString();
            at = attext.getText().toString();
            id = MainActivity.id;


            for(POI poi:pois){
                if(poi.mDescription.equalsIgnoreCase(at)){
                    xCordinates = poi.mLocation.getLongitude();
                    yCordinates = poi.mLocation.getLatitude();
                }
            }
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
                params.add(new BasicNameValuePair("status", status));
                params.add(new BasicNameValuePair("at", at));
                params.add(new BasicNameValuePair("xCordinates", String.valueOf(xCordinates)));
                params.add(new BasicNameValuePair("yCordinates", String.valueOf(yCordinates)));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        SEARCH_URL, "POST", params);

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

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(context, file_url, Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

}
