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

import com.anas.women_safety_app.MODELS.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    EditText eEmail,ePass;
    AppCompatButton btnLogin;
    TextView txtSkip,txtForgotPass;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        btnLogin=findViewById(R.id.btnLogin);
        eEmail=findViewById(R.id.eEmail);
        ePass=findViewById(R.id.ePass);
        txtSkip=findViewById(R.id.txtSkip);
        txtForgotPass=findViewById(R.id.txtForgotPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=eEmail.getText().toString().trim();
                String password=ePass.getText().toString().trim();

                Session session = new Session(getApplicationContext());
                session.saveUser(email,password);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();

                                } else {
                                    eEmail.setText("");
                                    ePass.setText("");
                                    Toast.makeText(LoginActivity.this, "failure", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });

        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotActivity.class));
            }
        });
    }
}