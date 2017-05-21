//package com.hoverslamstudios.splurge;
//
//import android.content.Context;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//
//import static android.content.Context.LOCATION_SERVICE;
//
///**
// * Created on 5/19/2017.
// */
//
//public final class LocationHelper implements LocationListener {
//	public interface OnLocationReceivedListener {
//		void onLocationReceived(Location location);
//
//		void onFailure();
//	}
//
//	private Context context;
//	private LocationHelper locationHelper;
//	private LocationManager locationManager;
//
//	public static LocationHelper getInstance() {
//		if (locationHelper == null) {
//			locationHelper = new LocationHelper();
//		}
//
//		return locationHelper;
//	}
//
//	public void getLocation(Context context) {
//		this.context = context;
//		checkPermission();
//		locationHelper = getInstance();
//		// getting GPS status
//		boolean checkGPS = locationManager
//				.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//		// getting network status
//		boolean checkNetwork = locationManager
//				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//		try {
//			LocationManager locationManager = (LocationManager) this.context
//					.getSystemService(LOCATION_SERVICE);
//
//			if (checkGPS) {
//				locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//			} else if (checkNetwork) {
//				locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//			} else {
//				// new snackbar AND ask to enable
//			}
//		} catch (Exception ex) {
//
//		}
//	}
//
//	private void checkPermission() {
//
//	}
//
//	@Override
//	public void onLocationChanged(Location location) {
//
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//
//	}
//}
