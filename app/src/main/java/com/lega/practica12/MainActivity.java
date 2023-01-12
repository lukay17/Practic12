package com.lega.practica12;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.lega.practica12.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    
    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false);
                    }
            );

    private String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private GoogleMap googlemap;

    private final LatLng HOTEL1 = new LatLng(9.40832,-84.15876);
    private final LatLng HOTEL2 = new LatLng(-29.36684,-50.86475);
    private final LatLng HOTEL3 = new LatLng(36.75581,26.98891);
    private final LatLng HOTEL4 = new LatLng(41.01283,28.97791);
    private final String direccion ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        listener();

        setContentView(binding.getRoot());
        pedirPermisos();
        iniciarMap();
    }

    private void listener() {
        limpiarRadioButton();

        binding.textHotel1.setOnClickListener(v -> {
            binding.textHotel2.setChecked(false);
            binding.textHotel3.setChecked(false);
            binding.textHotel4.setChecked(false);
            Log.e(TAG, "Presionaste Hotel 1");
            googlemap.moveCamera(CameraUpdateFactory.newLatLng(HOTEL1));
        });

        binding.textHotel2.setOnClickListener(v -> {
            binding.textHotel1.setChecked(false);
            binding.textHotel3.setChecked(false);
            binding.textHotel4.setChecked(false);
            Log.e(TAG, "Presionaste Hotel 2");
            googlemap.moveCamera(CameraUpdateFactory.newLatLng(HOTEL2));
        });

        binding.textHotel3.setOnClickListener(v -> {
            binding.textHotel1.setChecked(false);
            binding.textHotel2.setChecked(false);
            binding.textHotel4.setChecked(false);
            Log.e(TAG, "Presionaste Hotel 3");
            googlemap.moveCamera(CameraUpdateFactory.newLatLng(HOTEL3));
        });

        binding.textHotel4.setOnClickListener(v -> {
            binding.textHotel1.setChecked(false);
            binding.textHotel2.setChecked(false);
            binding.textHotel3.setChecked(false);
            Log.e(TAG, "Presionaste Hotel 4");
            googlemap.moveCamera(CameraUpdateFactory.newLatLng(HOTEL4));
        });
    }

    private void limpiarRadioButton() {
        binding.textHotel1.setChecked(false);
        binding.textHotel2.setChecked(false);
        binding.textHotel3.setChecked(false);
        binding.textHotel4.setChecked(false);
    }

    private void pedirPermisos() {
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void iniciarMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void createMaker() {
        Marker hotel1 = googlemap.addMarker(new MarkerOptions()
                .position(HOTEL1)
                .draggable(true)
                .snippet("Hotel en el Top 1 de los mas grandes del mundo")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_hotel))
                .title("Tulemar Bungalows & Villas"));

        Marker hotel2 = googlemap.addMarker(new MarkerOptions()
                .position(HOTEL2)
                .draggable(true)
                .snippet("Hotel en el Top 2 de los mas grandes del mundo")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_hotel))
                .title("Hotel Colline de France"));

        Marker hotel3 = googlemap.addMarker(new MarkerOptions()
                .position(HOTEL3)
                .draggable(true)
                .snippet("Hotel en el Top 3 de los mas grandes del mundo")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_hotel))
                .title("Ikos Ari"));

        Marker hotel4 = googlemap.addMarker(new MarkerOptions()
                .position(HOTEL4)
                .draggable(true)
                .snippet("Hotel en el Top 4 de los mas grandes del mundo")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_hotel))
                .title("Romance Istanbul Hotel"));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googlemap = googleMap;
        createMaker();
        onClickMaker(googlemap);
    }

    private void onClickMaker(GoogleMap googlemap) {
        googlemap.setOnMarkerClickListener(marker -> {
                try{
                    List<Address> addresList = getLocation(marker.getPosition()).get();
                    String detail = "Hotel : " + marker.getTitle() +"\n" + "Ubicado : " + addresList.get(0).getAddressLine(0);
                    Toast.makeText(this,  detail, Toast.LENGTH_LONG).show();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            return false;
        });
    }

    private Future<List<Address>> getLocation(LatLng latLng) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        return executor.submit(()-> {
            try {
                Geocoder geocoder = new Geocoder(this);
                return geocoder.getFromLocation(latLng.latitude, latLng.longitude,2);
            } catch (Exception exception) {
                return new ArrayList<>();
            }
        });
    }


}