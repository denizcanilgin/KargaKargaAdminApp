package com.example.kkadmin2.Models;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class Applicant {

    String FULLNAME;
    String USERID;
    String TITLE;
    String EMAIL;
    String PHONE;
    String UNIVERSITY;
    String DEPARTMENT;
    String SCENARIO;
    Boolean TESTCOMPLETED;
    ArrayList<String> COMMUNITIES;

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getDEPARTMENT() {
        return DEPARTMENT;
    }

    public void setDEPARTMENT(String DEPARTMENT) {
        this.DEPARTMENT = DEPARTMENT;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public Applicant(String FULLNAME, String USERID, String EMAIL,String PHONE, String TITLE, String UNIVERSITY, String SCENARIO , ArrayList<String> COMMUNITIES,Boolean TESTCOMPLETED,String DEPARTMENT) {
        this.FULLNAME = FULLNAME;
        this.UNIVERSITY = UNIVERSITY;
        this.COMMUNITIES = COMMUNITIES;
        this.SCENARIO = SCENARIO;
        this.USERID = USERID;
        this.TITLE = TITLE;
        this.EMAIL = EMAIL;
        this.TESTCOMPLETED = TESTCOMPLETED;
        this.PHONE = PHONE;
        this.DEPARTMENT = DEPARTMENT;
    }

    public Boolean getTESTCOMPLETED() {
        return TESTCOMPLETED;
    }

    public void setTESTCOMPLETED(Boolean TESTCOMPLETED) {
        this.TESTCOMPLETED = TESTCOMPLETED;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getSCENARIO() {
        return SCENARIO;
    }

    public void setSCENARIO(String SCENARIO) {
        this.SCENARIO = SCENARIO;
    }

    public String getFULLNAME() {
        return FULLNAME;
    }

    public void setFULLNAME(String FULLNAME) {
        this.FULLNAME = FULLNAME;
    }

    public String getUNIVERSITY() {
        return UNIVERSITY;
    }

    public void setUNIVERSITY(String UNIVERSITY) {
        this.UNIVERSITY = UNIVERSITY;
    }

    public ArrayList<String> getCOMMUNITIES() {
        return COMMUNITIES;
    }

    public void setCOMMUNITIES(ArrayList<String> COMMUNITIES) {
        this.COMMUNITIES = COMMUNITIES;
    }
}
