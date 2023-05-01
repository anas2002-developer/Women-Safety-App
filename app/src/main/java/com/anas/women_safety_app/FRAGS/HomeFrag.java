package com.anas.women_safety_app.FRAGS;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
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
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
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

public class HomeFrag extends Fragment implements SensorEventListener {

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

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    Handler handler = new Handler();
    int DELAY = 15000; // 30 seconds in milliseconds

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float SHAKE_THRESHOLD_GRAVITY = 10f;
    private long lastShakeTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sos = view.findViewById(R.id.sos);
        btnCurrent = view.findViewById(R.id.btnCurrent);
        btnPhoto = view.findViewById(R.id.btnPhoto);
        btnVideo = view.findViewById(R.id.btnVideo);
        btnRecording = view.findViewById(R.id.btnRecording);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastShakeTime = System.currentTimeMillis();

        smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.gmap);
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO };

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
            Toast.makeText(getActivity(), "Location Updated", Toast.LENGTH_SHORT).show();
            showCurrentLocation();
        });


        sos.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Location Sharing Started", Toast.LENGTH_SHORT).show();

            sos4link();
            sos4live();
            sos4call();
        });

        btnPhoto.setOnClickListener(v -> {
            ImagePicker.Companion.with(HomeFrag.this).start();
        });

        btnVideo.setOnClickListener(v -> {
            Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            i.putExtra(MediaStore.EXTRA_DURATION_LIMIT, DELAY/1000);
            i.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(i, 500);
        });

        btnRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setOutputFile(getRecordingFilePath());
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Stop recording
                            mediaRecorder.stop();
                            mediaRecorder.release();
                            mediaRecorder = null;
                            sendAudio();
                            Toast.makeText(getActivity(), "Recording stopped automatically!", Toast.LENGTH_LONG).show();
                        }
                    }, DELAY);


                    Toast.makeText(getActivity(),"Recording started!!",Toast.LENGTH_LONG).show();
                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

        sos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Location Sharing Stopped", Toast.LENGTH_SHORT).show();
                sos4nolive();
                return true;
            }
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
        String Phone = "7060997570";
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + Phone));
        startActivity(i);
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

                    Toast.makeText(getActivity(), "Failed to retrieve phone from firebase", Toast.LENGTH_SHORT).show();
                }
            });

//            Toast.makeText(getActivity(), "sms sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "sms not sent", Toast.LENGTH_SHORT).show();
        }

    }

    public void sos4address(LatLng latLng) {
        try {
            Geocoder geocoder = new Geocoder(getActivity());
            ArrayList<Address> arrAddresses = new ArrayList<>();
            arrAddresses = (ArrayList<Address>) geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);

            sms = "I m stuck help !";
            sms += "\nCURRENT LOCATION : " + arrAddresses.get(0).getAddressLine(0);

            Toast.makeText(getActivity(), "Current location sent (text)", Toast.LENGTH_SHORT).show();
            sos4sms(sms);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sos4location() {
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

    public void sos4link() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                Toast.makeText(getActivity(), "location sent", Toast.LENGTH_SHORT).show();
                sos4sms(mapsLink);

            }
        };
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, locationCallback, null);

    }

    public void sos4nolive() {

        if (locationCallback != null) {
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

                                FirebaseDynamicLinks.getInstance().createDynamicLink()
                                        .setLink(Uri.parse(uri.toString()))
                                        .setDomainUriPrefix("https://surakshak2.page.link")
                                        .setAndroidParameters(
                                                new DynamicLink.AndroidParameters.Builder()
                                                        .setMinimumVersion(1)
                                                        .build())
                                        .buildShortDynamicLink()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Uri shortLink = task.getResult().getShortLink();
                                                link = shortLink.toString();
                                                System.out.println(link);
                                                Toast.makeText(getActivity(), "Video Sent", Toast.LENGTH_SHORT).show();
                                                sos4sms("Vid : "+link);
                                            } else {
                                                // Handle error
                                            }
                                        });

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

                                FirebaseDynamicLinks.getInstance().createDynamicLink()
                                        .setLink(Uri.parse(uri.toString()))
                                        .setDomainUriPrefix("https://surakshak2.page.link")
                                        .setAndroidParameters(
                                                new DynamicLink.AndroidParameters.Builder()
                                                        .setMinimumVersion(1)
                                                        .build())
                                        .buildShortDynamicLink()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Uri shortLink = task.getResult().getShortLink();
                                                link = shortLink.toString();
                                                System.out.println(link);
                                                Toast.makeText(getActivity(), "Photo Sent", Toast.LENGTH_SHORT).show();
                                                sos4sms("Photo : "+link);
                                            } else {
                                                // Handle error
                                            }
                                        });


                            });
                        })
                        .addOnFailureListener(e -> {

                        });


            }
        }
    }


    public String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getActivity());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "testRecordingFile.mp3");
        return file.getPath();
    }

    public void sendAudio() {

        Uri uriRec = Uri.fromFile(new File(getRecordingFilePath()));

        FirebaseStorage fdbs = FirebaseStorage.getInstance();
        StorageReference roots = fdbs.getReference().child("LOCATION_IMG/" + FirebaseAuth.getInstance().getCurrentUser().getUid()+currentDate+currentTime+".mp3");

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/AMR_NB")
                .build();
        roots.putFile(uriRec,metadata)
                .addOnSuccessListener(taskSnapshot -> {
                    roots.getDownloadUrl().addOnSuccessListener(uri -> {

                        FirebaseDynamicLinks.getInstance().createDynamicLink()
                                .setLink(Uri.parse(uri.toString()))
                                .setDomainUriPrefix("https://surakshak2.page.link")
                                .setAndroidParameters(
                                        new DynamicLink.AndroidParameters.Builder()
                                                .setMinimumVersion(1)
                                                .build())
                                .buildShortDynamicLink()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Uri shortLink = task.getResult().getShortLink();
                                        link = shortLink.toString();
                                        System.out.println(link);
                                        Toast.makeText(getActivity(), "Recording Sent", Toast.LENGTH_SHORT).show();
                                        sos4sms("Recording : "+link);
//                                        Toast.makeText(getActivity(), link, Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Handle error
                                    }
                                });

                    });
                })
                .addOnFailureListener(e -> {

                });
    }

//    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float gravityX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gravityY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gravityZ = event.values[2] / SensorManager.GRAVITY_EARTH;

            float gravityForce = (float) Math.sqrt(gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ);
            if (gravityForce > SHAKE_THRESHOLD_GRAVITY) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShakeTime > 2000) {  // wait 2 seconds before next shake
                    lastShakeTime = currentTime;
                    sos.performClick(); // Trigger button click event
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(500); // Vibrate for 500 ms
                    }

                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

//    @Override
//    public void onStop() {
//        super.onStop();
////        sos4live();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
////        sos4live();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        sos4nolive();
//    }
}




