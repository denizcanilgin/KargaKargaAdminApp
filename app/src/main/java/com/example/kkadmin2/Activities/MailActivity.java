package com.example.kkadmin2.Activities;

import android.app.Application;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kkadmin2.App;
import com.example.kkadmin2.Mail.Mail;
import com.example.kkadmin2.Models.Applicant;
import com.example.kkadmin2.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class MailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_senderAcc,tv_numberOfNotFilledTest,tv_changeSenderAcc,tv_seeWhoNotFilled;
    private Spinner sp_receivers;
    private LinearLayout ly_customReceivers,ly_notFilledTest;
    private EditText et_mailContent,et_mailTitle;

    private Mail currentSenderAccount;
    private int  currentReceiverGroup = 0; //Custom Group By Default

    private EditText et_customReceivers;
    private Button bt_sendMail;


    private static Mail m1 = new Mail("gakkarga1@gmail.com", "Elvis.1789", "smtp.gmail.com","465","465");
    private static Mail m2 = new Mail("merhaba@gfkgenclikplatformu.com", "GFK2423gfk", "mail.gfkgenclikplatformu.com","465","465");

    App app;
    ArrayList<Applicant> applicants;
    ArrayList<Applicant> applicants_not_filled_test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        app = (App) getApplication();

        initViews();
        updateSenderMailAddressAndViews(m1);

    }

    private void initViews(){


        applicants = new ArrayList<>();
        applicants_not_filled_test = new ArrayList<>();

        et_mailTitle = findViewById(R.id.et_mailTitle);
        tv_seeWhoNotFilled = findViewById(R.id.tv_seeWhoNotFilled);
        tv_seeWhoNotFilled.setOnClickListener(this);
        tv_senderAcc = findViewById(R.id.tv_senderAcc);
        tv_numberOfNotFilledTest = findViewById(R.id.tv_numberOfNotFilledTest);
        ly_customReceivers = findViewById(R.id.ly_customReceivers);
        et_customReceivers = findViewById(R.id.et_customReceivers);
        bt_sendMail = findViewById(R.id.bt_sendMail);
        et_mailContent = findViewById(R.id.et_mailContent);
        ly_notFilledTest = findViewById(R.id.ly_notFilledTest);
        bt_sendMail.setOnClickListener(this);

        sp_receivers = findViewById(R.id.sp_receivers);
        String[] receivers = {"Özel Kullanıcılar","Öz Gelecek Testini Doldurmayanlar","Yarışmaya Başvurmayanlar"};
        ArrayAdapter receivers_adapter = new ArrayAdapter(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,receivers);
        sp_receivers.setAdapter(receivers_adapter);

        sp_receivers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                currentReceiverGroup = position;


                if(position == 0 ){

                    ly_customReceivers.setVisibility(View.VISIBLE);

                }else{

                    ly_customReceivers.setVisibility(View.GONE);


                }

                if(position == 1){

                    applicants_not_filled_test.clear();
                    ly_notFilledTest.setVisibility(View.VISIBLE);
                    applicants = app.getApplicants();
                    findNumberOfNotTestSolvers();
                    tv_numberOfNotFilledTest.setText( applicants_not_filled_test.size()+ " Kişi testi çözmedi");


                }else{

                    ly_notFilledTest.setVisibility(View.GONE);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tv_changeSenderAcc = findViewById(R.id.tv_changeSenderAcc);
        tv_changeSenderAcc.setOnClickListener(this);

    }

    private void findNumberOfNotTestSolvers(){

        for(Applicant applicant : applicants){

            Boolean testFilled = applicant.getTESTCOMPLETED();

            if(!testFilled){

                applicants_not_filled_test.add(applicant);

            }

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {

            case R.id.tv_changeSenderAcc:

                showDialogToSetSenderAcc();

                break;

            case R.id.bt_sendMail:

                sendMail(et_mailContent.getText().toString(),et_mailTitle.getText().toString());

                break;

            case R.id.tv_seeWhoNotFilled:

                showAllReceivers(applicants_not_filled_test);

                break;

        }
    }

    private void showAllReceivers(ArrayList<Applicant> receivers) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_all_receivers);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);

        ArrayList<String> emails = new ArrayList<>();

        for(Applicant applicant : receivers){

            emails.add(applicant.getEMAIL() + "");

        }

        ListView lv_allreceivers = dialog.findViewById(R.id.lv_allreceivers);
        ArrayAdapter<String> receivers_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,emails);


        dialog.show();

        lv_allreceivers.setAdapter(receivers_adapter);
    }


    private void showDialogToSetSenderAcc(){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_set_sender_acc);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);

        Button bt_setAcc = dialog.findViewById(R.id.bt_setAcc);
        final Spinner sp_accounts = dialog.findViewById(R.id.sp_accounts);

        String[] accs = {m1.get_user(),m2.get_user()};
        ArrayAdapter accs_adapter = new ArrayAdapter(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,accs);
        sp_accounts.setAdapter(accs_adapter);

        bt_setAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (sp_accounts.getSelectedItemPosition())
                {
                    case 0: updateSenderMailAddressAndViews(m1); break;
                    case 1: updateSenderMailAddressAndViews(m2); break;
                }

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void updateSenderMailAddressAndViews(Mail acc){

        this.currentSenderAccount = acc;
        tv_senderAcc.setText(this.currentSenderAccount.get_user() + "");

    }


    private void sendMail(String content,String title){

        String[] recipients = new String[10000]; 

        if(currentReceiverGroup == 0)
            recipients = et_customReceivers.getText().toString().trim().split(",");
        else if(currentReceiverGroup == 1){

            for(int i = 0; i < applicants_not_filled_test.size() ; i++){

                String email = applicants_not_filled_test.get(i).getEMAIL();
                recipients[i] = email;

            }



        }else if(currentReceiverGroup == 2){

            makeToast("Alıcı seçilmedi!");
            return;
        }

        makeToast(recipients.length + " Kişiye gönderiliyor...");
        MailActivity.SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.mailActivity = this;
        email.m = currentSenderAccount;
        email.m.setBody(content);
        email.m.set_to(recipients);
        email.m.set_subject(title);
        email.execute();
    }



    public class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        Mail m;
        MailActivity mailActivity;

        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    Log.i("EMAIL_STATE", "Email sent.");
                } else {
                    Log.i("EMAIL_STATE", "Email failed to send.");
                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(MailActivity.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
//                makeToast("Authentication failed.");
                Log.i("EMAIL_STATE", "Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(MailActivity.SendEmailAsyncTask.class.getName(), "Email failed");
                e.printStackTrace();

                Log.i("EMAIL_STATE", "Email failed to send.");
                return false;
            } catch (Exception e) {
                e.printStackTrace();

                Log.i("EMAIL_STATE", "Unexpected error occured.");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

//            if(aBoolean)
//                makeToast("Başarıyla gönderildi!");
//            else
//                makeToast("Bir hata meydana geldi!");


        }
    }

    private void makeToast(String s){Toast.makeText(getApplicationContext(),s,0).show();}


}
