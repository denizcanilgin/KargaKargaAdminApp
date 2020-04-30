package com.example.kkadmin2;

import android.app.Application;
import android.util.Log;

import com.example.kkadmin2.Models.Applicant;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private static final String APP_ID = "a9d929723df03e40fd4a4e513e3616543c03339f";
    private static final String CLIENT_KEY = "cde79a1ba26a016634f09e0669917be36773d868";
    private static final String SERVER_URL = "http://3.214.66.12/parse";
    private static final String DATABASE_URI = "mongodb://root:MJ5SMF8L4sY7@127.0.0.1:27017/bitnami_parse";
    //jPZebH4UJLD5



    public ArrayList<Applicant> applicants;

    public ArrayList<Applicant> getApplicants() {
        return applicants;
    }

    public void setApplicants(ArrayList<Applicant> applicants) {
        this.applicants = applicants;
    }

    @Override
    public void onCreate() {
        super.onCreate();



        Log.i("PARSE", "Initializing...");
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID)
                .enableLocalDataStore()
                .clientKey(CLIENT_KEY)
                .server(SERVER_URL)
                .build()
        );


    }

}
