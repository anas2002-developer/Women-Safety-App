package com.anas.women_safety_app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CallActivity extends AppCompatActivity {

    TextView txtCallername;
    ImageView imgDecline,imgAccept;


    MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;
    TextView timeText;
    int number;
    String delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txtCallername=findViewById(R.id.txtCallername);
        imgDecline=findViewById(R.id.imgDecline);
        imgAccept=findViewById(R.id.imgAccept);

        txtCallername.setText(getIntent().getStringExtra("Contactname"));



        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();



        imgDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        imgAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                mediaPlayer.stop();
                Intent i = new Intent(CallActivity.this,AcceptActivity.class);
                i.putExtra("Callername",getIntent().getStringExtra("Contactname"));
                startActivity(i);



            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
        finish();
    }
}