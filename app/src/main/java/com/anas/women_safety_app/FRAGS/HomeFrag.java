package com.anas.women_safety_app.FRAGS;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.anas.women_safety_app.MODELS.Model;
import com.anas.women_safety_app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;

public class HomeFrag extends Fragment {

    SupportMapFragment smf;
    FusedLocationProviderClient client;

    ImageView sos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);


        sos = view.findViewById(R.id.sos);
        smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.gmap);
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        showCurrentLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sos4sms();
            }
        });

        return view;
    }

    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return;
    }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                smf.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Shruti");

                        googleMap.addMarker(markerOptions);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));

                        googleMap.addCircle(new CircleOptions()
                                .center(latLng)
                                .radius(1000) //1km
                                .fillColor(0x30ff0040)
                                .strokeColor(0x30ff0000));

                        Geocoder geocoder=new Geocoder(getActivity());
                        ArrayList<Address> arrAddresses=new ArrayList<>();
                        try {
                            arrAddresses = (ArrayList<Address>) geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
                            Log.d("Address : ",arrAddresses.get(0).getAddressLine(0));
                            System.out.println(arrAddresses.get(0).getAddressLine(0));

                            System.out.println("Address 2 : "+arrAddresses.get(1).getAddressLine(0));
                            System.out.println("Address 3 : "+arrAddresses.get(2).getAddressLine(0));
                            System.out.println("Address 4 : "+arrAddresses.get(3).getAddressLine(0));
                            System.out.println("Address 5 : "+arrAddresses.get(4).getAddressLine(0));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void sos4call(){
        String Phone = "100";
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:"+Phone));
        startActivity(i);
    }

    public void sos4sms(){


    }

    private void readPhone(){

        try {
            String sms = "Help me";
            SmsManager smsManager = SmsManager.getDefault();

            FirebaseDatabase fdb = FirebaseDatabase.getInstance();
            DatabaseReference root = fdb.getReference().child("WOMENSAFETY").child("CONTACTS");

            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()){

                        Model model = snapshot1.getValue(Model.class);
                        String phone = model.getPhone();
                        smsManager.sendTextMessage(model.getPhone(),null,sms,null,null);

                        System.out.println(phone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(getActivity(), "Failed to retrieve phone from firebase", Toast.LENGTH_SHORT).show();
                }
            });

            Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), "Message not sent", Toast.LENGTH_SHORT).show();
        }

    }

}