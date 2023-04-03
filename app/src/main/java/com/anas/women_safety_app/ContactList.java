package com.anas.women_safety_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ContactList extends AppCompatActivity {

    ArrayList<String> contactArrayList;
    ArrayAdapter adapter;

    ListView listView;
    SearchView searchView;
    int delayInt=0;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        listView=findViewById(R.id.listView);
        searchView=findViewById(R.id.searchView);
        contactArrayList = new ArrayList<>();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        if (ContextCompat.checkSelfPermission(ContactList.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_CONTACTS}, 1);
//        }

        ContentResolver contentResolver = getContentResolver();
        String[] projection = {ContactsContract.Contacts.DISPLAY_NAME};
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,projection,null,null,ContactsContract.Contacts.DISPLAY_NAME);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactArrayList.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            }
            cursor.close();
            adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,contactArrayList);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ContactList.this,CallActivity.class);
                intent.putExtra("Contactname",parent.getItemAtPosition(position).toString());
                String delay = getIntent().getStringExtra("Delay1");
                delayInt = Integer.parseInt(delay)*60*1000;

                Toast.makeText(ContactList.this, "Calling in "+delay.toString()+" min", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        finish();
                        startActivity(intent);
                    }
                },delayInt);

            }
        });





    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}