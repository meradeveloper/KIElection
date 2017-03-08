package com.example.in.kielection;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.in.kielection.Common.Constants;
import com.example.in.kielection.Common.DB;
import com.example.in.kielection.Common.Preferences;
import com.example.in.kielection.Common.Utils;
import com.vansuita.pickimage.Util;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.ArrayList;

public class DataCorrection extends AppCompatActivity implements SpinnerCallback , View.OnClickListener {

    private Spinner spinCandidate,spinRound;

    private Preferences pref;
    private Spinners spinners;
    private DB db;
    private LinearLayout llVotes;
    private EditText etVotes;
    private RadioButton rbLead,rbVote;

    ArrayList<String> CandidateList=new ArrayList<>();
    ArrayList<String> RoundList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_correction);

        pref= new Preferences(this);

        spinners= new Spinners(this,this);

        db= new DB(this);

        etVotes=(EditText)findViewById(R.id.etvotes);
        llVotes=(LinearLayout) findViewById(R.id.llvotes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Data Correction");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setRadiobutton();
        setSpinner();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setRadiobutton()
    {
        rbLead=(RadioButton)findViewById(R.id.rb_lead);
        rbLead.setOnClickListener(this);

        rbVote=(RadioButton)findViewById(R.id.rb_vote);
        rbVote.setOnClickListener(this);
    }

    void setSpinner()
    {
        spinCandidate=(Spinner)findViewById(R.id.spin_candidate);
        CandidateList.clear();
        CandidateList.add("Select Candidate");

        getSpinnerData(DB.Table.Name.PartyDetail,CandidateList,DB.Table.PartyDetail.ConstituencyID.toString()+" = "+pref.get(Constants.ConstituencyID));
        Log.v("MYConstituencyList","ConstituencyList-"+CandidateList.toString());
        spinners.getSpinner(spinCandidate,CandidateList,R.layout.spinner_adapter_white_bg);

        spinRound=(Spinner)findViewById(R.id.spin_round);
        RoundList.clear();
        RoundList.add("Select Counting Round");
        getSpinnerData(DB.Table.Name.CountingRoundList,RoundList,DB.Table.CountingRoundList.UserID.toString()+" = "+pref.get(Constants.userId));
        spinners.getSpinner(spinRound,RoundList,R.layout.spinner_adapter_white_bg);


    }

    public void getSpinnerData(String table,ArrayList<String> DataList,String Condition) {

        //DataList.clear();

        Cursor cur=null;
        String WhereCondition=" WHERE "+Condition;
        String item="SELECT * FROM "+table+WhereCondition;
        cur=db.findCursor(item,null);

        Log.v("StringQ",item);

        if(cur!=null && cur.moveToNext())
        {
            for(int i=0;i<cur.getCount();i++)
            {

                if(table.equals(DB.Table.Name.PartyDetail))
                {
                    Log.v("ConstituencyListCount","ConstituencyList:- "+String.valueOf(cur.getCount()));
                    //ConstituencyList.add(cur.getString(cur.getColumnIndex(DB.Table.ConstituencyList.ID.toString())));
                    //ConstituencyList.add(cur.getString(cur.getColumnIndex(DB.Table.ConstituencyList.ConstituencyCode.toString())));
                    CandidateList.add(cur.getString(cur.getColumnIndex(DB.Table.PartyDetail.CandidateName.toString())));
                    // ConstituencyList.add(cur.getString(cur.getColumnIndex(DB.Table.ConstituencyList.UserID.toString())));
                }
                else if(table.equals(DB.Table.Name.CountingRoundList))
                {
                    //CountingRoundList.add(cur.getString(cur.getColumnIndex(DB.Table.CountingRoundList.ID.toString())));
                    //CountingRoundList.add(cur.getString(cur.getColumnIndex(DB.Table.CountingRoundList.CountingRoundCode.toString())));
                    RoundList.add(cur.getString(cur.getColumnIndex(DB.Table.CountingRoundList.CountingRoundName.toString())));
                    //CountingRoundList.add(cur.getString(cur.getColumnIndex(DB.Table.CountingRoundList.UserID.toString())));
                }

                cur.moveToNext();
            }

        }

        Log.v("CandidateList",CandidateList.toString());
        Log.v("CountingRoundList",RoundList.toString());


    }

    @Override
    public void getSpinner(String spinner, String Value) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.rb_lead:getcheckedRadioButton(v,rbVote);break;
            case R.id.rb_vote:getcheckedRadioButton(v,rbLead);break;
        }
    }

    void getcheckedRadioButton(View v,RadioButton radioButton)
    {
        if( !((RadioButton)v).isChecked() )
        {
            ((RadioButton)v).setChecked(true);
            if( ((RadioButton)v)==rbVote)
                Utils.onAnimation(llVotes);
            else
                Utils.goneAnimation(llVotes);
        }
        else
        {
            radioButton.setChecked(false);
            if( ((RadioButton)v)==rbVote)
                Utils.onAnimation(llVotes);
            else
                Utils.goneAnimation(llVotes);
        }
    }


}
