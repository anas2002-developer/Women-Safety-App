package com.anas.women_safety_app.MODELS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.anas.women_safety_app.LoginActivity;

public class Session {

    Context context;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    private final  String SP_SNAME = "Attendify";


    public Session(Context context) {
        this.context = context;
        sp=context.getSharedPreferences(SP_SNAME,Context.MODE_PRIVATE);
        editor=sp.edit();
    }

    public void saveUser(String email, String pass){

        editor.putString("SP_EMAIL",email);
        editor.putString("SP_PASS",pass);
        editor.putBoolean("SP_LOGGED_IN",true);
        editor.commit();

    }

    public boolean checkUser(){
        return sp.contains("SP_LOGGED_IN");
    }

    public void logoutUser(){

        editor.clear();
        editor.commit();

        context.startActivity(new Intent(context, LoginActivity.class));

    }

    public String infoUser(String key){
        return sp.getString(key,null);
    }




}
