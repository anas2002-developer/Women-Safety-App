package com.anas.women_safety_app.FRAGS;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anas.women_safety_app.R;

public class HelplineFrag extends Fragment {

    ImageView img112,img108,img102,img101,img100, img1091, img1098, img1073, img185;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_helpline, container, false);

        img112 = view.findViewById(R.id.img112);
        img108 = view.findViewById(R.id.img108);
        img102 = view.findViewById(R.id.img102);
        img101 = view.findViewById(R.id.img101);
        img100 = view.findViewById(R.id.img100);
        img1091 = view.findViewById(R.id.img1091);
        img1098 = view.findViewById(R.id.img1098);
        img1073 = view.findViewById(R.id.img1073);
        img185 = view.findViewById(R.id.img185);

        img112.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Phone = "112";
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+Phone));
                startActivity(i);
            }
        });

        img108.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Phone = "108";
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+Phone));
                startActivity(i);
            }
        });

        img102.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Phone = "102";
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+Phone));
                startActivity(i);
            }
        });


        img101.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Phone = "101";
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+Phone));
                startActivity(i);
            }
        });


        img100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Phone = "100";
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+Phone));
                startActivity(i);
            }
        });


        img1091.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Phone = "1091";
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+Phone));
                startActivity(i);
            }
        });


        img1098.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Phone = "1098";
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+Phone));
                startActivity(i);
            }
        });


        img1073.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Phone = "1071";
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+Phone));
                startActivity(i);
            }
        });

        img185.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Phone = "185";
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+Phone));
                startActivity(i);
            }
        });






        return view;
    }
}