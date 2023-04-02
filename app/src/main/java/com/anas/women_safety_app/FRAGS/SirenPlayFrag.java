package com.anas.women_safety_app.FRAGS;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.anas.women_safety_app.R;

public class SirenPlayFrag extends Fragment {


    MediaPlayer mediaPlayer;

    LottieAnimationView btnSwitch;
    boolean isSwitchOn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_siren_play, container, false);


        btnSwitch=view.findViewById(R.id.btnSwitch);

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSwitchOn){
                    btnSwitch.setMinAndMaxProgress(0.5f,1.0f);
                    btnSwitch.playAnimation();
                    isSwitchOn=false;
                    if(mediaPlayer!=null)
                    {
                        mediaPlayer.stop();
                        mediaPlayer=null;
                    }
                }
                else {
                    btnSwitch.setMinAndMaxProgress(0.0f,0.5f);
                    btnSwitch.playAnimation();
                    isSwitchOn=true;

                    if (mediaPlayer == null)
                    {
                        mediaPlayer = MediaPlayer.create(getActivity(),R.raw.police_sound);
                        mediaPlayer.start();
                    }
                }
            }
        });

        return view;
    }
}