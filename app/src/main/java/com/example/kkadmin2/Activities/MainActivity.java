package com.example.kkadmin2.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kkadmin2.Adapters.ApplicationAdapter;
import com.example.kkadmin2.App;
import com.example.kkadmin2.Models.Applicant;
import com.example.kkadmin2.R;
import com.example.kkadmin2.TinyDB.TinyDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<ParseObject> list_android_applications;
    ArrayList<ParseObject> list_IOS_applications;

    ArrayList<ParseObject> list_all_applications;
    ArrayList<ParseObject> list_apps_without_test_solved;
    ArrayList<Applicant> list_applicant;

    ApplicationAdapter applicationAdapter;

    ListView lv_applications;

    TextView tv_totalTeamNumber, tv_totalApplication, tv_AndroidApps, tv_IOSApps ,tv_pandemiOncesi,tv_pandemiSırası,tv_pandemiSonrası,tv_universityNumber;

    Button bt_NoTestSolvedList, bt_allapplications;

    ImageView iv_loading;

    boolean loaded = false;

    App app;

    int p = 0;
    int d = 0;
    int a = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (App) getApplication();

        init();


        RetrieveAndroidApplications();




    }

    private boolean checkLocalsIsEmpty(String key, Boolean type){

        if(retrieveLocalDB(key,type) != null) {
            return true;
        }else
            return false;

    }


    private void saveToLocalDB(String key, ArrayList list){

        Gson gson = new Gson();
        String json = gson.toJson(list);

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        prefsEditor.putString(key, json);
        prefsEditor.commit();

        Log.d("TAG","key = " + json);
    }

    private ArrayList retrieveLocalDB(String key, boolean type) {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(key, "");


        Type typex = null;
        if (type) {
            typex = new TypeToken<List<Applicant>>() {
            }.getType();
            ArrayList<Applicant> applicantsApp = gson.fromJson(json, typex);
            return applicantsApp;


        } else {
            typex = new TypeToken<List<ParseObject>>() {
            }.getType();
            ArrayList<ParseObject> applicantsParse = gson.fromJson(json, typex);
            return applicantsParse;

        }




    }

    private void init() {
        iv_loading = findViewById(R.id.iv_loading);
        Animation rotation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_anim);
        rotation.setFillAfter(true);
        iv_loading.startAnimation(rotation);


        bt_NoTestSolvedList = findViewById(R.id.bt_notestsolvedlist);
        bt_allapplications = findViewById(R.id.bt_allapplications);

        buttonEffect(bt_NoTestSolvedList);
        buttonEffect(bt_allapplications);

        tv_totalApplication = findViewById(R.id.tv_totalApplication);
        tv_totalTeamNumber = findViewById(R.id.tv_totalTeamNumber);
        tv_AndroidApps = findViewById(R.id.tv_AndroidApps);
        tv_IOSApps = findViewById(R.id.tv_IOSApps);
//        tv_universityNumber = findViewById(R.id.tv_universityNumber);

        tv_pandemiSonrası = findViewById(R.id.tv_pandemiSonrası);
        tv_pandemiSırası = findViewById(R.id.tv_pandemiSırası);
        tv_pandemiOncesi = findViewById(R.id.tv_pandemiOncesi);

        lv_applications = findViewById(R.id.lv_applications);

        list_android_applications = new ArrayList<>();
        list_IOS_applications = new ArrayList<>();
        list_all_applications = new ArrayList<>();
        list_apps_without_test_solved = new ArrayList<>();
        list_applicant = new ArrayList<>();

        applicationAdapter = new ApplicationAdapter(this, R.layout.listview_item_applications, list_all_applications);
        lv_applications.setAdapter(applicationAdapter);

    }

    private void setScenarioCount(int p, int d, int a){

        tv_pandemiOncesi.setText(p + "");
        tv_pandemiSırası.setText(d + "");
        tv_pandemiSonrası.setText(a + "");

    }

    private void calScenarioCount(){


//        Android
        for(ParseObject applicant : list_android_applications){
            String scenario = applicant.get("scenario") + "";

            if(scenario.toLowerCase().equals("pandemi öncesi")){p++;
            }else if(scenario.toLowerCase().equals("pandemi sırası")){d++;}
            else if(scenario.toLowerCase().equals("pandemi sonrası")){a++;}
        }

//        IoS
        for(ParseObject applicant : list_IOS_applications){
            String scenario = applicant.get("select_application_object_id") + "";

            if(scenario.equals("iRinQb0MFb")){a++;
            }else if(scenario.equals("geF9XjGHcA")){d++;}
            else if(scenario.equals("NmHmsZVnD1")){p++;}
        }

        setScenarioCount(p,d,a);

    }

    private void RetrieveAndroidApplications() {

        ParseQuery<ParseObject> query_android = ParseQuery.getQuery("FujiFilmApplications");
        query_android.setLimit(10000);
        query_android.orderByAscending("createdAt");
        query_android.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    Log.i("SUCCEED", "" + objects.size());


                    for (ParseObject object : objects) {

                        list_android_applications.add(object);
                        list_all_applications.add(object);
                        applicationAdapter.notifyDataSetChanged();

                    }

                    RetrieveIOSApplications();

//                    saveToLocalDB("db_list_android_applications",list_android_applications);


                } else {

                    Log.i("ERROR", "" + e.getMessage());

                }
            }
        });

    }

    private void RetrieveIOSApplications() {

        ParseQuery<ParseObject> query_ios = ParseQuery.getQuery("Applications_competition");
        query_ios.setLimit(10000);
        query_ios.orderByAscending("createdAt");
        query_ios.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    Log.i("SUCCEED", "" + objects.size());

                    for (ParseObject object : objects) {

                        list_all_applications.add(object);
                        list_IOS_applications.add(object);
                        applicationAdapter.notifyDataSetChanged();

                    }

                    saveToLocalDB("db_list_all_applications",list_all_applications);

                    SetNumbers();
                    RetrieveAllDetails();
                    RetrieveUsers();
                    calScenarioCount();
                   // RetrieveApplicationsWithoutTestSolved();

                }
            }
        });

    }

    private void RetrieveApplicationsWithoutTestSolved() {

        for (final ParseObject applicant : list_all_applications) {

            String str_userId = "";

            if (applicant.get("userId") != null)
                str_userId = applicant.get("userId").toString();
            else
                str_userId = applicant.get("user_id").toString();


            ParseQuery<ParseObject> query_typo = ParseQuery.getQuery("test");
            query_typo.setLimit(1000);
            query_typo.whereEqualTo("userId", str_userId);
            query_typo.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    Log.i("SIZE_WITHOUT", "" + list_all_applications.size());

                    String q130 = "#";

                    if (objects != null) {

                        if (objects.size() > 0)
                            if (!objects.get(0).get("p1").equals("#"))
                                list_apps_without_test_solved.add(applicant);
                    } else {

//                        list_apps_without_test_solved.add(applicant);

                    }

                    Log.i("SIZE_WITHOUT_TEST", "" + list_apps_without_test_solved.size());


                }
            });


        }


    }

    private void RetrieveUsers(){

        final int[] i = {0};

        for (int a = 0 ; a < list_all_applications.size() ; a++) {
            String str_userId = "";
            if (list_all_applications.get(a).get("userId") != null)
                str_userId = list_all_applications.get(a).get("userId").toString();
            else
                str_userId = list_all_applications.get(a).get("user_id").toString();

             ParseQuery<ParseUser> query = ParseUser.getQuery();
             query.whereEqualTo("userId", str_userId);
             query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if(e == null){

                            Log.i("SIZ_USERS","" + i[0]++);

                        }
                    }
                });


        }

    }

    private final static int DO_UPDATE_TEXT = 0;
    private final static int DO_THAT = 1;
    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch(what) {
                case DO_UPDATE_TEXT:

                    loaded = true;
                    iv_loading.clearAnimation();
//                    iv_loading.setVisibility(View.INVISIBLE);
                    iv_loading.setImageResource(R.drawable.ic_done_all_black_24dp);
                    makeSound();
                    iv_loading.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(MainActivity.this,TeamCreatorActivity.class);
                            startActivity(i);

                        }
                    });

                    break;
            }
        }
    };

    private void LoginAdmin(){


        ParseUser.logInInBackground("admin", "admin", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.

                    Log.i("LOGGED IN","SUCCEED");


                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Log.i("LOGGED IN","ERROR");

                }
            }
        });

    }

    private void RetrieveAllDetails() {


        final int[] i = {0};

            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    i[0]++;

                    if(i[0] == 500)

                    {
                        myHandler.sendEmptyMessage(DO_UPDATE_TEXT);
                        return;

                    }

                    ParseObject applicant = list_all_applications.get(i[0]);

            String str_userId = "";
            String str_scenario = "";
            final String[] str_email = {""};
            final String[] str_university = {""};
            final String[] str_department = {""};
            final String[] str_fullname = {""};
            final String[] str_title = {""};
            final String[] str_community = {""};
            final String[] str_phone = {""};
                    final String[] edu = {""};
                    final String[] bd = {""};
                    final String[] gender = {""};

            if (applicant.get("userId") != null)
                str_userId = applicant.get("userId").toString();
            else
                str_userId = applicant.get("user_id").toString();

            if (applicant.get("scenario") != null)
                str_scenario = applicant.get("scenario").toString();
            else
                str_scenario = applicant.get("select_application_object_id").toString();


            ParseQuery<ParseObject> query_contact = ParseQuery.getQuery("contact_info");
                    query_contact.whereEqualTo("userId", str_userId);
                    query_contact.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null){

                        if(objects.size() > 0){

                            if(objects.get(0).get("eposta") != null)
                                str_email[0] = objects.get(0).get("eposta").toString();
                            if(objects.get(0).get("cep_telefonu") != null)
                                str_phone[0] = objects.get(0).get("cep_telefonu").toString();

//                            Log.i("EMAIL","" + objects.get(0).get("eposta"));


                        }

                    }
                }
            });


                    //USER CLASS
            final ParseQuery<ParseUser> query = ParseUser.getQuery();
            final String finalStr_userId = str_userId;
            final String finalStr_scenario = str_scenario;
            final String finalStr_userId1 = str_userId;
            query.whereEqualTo("objectId", str_userId);
                    final String finalStr_userId2 = str_userId;
                    final String finalStr_userId3 = str_userId;
                    query.findInBackground(new FindCallback<ParseUser>() {
               @Override
               public void done(List<ParseUser> objects, ParseException e) {
                   if (e == null) {

                       if(objects.size() > 0) {
                           Log.i("EMAIL",   ":" + objects.get(0).get("custom_email"));


//                           if(objects.get(0).get("email") != null)
//                                str_email[0] = objects.get(0).get("email").toString();
                           if (objects.get(0).get("first_name") != null)
                               if(objects.get(0).get("first_name").toString().length() > 3)
                                    str_fullname[0] = objects.get(0).get("first_name").toString();
                                else
                                   str_fullname[0] = objects.get(0).get("username").toString();
                            else
                                str_fullname[0] = objects.get(0).get("first_name").toString();

                           if(str_email[0].length() < 3)
                               if(objects.get(0).get("custom_email") != null)
                                    str_email[0] = str_email[0] + objects.get(0).get("custom_email").toString();

                           if (objects.get(0).get("title") != null)
                               str_title[0] = objects.get(0).get("title").toString();

                       }

                       Log.i("FULL_NAME","" + str_fullname[0]);

                       //EDUCATION CLASS
                       ParseQuery<ParseObject> query_education = ParseQuery.getQuery("education_info");
                       query_education.setLimit(100000);
                       query_education.whereEqualTo("userId", finalStr_userId1);
                       query_education.findInBackground(new FindCallback<ParseObject>() {
                           @Override
                           public void done(List<ParseObject> objects, ParseException e) {
                               if (e == null && objects.size() > 0) {

                                   if (objects.get(0).get("university") != null)
                                       str_university[0] = objects.get(0).get("university").toString();
                                   if (objects.get(0).get("faculty") != null)
                                       str_department[0] = (String) objects.get(0).get("faculty");

                               }

                               if (e == null && objects.size() > 1 &&  str_university[0].length() < 3) {
                                   if (objects.get(1).get("university") != null)
                                       str_university[0] = str_university[0] + "" +  objects.get(1).get("university").toString();
                                   if (objects.get(1).get("faculty") != null)
                                       str_department[0] =  str_university[0]  + (String) objects.get(1).get("faculty");

                               }

                               if (e == null && objects.size() > 2 && str_university[0].length() < 3) {
                                   if (objects.get(2).get("university") != null)
                                       str_university[0] = str_university[0] + "" +   objects.get(2).get("university").toString();
                                   if (objects.get(2).get("faculty") != null)
                                       str_department[0] =  str_university[0] +  (String) objects.get(2).get("faculty");
                               }

                               ParseQuery<ParseObject> query_community = ParseQuery.getQuery("community_info");
                               query_community.whereEqualTo("userId", finalStr_userId2);
                               query_community.findInBackground(new FindCallback<ParseObject>() {
                                   @Override
                                   public void done(List<ParseObject> objects, ParseException e) {
                                       if(e == null){

                                           final ArrayList<String> list_coms = new ArrayList<>();

//                                            for(ParseObject club : objects){
//
//                                                str_community[0] = str_community[0] + "," + club.get("community_name").toString();
//
//                                            }

                                            for(int i = 0 ; i < objects.size() ; i++){

                                                if(objects.get(i).get("community_name") != null)
                                                    if(objects.get(i).get("community_name").toString().length() > 2)
                                                        str_community[0] = str_community[0] + objects.get(i).get("community_name").toString()  + "#" ;

                                            }

                                            if(objects.size() > 0)
                                                str_community[0] = objects.get(0).get("community_name").toString();


                                           if(objects.size() > 1)
                                               str_community[0] = str_community[0] + "/" + objects.get(1).get("community_name").toString();

                                           if(objects.size() > 2)
                                               str_community[0] = str_community[0] + "/" + objects.get(2).get("community_name").toString();

                                           if(objects.size() > 3)
                                               str_community[0] = str_community[0] + "/" + objects.get(3).get("community_name").toString();


                                           Log.i("COMMMM", "" + str_community[0]);

                                           StringBuilder sb = new StringBuilder(str_community[0]);




                                           for(ParseObject object : objects){

                                                   list_coms.add(object.get("community_name").toString());

                                               }



                                               ParseQuery<ParseObject> query_test = ParseQuery.getQuery("test");
                                               query_test.whereEqualTo("userId", finalStr_userId3);
                                               query_test.findInBackground(new FindCallback<ParseObject>() {
                                                   @Override
                                                   public void done(List<ParseObject> objects, ParseException e) {
                                                       if(e == null){

                                                           String answerp130 = "#";
                                                           Boolean completed = false;


                                                           if(objects.size() > 0)
                                                               if(objects.get(0).get("p130") != null)
                                                                    answerp130 = objects.get(0).get("p130").toString();

                                                           if(!answerp130.equals("#")){

                                                               completed = true;

                                                           }

                                                           ParseQuery<ParseObject> query_pi = ParseQuery.getQuery("personal_info");
                                                           query_pi.whereEqualTo("userId", finalStr_userId3);
                                                           final Boolean finalCompleted = completed;
                                                           query_pi.findInBackground(new FindCallback<ParseObject>() {
                                                               @Override
                                                               public void done(List<ParseObject> objects, ParseException e) {
                                                                   if(e == null){

                                                                       if(objects.size() > 0){

                                                                           if(!objects.get(0).get("egitim").toString().contains("x"))
                                                                               edu[0] = objects.get(0).get("egitim") + "";
                                                                           if(objects.get(0).get("Egitim") != null)
                                                                               edu[0] = edu[0] + " / "  + objects.get(0).get("Egitim") + "";

                                                                           if(objects.get(0).get("dogum_tarihi") != null)
                                                                               if(objects.get(0).get("dogum_tarihi").toString().length() > 0)
                                                                                    bd[0] = objects.get(0).get("dogum_tarihi").toString();

                                                                           if(objects.get(0).get("Cinsiyet") != null)
                                                                               if(objects.get(0).get("Cinsiyet").toString().length() > 0)
                                                                                   gender[0] = objects.get(0).get("Cinsiyet").toString();



                                                                           Log.i("NEW_INFO", " AGE: " + bd[0] + " EDU: " +  edu[0] + " GENDER:  " + gender[0]);
                                                                           Applicant applicant1 = new Applicant(str_fullname[0], finalStr_userId, bd[0], edu[0], gender[0],str_email[0],str_phone[0],str_title[0],str_university[0], finalStr_scenario, list_coms,str_community[0], finalCompleted,str_department[0]);
                                                                           list_applicant.add(applicant1);
                                                                           app.setApplicants(list_applicant);
                                                                           Log.i("SIZE_APPLICANT", "" + list_applicant.size());
                                                                           saveToLocalDB("db_list_applicant",list_applicant);


                                                                       }

                                                                   }
                                                               }
                                                           });



                                                       }
                                                   }
                                               });




                                       }
                                   }
                               });




                           }
                       });


                   }
               }
           });
                    // do something
                    handler.postDelayed(this, 25L);  // 1 second delay
                }
            };
            handler.post(runnable);

        }

    public void export(ArrayList<Applicant> applicants) {
        //generate data


        if(!loaded){

            Toast.makeText(getApplicationContext(),"Hala Yükleniyor...", 0).show();
            return;

        }

        Toast.makeText(getApplicationContext(), "SIZE:" + applicants.size(), 0).show();

        String strUSERID = "";
        String strUSERNAME = "";
        String strEMAIL = "";
        String strPHONE = "";
        String strUNIVERSITY = "";
        String strDepartment = "";
        String strSCENARIO = "";
        String strCOMMUNITIES = "";
        String strTITLE = "";
        String strTESTCOMPLETED = "";

        String strGENDER = "";
        String strBD = "";
        String strEDUSTATUS = "";

        StringBuilder data = new StringBuilder();
        data.append("USERID,USERNAME,EMAIL,PHONE,BIRTH DATE,GENDER,EDU. STATUS,TITLE,UNIVERSITY,DEPARTMENT,SCENARIO,COMMUNITY,TEST COMPLETED");

        for (int i = 0; i < applicants.size(); i++) {

            strCOMMUNITIES = "";

            strGENDER = applicants.get(i).getGENDER().trim();
            strBD = applicants.get(i).getAGE().trim();
            strEDUSTATUS = applicants.get(i).getSTATUS().trim();

            strUSERNAME = applicants.get(i).getFULLNAME().trim();
            strUSERID = applicants.get(i).getUSERID().trim();
            strSCENARIO = applicants.get(i).getSCENARIO().trim();

            strUNIVERSITY = applicants.get(i).getUNIVERSITY().trim();


            strDepartment = applicants.get(i).getDEPARTMENT().trim();
            strEMAIL = applicants.get(i).getEMAIL().trim();
            strPHONE = applicants.get(i).getPHONE().trim();
            strTITLE = applicants.get(i).getTITLE().trim();
            strTESTCOMPLETED = applicants.get(i).getTESTCOMPLETED().toString().trim();

            Log.i("COMPLETEDD?"," " + strTESTCOMPLETED);
            Log.i("COMPLETEDD?"," " + strTESTCOMPLETED);


                        strCOMMUNITIES = applicants.get(i).getSTR_COMMUNITIES();


            Log.i("NEW_INFO", " AGE: " + strBD + " EDU: " +  strEDUSTATUS + " GENDER:  " + strGENDER);



//            if(applicants.get(i).getCOMMUNITIES().size() > 0 )
////                for(String title : applicants.get(i).getCOMMUNITIES())
////                        strCOMMUNITY = strCOMMUNITY + " / " + title;

            if(strUSERID.contains(","))
                strUSERID = strUSERID.replace(",","");
            if(strUSERNAME.contains(","))
                strUSERNAME = strUSERNAME.replace(",","");
            if(strEMAIL.contains(","))
                strEMAIL = strEMAIL.replace(",","");
            if(strPHONE.contains(","))
                strPHONE = strPHONE.replace(",","");
            if(strBD.contains(","))
                strBD = strBD.replace(",","");

            if(strGENDER.contains(","))
                strGENDER = strGENDER.replace(",","");
            if(strEDUSTATUS.contains(","))
                strEDUSTATUS = strEDUSTATUS.replace(",","");
            if(strTITLE.contains(","))
                strTITLE = strTITLE.replace(",","");

            if(strUNIVERSITY.contains(","))
                strUNIVERSITY = strUNIVERSITY.replace(",","");

            if(strDepartment.contains(","))
                strDepartment = strDepartment.replace(",","");
            if(strSCENARIO.contains(","))
                strSCENARIO = strSCENARIO.replace(",","");
            if(strCOMMUNITIES.contains(","))
                strCOMMUNITIES = strCOMMUNITIES.replace(",","");
            if(strSCENARIO.contains(","))
                strSCENARIO = strSCENARIO.replace(",","");

            data.append("\n" + strUSERID + "," + strUSERNAME + "," + strEMAIL + "," + strPHONE +"," + strBD + "," + strGENDER + "," + strEDUSTATUS +  "," + strTITLE + "," + strUNIVERSITY + "," + strDepartment + "," + strSCENARIO + "," + strCOMMUNITIES + "," + strTESTCOMPLETED);
        }

        try {
            //saving the file into device
            FileOutputStream out = openFileOutput("basvurular.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            //exporting
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "basvurular.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.exportcsv.fileprovider", filelocation);


            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "CSV'yi gönder"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void SetNumbers() {

        tv_totalApplication.setText(list_all_applications.size() + "");
        tv_totalTeamNumber.setText("" + (list_all_applications.size() / 4));
        tv_AndroidApps.setText(list_android_applications.size() + "");
        tv_IOSApps.setText(list_IOS_applications.size() + "");

    }

    public void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
//                        Toast.makeText(MainActivity.this,"asdasd",0).show();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }

                }

                if (v.getId() == R.id.bt_notestsolvedlist) export(list_applicant);
                if (v.getId() == R.id.bt_allapplications)  startActivity(new Intent(MainActivity.this,MailActivity.class));
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bt_notestsolvedlist:

//                Toast.makeText(getApplicationContext(), "asdasd", 0).show();

                break;


        }

    }

    private void makeSound(){

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
