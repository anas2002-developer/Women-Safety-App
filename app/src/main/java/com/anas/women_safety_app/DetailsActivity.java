package com.anas.women_safety_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anas.women_safety_app.MODELS.Model_Details;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsActivity extends AppCompatActivity {

    EditText eName, eAge, eBlood, eContact, eStreet, eCity, eState, ePincode;
    CircleImageView IMG;
    Button btnSubmit;

    FirebaseAuth mAuth;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mAuth = FirebaseAuth.getInstance();

        IMG = findViewById(R.id.IMG);
        btnSubmit = findViewById(R.id.btnSubmit);
        eName = findViewById(R.id.eName);
        eAge = findViewById(R.id.eAge);
        eBlood = findViewById(R.id.eBlood);
        eContact = findViewById(R.id.eContact);
        eStreet = findViewById(R.id.eStreet);
        eCity = findViewById(R.id.eCity);
        eState = findViewById(R.id.eState);
        ePincode = findViewById(R.id.ePincode);

        IMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(DetailsActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent iGallery = new Intent(Intent.ACTION_PICK);
                                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(iGallery, 200);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uri!=null){
                    String name = eName.getText().toString();
                    String age = eAge.getText().toString();
                    String blood = eBlood.getText().toString();
                    String contact = eContact.getText().toString();
                    String street = eStreet.getText().toString();
                    String city = eCity.getText().toString();
                    String state = eState.getText().toString();
                    String pincode = ePincode.getText().toString();

                    Model_Details model = new Model_Details(name, age, blood, contact, street, city, state, pincode);

                    FirebaseDatabase fdb = FirebaseDatabase.getInstance();
                    DatabaseReference root = fdb.getReference();

                    root.child("SURAKSHAK").child(mAuth.getCurrentUser().getUid()).child("PROFILE").setValue(model);


                    FirebaseStorage fdbs = FirebaseStorage.getInstance();
                    StorageReference roots = fdbs.getReference().child("PROFILE_IMG/" + mAuth.getCurrentUser().getUid());

                    roots.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(DetailsActivity.this, "success", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DetailsActivity.this, "failure", Toast.LENGTH_SHORT).show();
                                }
                            });

                    Toast.makeText(DetailsActivity.this, "Profile Details Updated", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(DetailsActivity.this,LoginActivity.class));
                }
                else {
                    Toast.makeText(DetailsActivity.this, "Upload Profile Img", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 200) {
                uri = data.getData();
                IMG.setImageURI(uri);
                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show();
        }
    }
}