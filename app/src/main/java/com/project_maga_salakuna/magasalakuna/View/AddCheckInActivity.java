package com.project_maga_salakuna.magasalakuna.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.project_maga_salakuna.magasalakuna.R;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.logging.Logger;

public class AddCheckInActivity extends AppCompatActivity {
    ImageView imageView = null;
    AutoCompleteTextView attext = null;
    EditText statusTxt  = null;
    ArrayList<POI> pois = null;
    ArrayAdapter<String> adapter;
    ArrayList<String> stringPois = null;
    NominatimPOIProvider poiProvider = null;
    Context context = null;
    ProgressDialog pDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_check_in);
        statusTxt = (EditText) findViewById(R.id.statusTxt);
        attext = (AutoCompleteTextView) findViewById(R.id.attxt);
        imageView = (ImageView) findViewById(R.id.profpicbtn);
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
}
