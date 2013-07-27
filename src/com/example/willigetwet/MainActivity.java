package com.example.willigetwet;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class MainActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	/** LocationClient */
	private LocationClient mLocationClient;

	/** Most current location */
	private Location mCurrentLocation;

	/** Progress bar to spin whilst locating address */
	private ProgressBar mActivityIndicator;

	/** Address text box */
	private TextView mAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mLocationClient = new LocationClient(this, this, this);
		mActivityIndicator = (ProgressBar) findViewById(R.id.address_progress);
		mAddress = (TextView) findViewById(R.id.address);
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

	/**
	 * 
	 * When connected shows lattitude, longitude and the address Uses
	 * {@link GetAddressTask} in the background to load the address since it
	 * takes time, therefore activates the progress bar for the time being until
	 * GetAddressTask de-activates it
	 * 
	 * The location manager requestLocationUpdates is a fix due to Samsung GPS
	 * issues. {@link http
	 * ://stackoverflow.com/questions/16807986/gps-locations-are
	 * -not-retrieved-properly}
	 * 
	 * 
	 * @param bundle
	 * 
	 */
	@Override
	public void onConnected(Bundle bundle) {
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

		if (mCurrentLocation != null) {
			TextView latTextView = (TextView) findViewById(R.id.latitude_text);
			latTextView
					.setText(Double.toString(mCurrentLocation.getLatitude()));

			TextView longTextView = (TextView) findViewById(R.id.longitude_text);
			longTextView.setText(Double.toString(mCurrentLocation
					.getLongitude()));

			// Ensure that a Geocoder services is available
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
					&& Geocoder.isPresent()) {
				// Show the activity indicator
				mActivityIndicator.setVisibility(View.VISIBLE);
				/*
				 * Reverse geocoding is long-running and synchronous. Run it on
				 * a background thread. Pass the current location to the
				 * background task. When the task finishes, onPostExecute()
				 * displays the address.
				 */
				(new GetAddressTask(this)).execute(mCurrentLocation);
			}
		}

	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * A subclass of AsyncTask that calls getFromLocation() in the background.
	 * The class definition has these generic types: Location - A Location
	 * object containing the current location. Void - indicates that progress
	 * units are not used String - An address passed to onPostExecute()
	 */
	private class GetAddressTask extends AsyncTask<Location, Void, String> {
		Context mContext;

		public GetAddressTask(Context context) {
			super();
			mContext = context;
		}

		/**
		 * Get a Geocoder instance, get the latitude and longitude look up the
		 * address, and return it
		 * 
		 * @params params One or more Location objects
		 * @return A string containing the address of the current location, or
		 *         an empty string if no address can be found, or an error
		 *         message
		 */
		@Override
		protected String doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			// Get the current location from the input parameter list
			Location loc = params[0];
			// Create a list to contain the result address
			List<Address> addresses = null;
			try {
				/*
				 * Return 1 address.
				 */
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e1) {
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("IO Exception trying to get address");
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments "
						+ Double.toString(loc.getLatitude()) + " , "
						+ Double.toString(loc.getLongitude())
						+ " passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);
				/*
				 * Format the first line of address (if available), city, and
				 * country name.
				 */
				String addressText = String.format(
						"%s, %s, %s",
						// If there's a street address, add it
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "",
						// Locality is usually a city
						address.getLocality(),
						// The country of the address
						address.getCountryName());
				// Return the text
				return addressText;
			} else {
				return "No address found";
			}
		}

		/**
		 * A method that's called once doInBackground() completes. Turn off the
		 * indeterminate activity indicator and set the text of the UI element
		 * that shows the address. If the lookup failed, display the error
		 * message.
		 */
		@Override
		protected void onPostExecute(String address) {
			// Set activity indicator visibility to "gone"
			mActivityIndicator.setVisibility(View.GONE);
			// Display the results of the lookup.
			mAddress.setText(address);
		}
	}

}
