package com.anas.women_safety_app.FRAGS;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anas.women_safety_app.ADAPTERS.Adapter;
import com.anas.women_safety_app.MODELS.Model;
import com.anas.women_safety_app.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ContactFrag extends Fragment {

    RecyclerView vRV;
    DatabaseReference root;
    Adapter adapter;

    Calendar calendar ;

    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        fab=view.findViewById(R.id.fab);
        vRV=view.findViewById(R.id.vRV);
        vRV.setLayoutManager(new LinearLayoutManager(getActivity()));


//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
//                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CALL_PHONE},100);
//        }

        root= FirebaseDatabase.getInstance().getReference().child("SURAKSHAK").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CONTACTS");

        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(root, Model.class)
                        .build();

        adapter = new Adapter(options);
        vRV.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });

        return view;
    }

    private void addContact() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.contact_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.dialog_bg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText eName = dialog.findViewById(R.id.eName);
        EditText ePhone = dialog.findViewById(R.id.ePhone);
        EditText eType = dialog.findViewById(R.id.eType);
        Button btnAdd = dialog.findViewById(R.id.btnAdd);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);



        btnAdd.setOnClickListener(v -> {

            String Name = eName.getText().toString();
            String Phone = ePhone.getText().toString();
            String Type = eType.getText().toString();

            if (Name.equals("")){
                Toast.makeText(getActivity(), "Blank Field!", Toast.LENGTH_SHORT).show();
            }
            else {
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                String currDT = "_" + day + ":" + (month + 1) + ":" + year + "_"+hour + ":" + minute + ":" + second;

                Model model = new Model(Name,Phone,Type);
                FirebaseDatabase fdb = FirebaseDatabase.getInstance();
                DatabaseReference root2 = fdb.getReference();
                root2.child("SURAKSHAK")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("CONTACTS")
                        .child(currDT)
                        .setValue(model);
                dialog.dismiss();
            }

        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}