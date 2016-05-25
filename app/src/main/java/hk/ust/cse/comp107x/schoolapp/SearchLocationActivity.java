package hk.ust.cse.comp107x.schoolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import hk.ust.cse.comp107x.schoolapp.Singletons.Utils;

public class SearchLocationActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    LocationManager locationManager;
    String provider;
    LatLng latLng;

    AutoCompleteTextView mSearchLocation;
    private PlaceAutocompleteAdapter mAdapter;

    public void cancel(View view) {

        mSearchLocation.setText("");
    }
    public void getLocation(View view) {

        SharedPreferences preferences = getSharedPreferences("Location", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        double latitude = mMap.getCameraPosition().target.latitude;
        double longitude = mMap.getCameraPosition().target.longitude;

        editor.putString("latitude", Double.toString(latitude));
        editor.putString("longitude", Double.toString(longitude));
        editor.putString("Address", mSearchLocation.getText().toString());

        editor.commit();

        startActivity(new Intent(SearchLocationActivity.this, RegistrationActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        try {

            setUpMapIfNeeded();

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            provider = locationManager.getBestProvider(new Criteria(), false);

            Location location = locationManager.getLastKnownLocation(provider);

            if(location != null) {

                onLocationChanged(location);
            }

            mGoogleApiClient = buildApiClient();
            mGoogleApiClient.connect();

            mAdapter = new PlaceAutocompleteAdapter (getApplicationContext(), mGoogleApiClient, null,
                    null);
            mSearchLocation = (AutoCompleteTextView) findViewById(R.id.searchLocation);
            mSearchLocation.setAdapter(mAdapter);

            mSearchLocation.setOnItemClickListener(mAutocompleteClickListener);

        }catch (Exception e){

            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

//        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    public void setUpMapIfNeeded() {

        if(mMap == null) {

            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            if(mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        mMap.setTrafficEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                UpdateMap updateMap = new UpdateMap(getApplicationContext().getApplicationContext(), cameraPosition);
                updateMap.execute();
                mSearchLocation.setThreshold(1000);
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {

        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 17));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //builgoogle API
    public GoogleApiClient buildApiClient() {
        return new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class UpdateMap extends AsyncTask<Void, Void, Void> {
        Context context;
        CameraPosition position;
        List<Address> addresses;

        public UpdateMap (Context context, CameraPosition position) {
            this.context = context;
            this.position = position;
        }

        @Override
        protected Void doInBackground (Void... params) {
            try {
                Geocoder geo = new Geocoder (getApplicationContext().getApplicationContext(),
                        Locale.getDefault());

                addresses = geo.getFromLocation (position.target.latitude, position.target.longitude, 1);
            } catch (Exception e) {
                e.printStackTrace(); // getFromLocation() may sometimes fail
            }
            return null;
        }

        @Override
        protected void onPostExecute (Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (addresses.isEmpty ()) {
                } else {
                    Address addressObj = addresses.get(0);
                    String line = addressObj.getAddressLine(0);
                    String line2 = addressObj.getAddressLine(1);
                    String line3 = addressObj.getAddressLine(2);

                    StringBuilder builder = new StringBuilder();
                    if (line != null)
                        builder.append(line);

                    if (line2 != null && !line2.equals("null"))
                        builder.append(",").append(line2);

                    if (line3 != null && !line3.equals("null"))
                        builder.append(",").append(line3);

                    mSearchLocation.setText(builder.toString());
                }

            } catch (Exception e) {
                e.printStackTrace ();
            }
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener () {
        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem (position);
            final String placeId = item.getPlaceId ();
            final CharSequence primaryText = item.getPrimaryText (null);
            Log.i("", "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById (mGoogleApiClient, placeId);
            placeResult.setResultCallback (mUpdatePlaceDetailsCallback);

            Log.i("", "Called getPlaceById to get Place details for " + placeId);
            mSearchLocation.setThreshold(1000);
        }
    };


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer> () {
        @Override
        public void onResult (PlaceBuffer places) {
            if (!places.getStatus ().isSuccess ()) {
                // Request did not statusComplete successfully
                Log.e("", "Place query did not statusComplete. Error: " + places.getStatus().toString());
                places.release ();
                return;
            }

            // Get the Place object from the buffer.
            final Place place = places.get (0);

            Log.e("Place", place.getAddress() + "");

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16.0f));

            Log.i("", "LatLog " + place.getLatLng());
            Log.i("", "Place details received: " + place.getName());
            places.release();


        }
    };

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        locationManager.removeUpdates(this);
//    }
}
