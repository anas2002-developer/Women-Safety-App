package com.anas.women_safety_app.FRAGS;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.anas.women_safety_app.ContactList;
import com.anas.women_safety_app.R;

public class FakeCallFrag extends Fragment {


    EditText eDelay;
    Button btnContact;

    String delay = "0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fake_call, container, false);

        eDelay = view.findViewById(R.id.eDelay);
        btnContact = view.findViewById(R.id.btnContact);




        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eDelay.getText().toString().trim().equals("")) {
                } else {
                    delay = eDelay.getText().toString();
                }
//                Toast.makeText(MainActivity.this, delay, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getActivity(), ContactList.class);
                i.putExtra("Delay1", delay);
                getActivity().startActivity(i);
            }
        });

        return view;
    }
}