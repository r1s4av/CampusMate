package com.example.campusmate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Campus locations
        LatLng library = new LatLng(30.0000, 76.0000);
        LatLng mainBuilding = new LatLng(30.0010, 76.0010);
        LatLng cafeteria = new LatLng(29.9990, 76.0020);

        // Add markers with place names
        Marker libraryMarker = googleMap.addMarker(
                new MarkerOptions()
                        .position(library)
                        .title("Central Library")
                        .snippet("Campus Library")
        );

        Marker buildingMarker = googleMap.addMarker(
                new MarkerOptions()
                        .position(mainBuilding)
                        .title("Main Building")
                        .snippet("Main Academic Block")
        );

        Marker cafeteriaMarker = googleMap.addMarker(
                new MarkerOptions()
                        .position(cafeteria)
                        .title("Campus Cafeteria")
                        .snippet("Food and Refreshments")
        );

        // Show all place names when the map opens
        if (libraryMarker != null) {
            libraryMarker.showInfoWindow();
        }

        if (buildingMarker != null) {
            buildingMarker.showInfoWindow();
        }

        if (cafeteriaMarker != null) {
            cafeteriaMarker.showInfoWindow();
        }

        // Focus camera on the main building
        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(mainBuilding, 16f)
        );
    }
}
