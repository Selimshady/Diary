package com.example.diary;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.diary.db.AppDatabase;
import com.example.diary.db.Memory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.diary.databinding.ActivityAllMemoriesBinding;

import java.util.ArrayList;
import java.util.List;

public class AllMemoriesActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityAllMemoriesBinding binding;

    AppDatabase db;
    List<Memory> memories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAllMemoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        db = AppDatabase.getDbInstance(getApplicationContext());
        memories = new ArrayList<>();
        memories = db.memoryDao().getAllMemories();
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
        double x = 0;
        double y = 0;

        for(Memory memo: memories)
        {
            LatLng latLng = new LatLng(Double.parseDouble(memo.getLatitude()),Double.parseDouble(memo.getLongitude()));
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(memo.getTitle());

            switch (memo.getEmotion())
            {
                case 0:
                    markerOptions.icon(BitmapFromVector(this,R.drawable.tiredface));
                    break;
                case 1:
                    markerOptions.icon(BitmapFromVector(this,R.drawable.happyface));
                    break;
                case 2:
                    markerOptions.icon(BitmapFromVector(this,R.drawable.sadface));
                    break;
                case 3:
                    markerOptions.icon(BitmapFromVector(this,R.drawable.angryface));
                    break;
                case 4:
                    markerOptions.icon(BitmapFromVector(this,R.drawable.lovingface));
            }
            mMap.addMarker(markerOptions);
            x+=latLng.latitude;
            y+=latLng.longitude;
        }

        x/=memories.size();
        y/=memories.size();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(x,y),16));
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, 100, 100);

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}