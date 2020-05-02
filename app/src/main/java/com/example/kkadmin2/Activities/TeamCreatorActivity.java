package com.example.kkadmin2.Activities;

        import android.app.Dialog;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import com.example.kkadmin2.App;
        import com.example.kkadmin2.Models.Applicant;
        import com.example.kkadmin2.Models.Club;
        import com.example.kkadmin2.Models.University;
        import com.example.kkadmin2.R;
        import com.example.kkadmin2.Sorters.RemoveDublicates;
        import com.example.kkadmin2.Sorters.TeamMatcher;
        import com.example.kkadmin2.Sorters.UniversityMatcher;

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
    ArrayList<University> sorted_apps_step3_custom_unis;

    TextView tv_step1_1, tv_step1_2, tv_step2,tv_step3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamcreator);

        app = (App) getApplication();
        ly_sorted_apps = findViewById(R.id.ly_sorted_apps);
        sorted_apps_step1 = new ArrayList<>();
        sorted_apps_step1_custom_team_members = new ArrayList<>();
        sorted_apps_step2_custom_clubs = new ArrayList<>();
        sorted_apps_step3_custom_unis = new ArrayList<>();

        tv_step1_1 = findViewById(R.id.tv_step1_1);
        tv_step1_2 = findViewById(R.id.tv_step1_2);
        tv_step2 = findViewById(R.id.tv_step2);
        tv_step3 = findViewById(R.id.tv_step3);

        calStep1();
        calStep2();
        calStep3();

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

    //Clubs
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
        Toast.makeText(getApplicationContext(), "m" + teams_filled.size(), 0).show();

        for (Club club : teams_filled) {

            Log.i("filled_team_size ", club.getTitle() + " - " + club.getMembers().size());


        }

        setStep2(noDublicatesClubList.size());

    }

    private void setStep2(int number) {

        tv_step2.setText("İnovatTİM Harici Takım/Topluluk Sayısı " + "(" + number + ")");

    }

    //Universities
    private void calStep3(){

        for (Applicant applicant : app.getApplicants()) {

            String university = applicant.getUNIVERSITY();
//            String[] universities = university.split("8888");
            Log.i("UNI_NAME", "" + university);

                University university1 = new University(university,new ArrayList<Applicant>());
                sorted_apps_step3_custom_unis.add(university1);

        }


        for(int i  = 0 ; i < sorted_apps_step3_custom_unis.size() -1  ; i++){
        }

        //Uni to string

        ArrayList<String> uni_titles = new ArrayList<>();

        for(University uni : sorted_apps_step3_custom_unis){

            String uni_title = uni.getTitle().toLowerCase().trim();
            uni_titles.add(uni_title);

        }

        Log.i("BEFORE_DUBLICATE_UNI", "" + uni_titles.size());


        ArrayList<String>
                noDublicatesUniList = RemoveDublicates.removeDuplicates(uni_titles);
        setStep3(noDublicatesUniList.size());

        ArrayList<University> uniNoDublicatesUniList = new ArrayList<>();

        Log.i("AFTER_DUBLICATE_UNI", "" + noDublicatesUniList.size());

        for(String str : noDublicatesUniList){

            Log.i("NAME_AfterSORT", "" + str);
            University uni = new University(str,new ArrayList<Applicant>());
            uniNoDublicatesUniList.add(uni);

        }


        final ArrayList<University> teams_filled = UniversityMatcher.findTeamAndAdd(app.getApplicants(), uniNoDublicatesUniList);
        Toast.makeText(getApplicationContext(), "m" + teams_filled.size(), 0).show();

        for (University university : teams_filled) {

            Log.i("filled_uni_size ", university.getTitle() + " - " + university.getMembers().size());


        }

        tv_step3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> str_dialog = new ArrayList<>();
                for (University university : teams_filled) {

                    Log.i("filled_uni_size ", university.getTitle() + " - " + university.getMembers().size());
                    str_dialog.add(university.getTitle() + "  " + university.getMembers().size());
                }

                showDetailedList(str_dialog);
            }
        });


    }

    private void setStep3(int i){

        tv_step3.setText("Yarışmaya Başvuran Üniversite Sayısı" + " (" + i + ") ");

    }

    private void showDetailedList(ArrayList<String> list){

        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_listview);
        d.setCancelable(true);

        ListView lv_listview = d.findViewById(R.id.lv_listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,list);
        lv_listview.setAdapter(adapter);

        d.show();
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
