//package com.hoverslamstudios.splurge;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//
///**
// * Created by Jordan Davis on 5/14/2017.
// */
//
//public class HttpGooglePlacesRequest {
//	// example
//	// https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=YOUR_API_KEY
//
//	final String GOOGLE_API_URL = "https://maps.googleapis.com/maps/place/nearbysearch/json";
//	final String GOOGLE_API_KEY = "AIzaSyCmQ5BBi-AJ7sY2w8JsicR00FjZHFB8nCo";
//
//
//		// Instantiate the RequestQueue.
//		RequestQueue queue = Volley.newRequestQueue();
//
//// Request a string response from the provided URL.
//		StringRequest stringRequest = new StringRequest(Request.Method.GET, GOOGLE_API_URL,
//				new Response.Listener<String>() {
//					@Override
//					public void onResponse(String response) {
//						// Display the first 500 characters of the response string.
//						response.substring(0,500);
//					}
//				}, new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError error) {
//
//			}
//		});
//// Add the request to the RequestQueue.
//		queue.add(stringRequest);
//	}
//
//}
