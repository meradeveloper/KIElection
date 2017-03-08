package com.example.in.kielection;

import android.widget.CheckBox;
import android.widget.EditText;

public class ConstituencyData {

    private String ConstituencyName;

    private EditText etConstituency;


    public ConstituencyData() {
    }

    public ConstituencyData(String ConstituencyName , EditText etConstituency) {
        this.ConstituencyName = ConstituencyName;

        this.etConstituency=etConstituency;

    }

    public String getConstituency() {
        return ConstituencyName;
    }

    public void setConstituency(String constituency) {
        this.ConstituencyName = constituency;
    }

    public EditText getEtConstituency() {
        return etConstituency;
    }

    public void setEtConstituency(EditText etconfconst) {
        this.etConstituency = etconfconst;
    }

    public EditText getEtConfirmConstituency() {
        return etConstituency;
    }

    public void setEtConfirmConstituency(EditText etconfconst) {
        this.etConstituency = etconfconst;
    }


}