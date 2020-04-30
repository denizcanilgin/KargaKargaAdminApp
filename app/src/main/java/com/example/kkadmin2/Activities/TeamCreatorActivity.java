package com.example.kkadmin2.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kkadmin2.App;
import com.example.kkadmin2.Models.Applicant;
import com.example.kkadmin2.Models.Club;
import com.example.kkadmin2.R;

import java.util.ArrayList;

public class TeamCreatorActivity extends AppCompatActivity {

    App app;
    ListView ly_sorted_apps;
    ArrayAdapter<String> sorted_apps_adapter;

    ArrayList<String> sorted_apps_step1;
    ArrayList<Applicant> sorted_apps_step1_custom_team_members;
    ArrayList<Club> sorted_apps_step2_custom_clubs;

    TextView tv_step1_1, tv_step1_2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamcreator);

        app = (App)getApplication();
        ly_sorted_apps = findViewById(R.id.ly_sorted_apps);
        sorted_apps_step1 = new ArrayList<>();
        sorted_apps_step1_custom_team_members = new ArrayList<>();
        sorted_apps_step2_custom_clubs = new ArrayList<>();

        tv_step1_1 = findViewById(R.id.tv_step1_1);
        tv_step1_2 = findViewById(R.id.tv_step1_2);

        calStep1();


        sorted_apps_adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,sorted_apps_step1);
        ly_sorted_apps.setAdapter(sorted_apps_adapter);


        setStep1();
    }

    private void calStep1(){

        //Sort on InovaTIM
        for(Applicant applicant : app.getApplicants()){

            String communities = applicant.getCOMMUNITIES().toString();
            Log.i("communinities","" + communities.toLowerCase());

            if(communities.toLowerCase().contains("inovatim") || communities.toLowerCase().contains("i̇novati̇m") || communities.toLowerCase().contains("inova")){

                sorted_apps_step1.add(applicant.getFULLNAME() + ", " + applicant.getCOMMUNITIES());

            }else{

                sorted_apps_step1_custom_team_members.add(applicant);

            }

        }

    }

    private void setStep1(){
        tv_step1_1.setText("Inovatimli olanlar (" + sorted_apps_step1.size() +")");
        tv_step1_2.setText("Inovatimli olmayanlar (" + (app.getApplicants().size() - sorted_apps_step1.size()) +")");

    }

    private void calStep2(){
        //Create teams
        for(Applicant applicant : sorted_apps_step1_custom_team_members){

            for(String applicant_club : applicant.getCOMMUNITIES()){

                for(Club custom_club : sorted_apps_step2_custom_clubs){

                    if(custom_club.getTitle().toLowerCase().contains(applicant_club.toLowerCase())){



                    }else{

//                        sorted_apps_step2_custom_clubs.add(new Club(applicant_club,));

                    }

                }

            }

        }

    }





}
