package com.anas.women_safety_app.FRAGS;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.anas.women_safety_app.DetailsActivity;
import com.anas.women_safety_app.MODELS.Model;
import com.anas.women_safety_app.R;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFrag extends Fragment {

    SupportMapFragment smf;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    ImageView sos, btnCurrent, btnPhoto, btnVideo, btnTrack, btnRecording;

    String sms = "Hi! This is just a testing message (by Mohd Anas), dont worry";
    String link = " ";
    String mapsLink = " ";

    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    String currentDate = "_" + day + ":" + (month + 1) + ":" + year + "_";
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    String currentTime = hour + ":" + minute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sos = view.findViewById(R.id.sos);
        btnCurrent = view.findViewById(R.id.btnCurrent);
        btnPhoto = view.findViewById(R.id.btnPhoto);
        btnVideo = view.findViewById(R.id.btnVideo);
        btnTrack = view.findViewById(R.id.btnTrack);
        btnRecording = view.findViewById(R.id.btnRecording);

        smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.gmap);
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA};

        Dexter.withContext(getActivity())
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            showCurrentLocation();
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        btnCurrent.setOnClickListener(v -> {
            showCurrentLocation();
        });


        sos.setOnClickListener(v -> {
            sos4location();
            sos4link();
            sos4live();
            sos4call();
        });

        btnPhoto.setOnClickListener(v -> {
            ImagePicker.Companion.with(HomeFrag.this).start();
        });

        btnVideo.setOnClickListener(v -> {
            Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            i.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
            i.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(i, 500);
        });

        btnTrack.setOnClickListener(v -> {
            sos4nolive();
        });

        return view;
    }

    public void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

//                if (location!=null){
                smf.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {

                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Last Location");

                        googleMap.addMarker(markerOptions);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));

                        googleMap.addCircle(new CircleOptions()
                                .center(latLng)
                                .radius(1000) //1km circle
                                .fillColor(0x30ff0040)
                                .strokeColor(0x30ff0000));


                    }
                });


            }
        });
    }

    public void sos4call() {
        String Phone = "7060997580";
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + Phone));
        startActivity(i);
    }

    private void sos4sms(String sms2) {

        try {
            SmsManager smsManager = SmsManager.getDefault();

            FirebaseDatabase fdb = FirebaseDatabase.getInstance();
            DatabaseReference root = fdb.getReference().child("WOMENSAFETY").child("CONTACTS");

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

                    Toast.makeText(getActivity(), "Failed to retrieve phone from firebase", Toast.LENGTH_SHORT).show();
                }
            });

            Toast.makeText(getActivity(), "sms sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "sms not sent", Toast.LENGTH_SHORT).show();
        }

    }

    private void sos4address(LatLng latLng) {
        try {
            Geocoder geocoder = new Geocoder(getActivity());
            ArrayList<Address> arrAddresses = new ArrayList<>();
            arrAddresses = (ArrayList<Address>) geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);

            sms = "I m stuck help !";
            sms += "\nCURRENT LOCATION : " + arrAddresses.get(0).getAddressLine(0);
            sms += "\n\nNEARBY LANDMARK : \n" + arrAddresses.get(1).getAddressLine(0);

            sos4sms(sms);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sos4location() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {


                smf.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {


                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));


                        sos4address(latLng);

                    }
                });
            }
        });
    }

    private void sos4link() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                mapsLink = "https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();
            }
        });
    }

    private void sos4live() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(15000) // Update interval in milliseconds
                .setFastestInterval(1000); // Fastest update interval in milliseconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // Get the user's current location
                Location userLocation = locationResult.getLastLocation();
                sos4sms(mapsLink);
                Toast.makeText(getActivity(), "Location Sharing started", Toast.LENGTH_SHORT).show();

            }
        };
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, locationCallback, null);

    }

    private void sos4nolive() {

        if (locationCallback != null) {
            Toast.makeText(getActivity(), "location sharing stopped", Toast.LENGTH_SHORT).show();
            LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == 500 ){
                Uri uriVid = data.getData();

                FirebaseStorage fdbs = FirebaseStorage.getInstance();
                StorageReference roots = fdbs.getReference().child("LOCATION_IMG/" + FirebaseAuth.getInstance().getCurrentUser().getUid()+currentDate+currentTime);

                roots.putFile(uriVid)
                        .addOnSuccessListener(taskSnapshot -> {
                            roots.getDownloadUrl().addOnSuccessListener(uri -> {

                                link = uri.toString();
                                System.out.println(link);
//                                sos4sms(link);
                                Toast.makeText(getActivity(), "vid sent", Toast.LENGTH_SHORT).show();
                            });
                        })
                        .addOnFailureListener(e -> {

                        });
            }

            if (requestCode == ImagePicker.REQUEST_CODE){

                Uri uriPhoto = data.getData();

                FirebaseStorage fdbs = FirebaseStorage.getInstance();
                StorageReference roots = fdbs.getReference().child("LOCATION_IMG/" + FirebaseAuth.getInstance().getCurrentUser().getUid()+currentDate+currentTime);

                roots.putFile(uriPhoto)
                        .addOnSuccessListener(taskSnapshot -> {
                            roots.getDownloadUrl().addOnSuccessListener(uri -> {

                                link = uri.toString();
                                System.out.println(link);
                                sos4sms(link);
                                Toast.makeText(getActivity(), "photo sent", Toast.LENGTH_SHORT).show();
                            });
                        })
                        .addOnFailureListener(e -> {

                        });
            }


        }
    }

    @Override
    public void onStop() {
        super.onStop();
        sos4live();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        sos4live();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sos4nolive();
    }
}




