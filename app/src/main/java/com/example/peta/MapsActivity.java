package com.example.peta;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.peta.databinding.ActivityMapsBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.Manifest.permission;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    double latitude;
    double longitude;
    Button koordinat;
    Button posisi_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        koordinat = findViewById(R.id.koordinat);
        posisi_user = findViewById(R.id.posisi_user);
        koordinat.setOnClickListener(this);
        posisi_user.setOnClickListener(this);


//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (latitude != 0 && longitude!=0){
            Toast.makeText(getApplicationContext(),"Latitude : " + latitude + " Longitude : " + longitude, Toast.LENGTH_LONG).show();
        }
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        CekGPS();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(location -> {
            mMap.addMarker(new MarkerOptions().position((new LatLng(location.getLatitude(),location.getLongitude()))).
                    title(" It's Me! "));
        });
//        mMap = googleMap;
//
//        mMap.setMyLocationEnabled(true);
//
//        mMap.setOnMapClickListener(location -> {
//            mMap.addMarker(new MarkerOptions().position((new LatLng(location.getLatitude(),location.getLongitude()))).
//                    title(" It's Me! "));
//        });
//
////         Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onClick(View v) {
        if (v == koordinat) {

            if (latitude != 0 && longitude != 0) {
                Toast.makeText(getApplicationContext(), "Latitude : " + latitude + " Longitude : " + longitude, Toast.LENGTH_LONG).show();
            }
        } else if (v == posisi_user) {
            LatLng user = new LatLng(latitude, longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user, 12));
        }
    }
    public void CekGPS() {
        try {

            /* Pengecekan GPS hidup / tidak */
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Info");
                builder.setMessage("Anda akan mengaktifkan GPS ?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int witch) {
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                });

                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int witch) {

                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        } catch (Exception e) {
            // TODO: handle exception

        }
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // menampilkan status google play service
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {
            // Google Play Service Tersedia
            try {
                LocationManager locationManager = (LocationManager)
                        getSystemService(LOCATION_SERVICE);

                // Membuat Kriteria Untuk Penumpangan Provider
                Criteria criteria = new Criteria();

                // Mencari Provider Terbaik
                String provider = locationManager.getBestProvider(criteria, true);

                // Mendapatkan Lokasi Terakhir
                if (Build.VERSION.SDK_INT >= 23){
                    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{
                                permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                        return;
                    }
                }
                Location location = locationManager.getLastKnownLocation(provider);

                if(location != null){
                    onLocationChanged(location);
                }
                locationManager.requestLocationUpdates(provider,5000,0, (LocationListener) this);
            } catch (Exception e){
            }
        }
    }

    public void onLocationChanged(Location lokasi){
        //TODO Auto-generated method stub
        latitude = lokasi.getLatitude();
        longitude = lokasi.getLongitude();
    }
}