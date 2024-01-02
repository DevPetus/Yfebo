package com.example.yfebo;

import static com.google.android.gms.location.Priority.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;

    final static int MY_PERMISSIONS_REQUEST_COARSE_LOCATE = 35;
    public String cityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Button locateButton = findViewById(R.id.locateButton);
        EditText cityText = findViewById(R.id.cityText);
        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { cityText.setText(clickLocate(view)); }
        });
    }

    public String clickLocate(View v) {

        if (ContextCompat.checkSelfPermission(this,

                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATE);
        } else {
            locate();
            if (cityName != "") { return cityName; }
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_COARSE_LOCATE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locate();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void locate() {
        Log.v("Yfebo", "Locate");
        fusedLocationClient.getCurrentLocation(PRIORITY_LOW_POWER, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.v("Yfebo", "Location GET");
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1);
                                cityName = addresses.get(0).getAddressLine(0);
                            } catch (IOException e) { throw new RuntimeException(e); }
                        }
                    }
                });

    }
}