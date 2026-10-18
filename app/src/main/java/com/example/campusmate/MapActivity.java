package com.example.campusmate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment =
                (SupportMapFragment)
                        getSupportFragmentManager()
                                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Replace these example coordinates with
        // your actual campus locations.
        LatLng library =
                new LatLng(30.0000, 76.0000);

        LatLng mainBuilding =
                new LatLng(30.0010, 76.0010);

        LatLng cafeteria =
                new LatLng(29.9990, 76.0020);

        googleMap.addMarker(
                new MarkerOptions()
                        .position(library)
                        .title("Library"));

        googleMap.addMarker(
                new MarkerOptions()
                        .position(mainBuilding)
                        .title("Main Building"));

        googleMap.addMarker(
                new MarkerOptions()
                        .position(cafeteria)
                        .title("Cafeteria"));

        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        mainBuilding, 16f));
    }
}
