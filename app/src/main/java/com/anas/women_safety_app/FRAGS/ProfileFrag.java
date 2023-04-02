package com.anas.women_safety_app.FRAGS;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anas.women_safety_app.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFrag extends Fragment {

    CircleImageView IMG;
    TextView txtName,
    txtEmail,
    txtAge,
    txtBlood,
    txtContact,
    txtStreet,
    txtCity,
    txtState,
    txtPincode;

    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtAge = view.findViewById(R.id.txtAge);
        txtBlood = view.findViewById(R.id.txtBlood);
        txtContact = view.findViewById(R.id.txtContact);
        txtStreet = view.findViewById(R.id.txtStreet);
        txtCity = view.findViewById(R.id.txtCity);
        txtState = view.findViewById(R.id.txtState);
        txtPincode = view.findViewById(R.id.txtPincode);
        IMG = view.findViewById(R.id.IMG);

        mAuth = FirebaseAuth.getInstance();

        txtEmail.setText(mAuth.getCurrentUser().getEmail());

        loadPROFILE_IMG();
        loadDetails();


        return view;
    }

    private void loadPROFILE_IMG() {

        FirebaseStorage fdbs = FirebaseStorage.getInstance();
        StorageReference roots = fdbs.getReference().child("PROFILE_IMG/" + mAuth.getCurrentUser().getUid());

        roots.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(getActivity())
                                .load(uri.toString())
                                .into(IMG);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "failure loading Profile image", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadDetails() {

        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        DatabaseReference root = fdb.getReference();

        root.child("WOMENSAFETY").child("PROFILE").child(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){

                            if (task.getResult().exists()){
                                DataSnapshot snapshot = task.getResult();
                                String name = String.valueOf(snapshot.child("name").getValue());
                                String age = String.valueOf(snapshot.child("age").getValue());
                                String blood = String.valueOf(snapshot.child("blood").getValue());
                                String contact = String.valueOf(snapshot.child("contact").getValue());
                                String street = String.valueOf(snapshot.child("street").getValue());
                                String city = String.valueOf(snapshot.child("city").getValue());
                                String state = String.valueOf(snapshot.child("state").getValue());
                                String pincode = String.valueOf(snapshot.child("pincode").getValue());

                                txtName.setText(name);
                                txtAge.setText(age);
                                txtBlood.setText(blood);
                                txtContact.setText(contact);
                                txtStreet.setText(street);
                                txtCity.setText(city);
                                txtState.setText(state);
                                txtPincode.setText(pincode);

                            }


                        }
                        else {
                            Toast.makeText(getActivity(), "failure loading details", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

}