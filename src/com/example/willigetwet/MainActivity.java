package com.example.willigetwet;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class MainActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		LocationListener {

	private LocationClient mLocationClient;
	private Location mCurrentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mLocationClient = new LocationClient(this, this, this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle arg0) {
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		LocationManager manager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
				new LocationListener() {
					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}

					@Override
					public void onProviderEnabled(String provider) {
					}

					@Override
					public void onProviderDisabled(String provider) {
					}

					@Override
					public void onLocationChanged(final Location location) {
					}
				});
		mCurrentLocation = mLocationClient.getLastLocation();

		if (mCurrentLocation != null)
		{
			TextView latTextView = (TextView) findViewById(R.id.latitude_text);
			latTextView.setText(Double.toString(mCurrentLocation.getLatitude()));
	
			TextView longTextView = (TextView) findViewById(R.id.longitude_text);
			longTextView.setText(Double.toString(mCurrentLocation.getLongitude()));
		}
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onLocationChanged(final Location location) {
	    mCurrentLocation = location;
		if (mCurrentLocation != null) {
			TextView latTextView = (TextView) findViewById(R.id.latitude_text);
			latTextView
					.setText(Double.toString(mCurrentLocation.getLatitude()));

			TextView longTextView = (TextView) findViewById(R.id.longitude_text);
			longTextView.setText(Double.toString(mCurrentLocation
					.getLongitude()));
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
