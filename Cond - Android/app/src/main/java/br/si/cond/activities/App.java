package br.si.cond.activities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.si.cond.model.User;

public class App {
    public static Integer escritorio_id=1;

    public static void saveReadedInfo(Context c) {
        SharedPreferences prefs = c.getSharedPreferences("APPDATA", 0);
        SharedPreferences.Editor prefsEdit= prefs.edit();
        Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

        prefsEdit.putString("readedInfo", "1");

        prefsEdit.commit();
    }


    public static String isReaded(Context c) {
        SharedPreferences prefs = c.getSharedPreferences("APPDATA", 0);
        String json = prefs.getString("readedInfo","-1");

        return json;
    }

    public static void saveUser(Context c,User user){

        SharedPreferences prefs = c.getSharedPreferences("USERDATA", 0);
        SharedPreferences.Editor prefsEdit= prefs.edit();
        Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        String json = gson.toJson(user);
        prefsEdit.putString("strUserData", json);

        prefsEdit.commit();
    }

    public static void logout(Context c){

        SharedPreferences prefs = c.getSharedPreferences("USERDATA", 0);
        SharedPreferences.Editor prefsEdit= prefs.edit();
        prefsEdit.putString("strUserData", "-1");

        prefsEdit.commit();
    }

    public static User getUser(Context c){
        User user = new User();
        SharedPreferences prefs = c.getSharedPreferences("USERDATA", 0);
        Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        String json = prefs.getString("strUserData","-1");

        if(json.equalsIgnoreCase("-1"))
            return null;
        else{
            user = gson.fromJson(json,User.class);
        }

        return user;

    }
}
