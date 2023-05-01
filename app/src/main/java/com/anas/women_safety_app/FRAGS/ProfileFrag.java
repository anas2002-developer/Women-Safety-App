package com.anas.women_safety_app.FRAGS;

import android.app.Dialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

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

    Button btnEdit;
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

        btnEdit = view.findViewById(R.id.btnEdit);

        mAuth = FirebaseAuth.getInstance();

        txtEmail.setText(mAuth.getCurrentUser().getEmail());

        loadPROFILE_IMG();
        loadDetails();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.profile_dialog);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.dialog_bg));
                }
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                EditText eName = dialog.findViewById(R.id.eName);
                EditText eAge = dialog.findViewById(R.id.eAge);
                EditText eBlood = dialog.findViewById(R.id.eBlood);
                EditText eContact = dialog.findViewById(R.id.eContact);
                EditText eStreet = dialog.findViewById(R.id.eStreet);
                EditText eCity = dialog.findViewById(R.id.eCity);
                EditText eState = dialog.findViewById(R.id.eState);
                EditText ePincode = dialog.findViewById(R.id.ePincode);

                FirebaseDatabase.getInstance().getReference().child("SURAKSHAK").child(FirebaseAuth.getInstance().getUid()).child("PROFILE").get()
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

                                                eName.setText(name);
                                                eAge.setText(age);
                                                eBlood.setText(blood);
                                                eContact.setText(contact);
                                                eStreet.setText(street);
                                                eCity.setText(city);
                                                eState.setText(state);
                                                ePincode.setText(pincode);

                                            }


                                        }

                                    }
                                });


                Button btnSave = dialog.findViewById(R.id.btnSave);

                btnSave.setOnClickListener(v1 -> {

                    String name = eName.getText().toString();
                    String age = eAge.getText().toString();
                    String blood = eBlood.getText().toString();
                    String contact = eContact.getText().toString();
                    String street = eStreet.getText().toString();
                    String city = eCity.getText().toString();
                    String state = eState.getText().toString();
                    String pincode = ePincode.getText().toString();

                    if (name.equals("")){
                        Toast.makeText(getActivity(), "Blank Field!", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Map<String,Object> map = new HashMap<>();
                        map.put("name",name);
                        map.put("age",age);
                        map.put("blood",blood);
                        map.put("contact",contact);
                        map.put("street",street);
                        map.put("city",city);
                        map.put("state",state);
                        map.put("pincode",pincode);

                        FirebaseDatabase.getInstance().getReference().child("SURAKSHAK").child(FirebaseAuth.getInstance().getUid()).child("PROFILE")
                                .updateChildren(map);

                        Toast.makeText(getActivity(), "Details Saved, Please Restart App", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
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

        root.child("SURAKSHAK").child(mAuth.getCurrentUser().getUid()).child("PROFILE").get()
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