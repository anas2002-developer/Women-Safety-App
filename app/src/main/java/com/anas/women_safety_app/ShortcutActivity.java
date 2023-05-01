package com.anas.women_safety_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.anas.women_safety_app.FRAGS.HomeFrag;
import com.anas.women_safety_app.MODELS.Model;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ShortcutActivity extends AppCompatActivity {

    SupportMapFragment smf;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    String sms = "Hi! This is just a testing message (by Mohd Anas), dont worry";
    String link = " ";
    String mapsLink = " ";
    Handler handler = new Handler();
    int DELAY = 15000; // 30 seconds in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut);


        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap);
        client = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        Toast.makeText(getApplicationContext(), "Location Sharing Started", Toast.LENGTH_SHORT).show();

        sos4link();
        sos4live();
        sos4call();

    }

    public void sos4link() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                sos4location();
                mapsLink = "https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();
            }
        });
    }

    public void sos4live() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(DELAY) // Update interval in milliseconds
                .setFastestInterval(DELAY); // Fastest update interval in milliseconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // Get the user's current location
                Location userLocation = locationResult.getLastLocation();
                Toast.makeText(getApplicationContext(), "location sent", Toast.LENGTH_SHORT).show();
                sos4sms(mapsLink);

            }
        };
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(locationRequest, locationCallback, null);

    }

    public void sos4call() {
        String Phone = "7060997570";
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + Phone));
        startActivity(i);
    }

    public void sos4location() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {




                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));


                        sos4address(latLng);


            }
        });
    }

    public void sos4sms(String sms2) {

        try {
            SmsManager smsManager = SmsManager.getDefault();

            FirebaseDatabase fdb = FirebaseDatabase.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference root = fdb.getReference().child("SURAKSHAK").child(mAuth.getCurrentUser().getUid()).child("CONTACTS");

            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        Model model = snapshot1.getValue(Model.class);
                        String phone = model.getPhone();
                        smsManager.sendTextMessage(phone, null, sms2, null, null);

                        System.out.println(phone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(getApplicationContext(), "Failed to retrieve phone from firebase", Toast.LENGTH_SHORT).show();
                }
            });

//            Toast.makeText(getActivity(), "sms sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "sms not sent", Toast.LENGTH_SHORT).show();
        }

    }

    public void sos4address(LatLng latLng) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            ArrayList<Address> arrAddresses = new ArrayList<>();
            arrAddresses = (ArrayList<Address>) geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);

            sms = "I m stuck help !";
            sms += "\nCURRENT LOCATION : " + arrAddresses.get(0).getAddressLine(0);

            Toast.makeText(getApplicationContext(), "Current location sent (text)", Toast.LENGTH_SHORT).show();
            sos4sms(sms);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sos4nolive() {

        if (locationCallback != null) {
            LocationServices.getFusedLocationProviderClient(getApplicationContext()).removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Location Sharing Stopped", Toast.LENGTH_SHORT).show();
        sos4nolive();
    }
}