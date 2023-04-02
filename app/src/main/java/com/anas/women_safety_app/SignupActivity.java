package com.anas.women_safety_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText eEmail,ePass;
    AppCompatButton btnSignup;
    TextView txtSkip;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();


        btnSignup=findViewById(R.id.btnSignup);
        txtSkip=findViewById(R.id.txtSkip);
        eEmail=findViewById(R.id.eEmail);
        ePass=findViewById(R.id.ePass);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email=eEmail.getText().toString().trim();
                String password=ePass.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    startActivity(new Intent(SignupActivity.this,DetailsActivity.class));
                                    Toast.makeText(SignupActivity.this, "success", Toast.LENGTH_SHORT).show();

                                } else {
                                    eEmail.setText("");
                                    ePass.setText("");
                                    Toast.makeText(SignupActivity.this, "failure", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });


        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
//                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }
}