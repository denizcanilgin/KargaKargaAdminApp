package com.example.kkadmin2.Models;

import java.util.ArrayList;

public class Club {
    String title;
    ArrayList<Applicant> members;

    public Club(String title, ArrayList<Applicant> members) {
        this.title = title;
        this.members = members;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Applicant> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Applicant> members) {
        this.members = members;
    }
}
