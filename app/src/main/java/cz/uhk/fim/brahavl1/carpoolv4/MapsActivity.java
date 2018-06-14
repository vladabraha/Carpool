package cz.uhk.fim.brahavl1.carpoolv4;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationFragment.onButtonInterface, LocationFragmentBottom.onButtonSaveInterface {

    private GoogleMap mMap;
    private LocationFragment locationFragment;
    private LocationFragmentBottom locationFragmentBottom;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Boolean mRequestingLocationUpdates;
    private LocationRequest locationRequest;
    private SettingsClient settingsClient;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private LocationSettingsRequest mLocationSettingsRequest;

    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String TAG = MapsActivity.class.getSimpleName();
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private double lat;
    private double lon;
    private double prevLat;
    private double prevLon;
    private LatLng myLocation; //sem budeme ukladat kde jsme
    private double distance = 0; //ujeta vzdálenost


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (findViewById(R.id.fragmentLocation) != null) { //kdyby otaceni, ale nebude
            locationFragment = new LocationFragment(); //vytvori fragment
            locationFragment.setOnLocationUpdateInterface(this);

            getSupportFragmentManager().beginTransaction().add(R.id.fragmentLocation, locationFragment).commit(); //vlozi fragment do Map activity
        }

        if (findViewById(R.id.fragmentLocationBottom) != null) { //kdyby otaceni, ale nebude
            locationFragmentBottom = new LocationFragmentBottom(); //vytvori fragment
            locationFragmentBottom.setOnLocationUpdateInterface(this);

            getSupportFragmentManager().beginTransaction().add(R.id.fragmentLocationBottom, locationFragmentBottom).commit(); //vlozi fragment do Map activity
        }


        //inicializace
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mRequestingLocationUpdates = false;
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(4000); //kazdy 4 sec se to aktualizuje

        settingsClient = LocationServices.getSettingsClient(this);

        updateValuesFromBundle(savedInstanceState);

        createLocationCallBack();


        buildLocationSettingrequest();

    }

    private void buildLocationSettingrequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        mLocationSettingsRequest = builder.build();

    }

    //zjisti do current location kde se nachazim
    private void createLocationCallBack() {

        Log.i("bla bla", "vola se createLoacationCallback");

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();

                updateLocation();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates && checkPermissions()) {
            System.out.println(mRequestingLocationUpdates);
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "vola se updateValuesFromBundle");
        if (savedInstanceState != null) {
            //TODO create buttons enabled/disabled
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
                System.out.println("tady se to nahodou nenastavuej" + mRequestingLocationUpdates);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            /*// Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }*/
            updateLocation();
        }
    }

    /**
     * Stores activity data in the Bundle.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "vola se onSaveInstanceState");
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }


    //zacne tahat aktivitu - google dev.
    private void startLocationUpdates() {
        Log.i(TAG, "vola se startLocationUpdates");
        // Begin by checking if the device has the necessary location settings.
        settingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions();
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocation();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(MapsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        updateLocation();
                    }
                });

    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        Log.i(TAG, "vola se stopLocationUpdates");
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;;
                    }
                });
    }


    //Vrati na co kliknul uzivatel ve vyskakovacim okne
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "vola se onActiviyResult");
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateLocation();
                        break;
                }
                break;
        }
    }

    private void updateLocation() {
        if (mCurrentLocation == null) {
//            Toast.makeText(this,mCurrentLocation.toString(), Toast.LENGTH_SHORT).show();
            return;
        } else {

            prevLat = lat;
            prevLon = lon;

            lat = mCurrentLocation.getLatitude();
            lon = mCurrentLocation.getLongitude();

            myLocation = new LatLng(lat, lon);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

            if (prevLon != 0) {
                mMap.addPolyline(new PolylineOptions().clickable(false).add(
                        new LatLng(prevLat, prevLon), //pridavam body, puvodni lokace a nova prichozi
                        new LatLng(lat, lon)
                ));

                float[] distanceList = new float[2];
                Location.distanceBetween(prevLat, prevLon, lat, lon, distanceList);

                distance = distance + distanceList[0];

                locationFragment.updateDistance(distance);


            }
        }


    }

    /**
     * Callback received when a permissions request has been completed.
     * Pokud se vsechny povoleni od uzivatele uspesne vrati zpatky, tak se zapne startLocationUpdate
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "vola se onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                //TODO DO TOHODLE TO SNAD NIKDY NESPADNE - MUZE SE TO TEDY SMAZAT??
             /*   showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });*/
            }
        }
    }

    //ukazuje dialog se zapnutím polohy
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                this.findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Řekne jestli je k dispozici povoleni na polohu, jinak v elsu on resume vyskočí dotazník
     *
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

           //LatLng sydney = new LatLng(-34, 151);
        if (getLastKnownPosition() != null){
            Location loc = new Location(getLastKnownPosition());
            LatLng position = new LatLng(loc.getLatitude(), loc.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12.0f));
        }

    }

    //posledni znama pozice stack
    private Location getLastKnownPosition() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                return lastKnownLocationGPS;
            } else {
                Location loc =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//                System.out.println("1::"+loc);----getting null over here
//                System.out.println("2::"+loc.getLatitude());
                return loc;
            }
        } else {
            return null;
        }
    }


    @Override
    public void onButtonClickStart(Boolean mRequestingLocationUpdates) {
        this.mRequestingLocationUpdates = mRequestingLocationUpdates;

        if (mRequestingLocationUpdates && checkPermissions()) {
            System.out.println(mRequestingLocationUpdates + "clickedGo");

            startLocationUpdates();
            Toast.makeText(this, "Start location updates", Toast.LENGTH_SHORT).show();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

        updateLocation();
        //locationFragment.startChronometr();

    }

//    @Override
//    public void onButtonClickStop() {
//       stopLocationUpdates();
//       //locationFragment.stopChronometr();
//    }

    @Override
    public void onButtonClickStop(String distance, long base) {
        stopLocationUpdates();
        Toast.makeText(this, "Stop location updates", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, distance + " " + String.valueOf(base),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonClickSave() {
        Toast.makeText(this,"Ahoj z Maps Activity",Toast.LENGTH_SHORT).show();

    }
}
