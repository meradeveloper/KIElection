package com.example.in.kielection;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.in.kielection.Common.DB;
import com.example.in.kielection.Common.Utils;

import java.util.ArrayList;

/**
 * Created by Localadmin on 2/15/2017.
 */

public class Spinners {

    Context context;
    SpinnerCallback spinnerCallback;

    public Spinners(SpinnerCallback spinnerCallback,Context context)
    {
        this.context=context;
        this.spinnerCallback=spinnerCallback;
    }

    public void getSpinner(final Spinner mySpinner, final ArrayList<String> myList, int layoutID)
    {

        mySpinner.setAdapter(new SpinnerAdapter(context, R.layout.spinner_rows, myList,layoutID));

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                String item = myList.get(mySpinner.getSelectedItemPosition());

                /*Select Constituency,Select Counting Round,Select Regular Notification*/
                switch (mySpinner.getId())
                {
                    case R.id.spin_const:
                        Log.v("spin_const",item);
                        if(!myList.get(position).toString().equalsIgnoreCase("Select Constituency"))
                        {
                            String Constituency="";
                            Constituency= Utils.getSpinnerItemId(DB.Table.Name.ConstituencyList,DB.Table.ConstituencyList.ID.toString(),DB.Table.ConstituencyList.ConstituencyName.toString(),item,context);
                            if(spinnerCallback != null)
                                spinnerCallback.getSpinner("Constituency",Constituency);

                            Log.v("Constituency1",Constituency);

                        }
                        else
                        {
                            if(spinnerCallback != null)
                                spinnerCallback.getSpinner("Constituency","");
                        }

                        break;
                    case R.id.spin_lead:Log.v("spin_lead",item);
                        if(!myList.get(position).toString().equalsIgnoreCase("Select Leading Candidate"))
                        {
                            String CandidateID="";
                            String s[]=item.split("-");
                            Log.v("s_item",item);
                            Log.v("s_item","s[0]"+s[0]);

                            CandidateID = Utils.getSpinnerItemId(DB.Table.Name.PartyDetail,DB.Table.PartyDetail.CandidateID.toString(),DB.Table.PartyDetail.CandidateName.toString(),s[0].trim(),context);
                            if(spinnerCallback != null)
                                spinnerCallback.getSpinner("LeadCandidate",CandidateID);
                            Container.spinStatus.setEnabled(false);
                        }
                        else
                        {
                            if(spinnerCallback != null)
                                spinnerCallback.getSpinner("LeadCandidate","");

                            Container.spinStatus.setEnabled(true);
                        }

                        break;
                    case R.id.spin_round1:Log.v("spin_round1",item);
                        if(!myList.get(position).toString().equalsIgnoreCase("Select Counting Round"))
                        {
                            String CountingRoundID="";
                            CountingRoundID = Utils.getSpinnerItemId(DB.Table.Name.CountingRoundList,DB.Table.CountingRoundList.ID.toString(),DB.Table.CountingRoundList.CountingRoundName.toString(),item,context);

                            if(spinnerCallback != null)
                                spinnerCallback.getSpinner("Round1",item);
                        }
                        else
                        {
                            if(spinnerCallback != null)
                                spinnerCallback.getSpinner("Round1","");
                        }

                        break;

                    case R.id.spin_round2:Log.v("spin_round2",item);
                        if(!myList.get(position).toString().equalsIgnoreCase("Select Counting Round"))
                        {
                            String CountingRoundID="";
                            CountingRoundID = Utils.getSpinnerItemId(DB.Table.Name.CountingRoundList,DB.Table.CountingRoundList.ID.toString(),DB.Table.CountingRoundList.CountingRoundName.toString(),item,context);

                            if(spinnerCallback != null)
                                spinnerCallback.getSpinner("Round2",item);
                        }
                        else
                        {
                            if(spinnerCallback != null)
                                spinnerCallback.getSpinner("Round2","");
                        }

                        break;

                    case R.id.spin_status:Log.v("spin_status",item);

                        if(!myList.get(position).toString().equalsIgnoreCase("Select"))
                        {
                            if(spinnerCallback != null)
                                spinnerCallback.getSpinner("RegularNotification",item);

                            Container.spinLead.setEnabled(false);
                        }
                        else
                        {
                            if(spinnerCallback != null)
                                spinnerCallback.getSpinner("RegularNotification","");

                            Container.spinLead.setEnabled(true);
                        }

                        break;
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }
}
