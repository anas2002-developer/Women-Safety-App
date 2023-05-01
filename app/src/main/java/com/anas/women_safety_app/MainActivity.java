package com.anas.women_safety_app;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.anas.women_safety_app.FRAGS.ContactFrag;
import com.anas.women_safety_app.FRAGS.FakeCallFrag;
import com.anas.women_safety_app.FRAGS.HelplineFrag;
import com.anas.women_safety_app.FRAGS.HomeFrag;
import com.anas.women_safety_app.FRAGS.ProfileFrag;
import com.anas.women_safety_app.FRAGS.SettingsFrag;
import com.anas.women_safety_app.FRAGS.SirenPlayFrag;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout layDL;
    NavigationView vNV;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        layDL = findViewById(R.id.layDL);
        vNV = findViewById(R.id.vNV);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, layDL, toolbar, R.string.open_drawer, R.string.close_drawer);


        layDL.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            loadFrag(new HomeFrag());
            vNV.setCheckedItem(R.id.row_home);
        }
        NavClick();





    }

    private void NavClick() {
        vNV.setNavigationItemSelectedListener(item -> {
            Fragment frag = null;
            switch (item.getItemId()) {

                case R.id.row_home:
                    frag = new HomeFrag();
                    break;
                case R.id.row_contact:
                    frag = new ContactFrag();
                    break;
                case R.id.row_helpline:
                    frag = new HelplineFrag();
                    break;
                case R.id.row_sirenplay:
                    frag = new SirenPlayFrag();
                    break;
                case R.id.row_fakecall:
                    frag = new FakeCallFrag();

                    break;
                case R.id.row_profile:
                    frag = new ProfileFrag();

                    break;
                case R.id.row_settings:
                    frag = new SettingsFrag();

                    break;
                case R.id.row_howtouse:
//                    frag = new HowtouseFragment();
                    Toast.makeText(this, "How to use", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.row_feedback:
                    feedback();
                    break;
                case R.id.row_share:
                    Share();
                    break;
                case R.id.row_community:
                    Toast.makeText(this, "community support", Toast.LENGTH_SHORT).show();
                    break;
            }
            layDL.closeDrawer(GravityCompat.START);

            if (frag != null) {
                loadFrag(frag);
            }

            return true;
        });
    }

    private void Share() {

        Intent iShare=new Intent(Intent.ACTION_SEND);
        iShare.setType("text/plain");
        iShare.putExtra(Intent.EXTRA_TEXT,"Download Women Safety App, https://drive.google.com/file/d/12_kTsi_8LvP5WSHyq47Wz7HwTDQ4Ztb5/view?usp=sharing");
        startActivity(Intent.createChooser(iShare,"Share via"));

    }

    private void feedback() {
        Intent iEmail=new Intent(Intent.ACTION_SEND);
        iEmail.setType("message/rfc822");
        iEmail.putExtra(Intent.EXTRA_EMAIL,new String[]{
                "anas4112002@gmail.com"
                ,"shrutisaini1415@gmail.com"
        });
        iEmail.putExtra(Intent.EXTRA_SUBJECT,"Bug Fix");
        iEmail.putExtra(Intent.EXTRA_TEXT,"Please, Fix the bugs");
        startActivity(Intent.createChooser(iEmail,"Email via"));
    }


    private void loadFrag(Fragment frag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layFL, frag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.layFL);
        if (layDL.isDrawerOpen(GravityCompat.START)){
            layDL.closeDrawer(GravityCompat.START);
        }
        else if(currFrag!=new HomeFrag()) {
            loadFrag(new HomeFrag());
            vNV.setCheckedItem(R.id.row_home);
        }
        else {
            super.onBackPressed();
        }
    }
}