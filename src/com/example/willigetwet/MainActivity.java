package com.example.willigetwet;

import com.example.willigetwet.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private LocationClient mLocationClient;    // Global variable to hold the current location
    private Location mCurrentLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLocationClient = new LocationClient(this, this, this);
		   
		
//		TextView textView = new TextView(this);
//		textView.setTextSize(40);
//		textView.setText("Bar");
        setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
        mLocationClient.connect();
	}
	
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
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
//		mCurrentLocation = mLocationClient.getLastLocation();
//
    	TextView textView = (TextView) findViewById(R.id.latitude_text);
    	textView.setText("Hello");
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
	}

}
