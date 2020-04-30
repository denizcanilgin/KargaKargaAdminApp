package com.example.kkadmin2.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kkadmin2.App;
import com.example.kkadmin2.Models.Applicant;
import com.example.kkadmin2.Models.Club;
import com.example.kkadmin2.R;
import com.example.kkadmin2.Sorters.RemoveDublicates;
import com.example.kkadmin2.Sorters.TeamMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TeamCreatorActivity extends AppCompatActivity {

    App app;
    ListView ly_sorted_apps;
    ArrayAdapter<String> sorted_apps_adapter;

    ArrayList<String> sorted_apps_step1;
    ArrayList<Applicant> sorted_apps_step1_custom_team_members;
    ArrayList<Club> sorted_apps_step2_custom_clubs;

    TextView tv_step1_1, tv_step1_2, tv_step2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamcreator);

        app = (App) getApplication();
        ly_sorted_apps = findViewById(R.id.ly_sorted_apps);
        sorted_apps_step1 = new ArrayList<>();
        sorted_apps_step1_custom_team_members = new ArrayList<>();
        sorted_apps_step2_custom_clubs = new ArrayList<>();

        tv_step1_1 = findViewById(R.id.tv_step1_1);
        tv_step1_2 = findViewById(R.id.tv_step1_2);
        tv_step2 = findViewById(R.id.tv_step2);

        calStep1();
        calStep2();

//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                //TODO your background code
//                bubbleSortClubs();
//            }
//        });
//

        sorted_apps_adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, sorted_apps_step1);
        ly_sorted_apps.setAdapter(sorted_apps_adapter);


    }

    private void calStep1() {

        //Sort on InovaTIM
        for (Applicant applicant : app.getApplicants()) {

            String communities = applicant.getCOMMUNITIES().toString();
            Log.i("communinities", "" + communities.toLowerCase());

            if (communities.toLowerCase().contains("inovatim") || communities.toLowerCase().contains("i̇novati̇m") || communities.toLowerCase().contains("ino")) {

                sorted_apps_step1.add(applicant.getFULLNAME() + ", " + applicant.getCOMMUNITIES());

            } else {

                sorted_apps_step1_custom_team_members.add(applicant);

            }

        }


        setStep1();

    }

    private void setStep1() {
        tv_step1_1.setText("İnovatTİMli olanlar (" + sorted_apps_step1.size() + ")");
        tv_step1_2.setText("İnovatTİMli olmayanlar (" + (app.getApplicants().size() - sorted_apps_step1.size()) + ")");

    }

    private void calStep2() {
        //Create teams
        for (Applicant applicant : sorted_apps_step1_custom_team_members) {

            for (String applicant_club : applicant.getCOMMUNITIES()) {

                Club club = new Club(applicant_club, null);
                sorted_apps_step2_custom_clubs.add(club);

            }

        }

        ArrayList<String> str_clubs = new ArrayList<>();
        for (Club c : sorted_apps_step2_custom_clubs) {
            str_clubs.add(c.getTitle().toLowerCase().trim());
        }


        ArrayList<String>
                noDublicatesClubList = RemoveDublicates.removeDuplicates(str_clubs);


        for (Object c : noDublicatesClubList) {
            Log.i("R_DUB_CLUB ", "" + c);
        }


        sorted_apps_step2_custom_clubs.clear();
        for (String applicant_club : noDublicatesClubList) {
            ArrayList<Applicant> members = new ArrayList<Applicant>();
            Club club = new Club(applicant_club, members);
            club.setMembers(members);
            sorted_apps_step2_custom_clubs.add(club);
        }

        ArrayList<Club> teams_filled = TeamMatcher.findTeamAndAdd(sorted_apps_step1_custom_team_members, sorted_apps_step2_custom_clubs);
        Toast.makeText(getApplicationContext(),"m" + teams_filled.size(),0).show();

        for(Club club : teams_filled){

            Log.i("filled_team_size ",  club.getTitle()+ " - " + club.getMembers().size());


        }

        setStep2(noDublicatesClubList.size());

    }

    private void setStep2(int number){

        tv_step2.setText("İnovatTİM Harici Takım/Topluluk Sayısı " + "(" + number + ")");

    }


    private void bubbleSortClubs() {

        boolean sort = false;
        while (!sort) {
            sort = true;
            for (int i = 0; i < sorted_apps_step2_custom_clubs.size() - 1; i++) {

                Log.i("Sorting ", "...");

//                Log.i("CLUB "," " + sorted_apps_step2_custom_clubs.get(i).getTitle() );

                String pClubTitle = sorted_apps_step2_custom_clubs.get(i).getTitle();
                String nClubTitle = sorted_apps_step2_custom_clubs.get(i + 1).getTitle();

                if (pClubTitle.length() > nClubTitle.length()) {

                    Club c_temp = sorted_apps_step2_custom_clubs.get(i);
                    sorted_apps_step2_custom_clubs.set(i, sorted_apps_step2_custom_clubs.get(i + 1));
                    sorted_apps_step2_custom_clubs.set(i + 1, c_temp);
                    sort = false;

                }
            }

        }

//        for (Club c : sorted_apps_step2_custom_clubs) {
//            Log.i("CLUB ", "" + c.getTitle());
//        }


    }


}
