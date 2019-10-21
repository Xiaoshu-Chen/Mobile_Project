package com.example.demo;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

// choose location on the map.
public class choose_location extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    TextView address;
    Button OK;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String TAG = "Adress";
    private Location currentLocation;
    Marker currentMarker;
    private static final int LOCATION_REQUEST_CODE =101;
    String result, address_text1;
    SensorManager sensorManager;
    Sensor sensor;
    int darkMode = 0;
    int previous = 0;

    // Create the connections and listen to different event actions.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        address = findViewById(R.id.address);
        OK = findViewById(R.id.OK);

        // Create initialize sensor obj.
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensor.TYPE_LIGHT);

        //initialize location service.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(choose_location.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(choose_location.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        fetchLastLocation();

        OK.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",result);
                returnIntent.putExtra("address",address_text1);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }


    // initialize the google map and make current marker on map.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);

        if (darkMode == 1) {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
        } else if (darkMode == 2) {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json2));
        }
        LatLng current = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
//        Log.d(TAG, "address1: "+currentLocation.getLatitude()+","+currentLocation.getLongitude());


        currentMarker = mMap.addMarker(new MarkerOptions().position(current).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center = mMap.getCameraPosition().target;

                if (currentMarker != null) {
                    currentMarker.remove();
                    currentMarker = mMap.addMarker(new MarkerOptions().position(center).title("Current location"));
                    address_text1 = getCompleteAddressString(center.latitude, center.longitude);

                    result = center.latitude+","+center.longitude;
                    address.setText(address_text1);
                }
            }
        });
    }

    //Fetch the entire address from the map.
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Address", strReturnedAddress.toString());
            } else {
                Log.w("Address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Address", "Canont get Address!");
        }
        return strAdd;
    }

    //Display the fetched location.
    private void fetchLastLocation(){
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d(TAG, "location: "+location);
                            currentLocation = location;
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String address_text = getCompleteAddressString(latitude, longitude);
                            address.setText(address_text);

//                            Log.d(TAG, "address1: "+latitude +","+longitude);
                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(choose_location.this);
                        } else {
                            Log.d(TAG, "location is null");
                        }
                    }
                });
    }

    //Handle the location permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                } else {
                    Toast.makeText(choose_location.this,"Location permission missing",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // pause the sensor.
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // resume the sensor.
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_NORMAL);
    }

    // Change mode based on the data from the sensor.
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (sensorEvent.values[0] < 13333) {
                previous = darkMode;
                darkMode = 1;
            } else if (sensorEvent.values[0] < 26666){
                previous = darkMode;
                darkMode = 2;
            } else {
                previous = darkMode;
                darkMode = 3;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
