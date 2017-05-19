package com.hoverslamstudios.splurge;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Place> nearbyPlaceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        performPlacesRequest();
    }

    private void performPlacesRequest() {
        // example
        // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=YOUR_API_KEY
        final String GOOGLE_API_KEY = "AIzaSyCmQ5BBi-AJ7sY2w8JsicR00FjZHFB8nCo";
        final String priceLevel;
        final String radius;
        final String foodURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=40.5853,-105.0844&" +
                "maxPriceLevel=4&" +
                "radius=50000&" +
                "type=restaurant&" +
                "key=" + GOOGLE_API_KEY;



        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, foodURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray object = response.getJSONArray("results");
                            parseObjectArray(object);
                        } catch (JSONException ex) {
                            //?
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    private void parseObjectArray(JSONArray jsonArray) {
        nearbyPlaceList = new ArrayList<Place>();


        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                boolean isLodging = false;
                JSONObject placeJSON = jsonArray.getJSONObject(i);
                Place place = new Place();
                place.latitude = placeJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
                place.longitude = placeJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
                place.name = placeJSON.has("name") ? placeJSON.getString("name") : "";
                place.placeId = placeJSON.has("place_id") ? placeJSON.getString("place_id") : "";
                place.rating = placeJSON.has("rating") ? placeJSON.getInt("rating") : 0;
                place.priceLevel = placeJSON.has("price_level") ? placeJSON.getInt("price_level") : 0;
                place.address = placeJSON.has("vicinity") ? placeJSON.getString("vicinity") : "";
                JSONArray tempArray = placeJSON.has("types") ?  placeJSON.getJSONArray("types") : null;

                if(tempArray != null) {
                    for(int x = 0; x < tempArray.length(); x++) {
                        place.types.add(tempArray.getString(x));
                    }
                }

                for(int j = 0; j < place.types.size(); j++) {
                    if(place.types.get(j).equals("lodging")) {
                        isLodging = true;
                    }
                }

                if(!isLodging) {
                    nearbyPlaceList.add(place);
                }
            }
        } catch (JSONException ex) {

        }
    }
}
