package com.example.in.kielection;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class FormData {

    private String CandidateName, Value , PartyName,cId,pId, categoryId;

    private EditText etVote;

    private CheckBox leadCheck;


    public FormData() {
    }

    public FormData(String candidateName, String value, String partyName,String cId,String pId,String categoryId,EditText etVote,CheckBox leadCheck) {
        this.CandidateName = candidateName;
        this.Value = value;
        this.PartyName = partyName;
        this.cId = cId;
        this.pId = pId;
        this.categoryId = categoryId;
        this.etVote=etVote;
        this.leadCheck=leadCheck;
    }

    public String getCatId() {
        return categoryId;
    }

    public String getCId() {
        return cId;
    }

    public String getpId() {
        return pId;
    }

    public String getCandidateName() {
        return CandidateName;
    }

    public void setCandidateName(String name) {
        this.CandidateName = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        this.Value = value;
    }

    public String getPartyName() {
        return PartyName;
    }

    public void setPartyName(String partyName) {
        this.PartyName = partyName;
    }

    public EditText getVote() {
        return etVote;
    }

    public void setVote(EditText etVote) {
        this.etVote = etVote;
    }

    public CheckBox getLead() {
        return leadCheck;
    }

    public void setLead(CheckBox leadCheck) {
        this.leadCheck = leadCheck;
    }


}