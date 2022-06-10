package com.example.diary;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.diary.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener
        ,GoogleMap.OnMarkerDragListener{

    private GoogleMap mMap;

    private Geocoder geocoder;

    Button confirmButton;

    String latitude,longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);

        confirmButton = findViewById(R.id.confirm_location);

        confirmButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("LATITUDE",latitude);
            intent.putExtra("LONGITUDE",longitude);
            setResult(18,intent);
            MapsActivity.super.onBackPressed();
        });

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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);

        try {
            List<Address> addressList = geocoder.getFromLocationName("İstanbul",1);
            if(addressList.size() > 0)
            {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAdress = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions().position(latLng).title(streetAdress).draggable(true));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        Log.d("MapsActivity","onMarkerDrag: ");
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        Log.d("MapsActivity","onMarkerDragEnd: ");
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(addresses.size() > 0) {
                latitude = String.valueOf(latLng.latitude);
                longitude = String.valueOf(latLng.longitude);
                Address address = addresses.get(0);
                String streetAdress = address.getAddressLine(0);
                marker.setTitle(streetAdress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
        Log.d("MapsActivity","onMarkerDragStart: ");
    }
}