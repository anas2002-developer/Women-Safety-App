package com.anas.women_safety_app.FRAGS;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anas.women_safety_app.MODELS.Session;
import com.anas.women_safety_app.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFrag extends Fragment implements View.OnClickListener {

    AppCompatButton btnLogout;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        btnLogout=view.findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(this::onClick);

        return view;
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnLogout:
                logout();
                break;
        }
    }


    public void logout(){

        Session session = new Session(getActivity());
        mAuth.signOut();
        getActivity().finish();
        session.logoutUser();
    }


}