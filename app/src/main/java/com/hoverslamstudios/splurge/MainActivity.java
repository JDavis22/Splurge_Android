package com.hoverslamstudios.splurge;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity
        extends AppCompatActivity
        implements com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String GOOGLE_API_KEY = "AIzaSyCmQ5BBi-AJ7sY2w8JsicR00FjZHFB8nCo";
    private static final String AD_UNIT_ID = "ca-app-pub-6378196838372847/2222869010";
    private static final String APP_ID = "";
    private TextInputEditText locationEditText;
    private TextView restaurantText;
    private TextView restaurantWebsiteText;
    private TextView restaurantRatingText;
    private TextView restaurantPriceLevelText;
    private NativeExpressAdView adNativeView;
    private ProgressBar progressBar;
    private ArrayList<Place> nearbyPlaceList;
    private JSONArray nearbySearchResults;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Place currentPlace;
    private String latitude;
    private String longitude;

    public enum REQUEST_TYPE {
        PLACE_DETAILS,
        PLACE_SEARCH
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bindViews();

        MobileAds.initialize(this, AD_UNIT_ID);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice()
                .build();
        adNativeView.loadAd(adRequest);

        checkPermissions();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void bindViews() {
        Button submitButton = (Button) findViewById(R.id.searchButton);
        submitButton.setOnClickListener(submitButtonClickListener);

        Button getLocationButton = (Button) findViewById(R.id.getLocationButton);
        getLocationButton.setOnClickListener(getLocationClickListener);

        locationEditText = (TextInputEditText) findViewById(R.id.locationEditText);

        restaurantText = (TextView) findViewById(R.id.restaurantTitleText);
        restaurantText.setOnClickListener(restaurantClickListener);

        restaurantPriceLevelText = (TextView) findViewById(R.id.restaurantPriceLevelText);
        restaurantRatingText = (TextView) findViewById(R.id.restaurantRatingText);
        restaurantWebsiteText = (TextView) findViewById(R.id.restaurantWebsiteUrlText);
        restaurantWebsiteText.setOnClickListener(restaurantWebsiteClickListener);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        adNativeView = (NativeExpressAdView) findViewById(R.id.adNativeView);

    }

    View.OnClickListener submitButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!locationEditText.getText().toString().isEmpty()) {
                // show hide progress
                progressBar.setVisibility(View.VISIBLE);

                if (nearbySearchResults != null && nearbySearchResults.length() > 0) {
                    parsePlaceObject(null);
                } else {
                    performPlacesRequest();
                }
            }
        }
    };

    View.OnClickListener getLocationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // show hide progress
            progressBar.setVisibility(View.VISIBLE);
            getLastLocation();
        }
    };

    TextView.OnClickListener restaurantClickListener = new TextView.OnClickListener() {
        @Override
        public void onClick(View v) {
            // open map with address!
            if (currentPlace != null) {
                String url = currentPlace.mapsUrl;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        }
    };

    TextView.OnClickListener restaurantWebsiteClickListener = new TextView.OnClickListener() {
        @Override
        public void onClick(View v) {
            // open browser with website!
        }
    };

    private void startLocationUpdates() {
        checkPermissions();

        LocationRequest request = createLocationRequest();

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        } catch (SecurityException ex) {

        }
    }

    /**
     * LISTENER METHODS
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(50000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return mLocationRequest;
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // we have permissions...
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PermissionsHelper.LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionsHelper.LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    performPlacesRequest();
                } else if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Snackbar.make(findViewById(R.id.mainContentView), "Please enable location permissions in app settings", Snackbar.LENGTH_LONG)
                            .setAction("Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // settings intent
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                                }
                            }).show();
                }
            }
        }
    }

    private void getLastLocation() {
        checkPermissions();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
            Snackbar.make(findViewById(R.id.mainContentView), "Please enable GPS in settings", Snackbar.LENGTH_LONG)
                    .setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // settings intent
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        }
                    }).show();
        } else {
            try {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    latitude = String.valueOf(mLastLocation.getLatitude());
                    longitude = String.valueOf(mLastLocation.getLongitude());
                    locationEditText.setText("Current Location");

                    if (progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }
                } else if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);

                    Snackbar.make(findViewById(R.id.mainContentView), "Unable to get location", Snackbar.LENGTH_LONG).show();
                }
            } catch (SecurityException ex) {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    }

    private void performPlacesRequest() {
        // show hide progress bar
        String userLocation = getUserLocation();

        if (userLocation == null || userLocation.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            locationEditText.setText("");
            return;
        }

        final String radarSearchURL = "https://maps.googleapis.com/maps/api/place/radarsearch/json?" +
                "location=" + userLocation + "&" +
                "maxPriceLevel=4&" +
                "radius=16093&" +
                "type=restaurant&" +
                "key=" + GOOGLE_API_KEY;

        createVolleyRequest(radarSearchURL, REQUEST_TYPE.PLACE_SEARCH);
    }

    private String getUserLocation() {
        if (!locationEditText.getText().toString().isEmpty()) {
            if (locationEditText.getText().toString().equals("Current Location")) {
                return latitude + "," + longitude;
            } else {
                return getLatLng(locationEditText.getText().toString());
            }
        }

        return null;
    }

    private void createVolleyRequest(String url, final REQUEST_TYPE requestType) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            switch (requestType) {
                                case PLACE_SEARCH:
                                    JSONArray objects = response.getJSONArray("results");
                                    if (objects.length() > 1) {
                                        parsePlaceObject(objects);
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Snackbar.make(findViewById(R.id.mainContentView), "Could not retrieve data.", Snackbar.LENGTH_LONG).show();
                                    }
                                    break;
                                case PLACE_DETAILS:
                                    JSONObject object = response.getJSONObject("result");
                                    parsePlaceDetails(object);
                                    break;
                            }

                        } catch (JSONException ex) {
                            progressBar.setVisibility(View.GONE);
                            ex.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                error.printStackTrace();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    private String getLatLng(String searchAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Address location;

        try {
            address = coder.getFromLocationName(searchAddress, 1);
            if (address == null) {
                return null;
            }
            if (address.size() >= 1) {
                location = address.get(0);
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                return latitude + "," + longitude;
            } else {
                return "";
            }


        } catch (IOException ex) {
            locationEditText.setText(null);
            progressBar.setVisibility(View.GONE);
            ex.printStackTrace();
        }

        progressBar.setVisibility(View.GONE);
        return null;
    }

    private void parsePlaceObject(@Nullable JSONArray jsonArray) {
        if (nearbySearchResults == null && jsonArray != null) {
            nearbySearchResults = new JSONArray();
            nearbySearchResults = jsonArray;
        }

        try {
            if (nearbySearchResults != null && nearbySearchResults.length() > 0) {
                Random random = new Random();
                int index = random.nextInt(nearbySearchResults.length() - 1);
                JSONObject placeJSON = nearbySearchResults.getJSONObject(index);
                String placeId = placeJSON.getString("place_id");
                getPlaceDetails(placeId);
            }
        } catch (JSONException ex) {
            progressBar.setVisibility(View.GONE);
            ex.printStackTrace();
        }
    }

    private void parsePlaceDetails(JSONObject jsonObject) {
        try {
            boolean isLodging = false;

            Place place = new Place();

            place.latitude = jsonObject.optJSONObject("geometry").optJSONObject("location").optString("lat");
            place.latitude = jsonObject.optJSONObject("geometry").optJSONObject("location").optString("lng");
            place.name = jsonObject.optString("name");
            place.placeId = jsonObject.optString("place_id");
            place.rating = jsonObject.optInt("rating");
            place.priceLevel = jsonObject.optInt("price_level");
            place.address = jsonObject.optString("vicinity");
            place.website = jsonObject.optString("website");
            place.mapsUrl = jsonObject.optString("url");
            JSONObject temp = jsonObject.optJSONObject("opening_hours");
            place.isOpen = temp != null && temp.optBoolean("open_now");
            JSONArray tempArray = jsonObject.optJSONArray("types");

            if (tempArray != null) {
                for (int x = 0; x < tempArray.length(); x++) {
                    place.types.add(tempArray.getString(x));
                }
            }

            for (int j = 0; j < place.types.size(); j++) {
                if (place.types.get(j).equals("lodging")) {
                    isLodging = true;
                }
            }

            if (!isLodging && place.isOpen) {
                showRandomPlace(place);
            } else {
                parsePlaceObject(null);
            }
        } catch (JSONException ex) {
            progressBar.setVisibility(View.GONE);
            ex.printStackTrace();
        }
    }

    private void getPlaceDetails(String placeID) {
        final String placeDetailsRequestURL = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "placeid=" + placeID + "&" +
                "key=" + GOOGLE_API_KEY;

        createVolleyRequest(placeDetailsRequestURL, REQUEST_TYPE.PLACE_DETAILS);
    }

    private void showRandomPlace(Place place) {
        currentPlace = place;

        // Name of place, underlined, keep it clickable for map.
        restaurantText.setPaintFlags(restaurantText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        restaurantText.setText(currentPlace.name);

        // Rating
        restaurantRatingText.setText("Rating: " + currentPlace.rating);

        // Price level - make this customizable
        restaurantPriceLevelText.setText(currentPlace.getPriceLevelText(currentPlace.priceLevel));
        // Website
        restaurantWebsiteText.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='" + currentPlace.website + "'>Visit Website</a>";
        restaurantWebsiteText.setText(Html.fromHtml(text));

        progressBar.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
