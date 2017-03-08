package com.example.in.kielection;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.in.kielection.Common.DB;

import java.util.ArrayList;

public class FinalResult extends Activity {

    Spinner spPartys;
    String party="";
    DB db;
    ArrayList<String> PartyList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_result);
        db=new DB(this);
        spPartys = (Spinner) findViewById(R.id.spinner);
        getPartyName();
    }

    public void submitFinalResult(View view) {

    }
    public void getPartyName() {

        //db.truncate(DB.Table.Name.Answere);
        PartyList.clear();
        PartyList.add("Select Party");
        Cursor cur=null;
        String item="Select * from PartyDetail";
        cur=db.findCursor(item,null);
        if(cur!=null && cur.moveToNext())
        {
            for(int i=0;i<cur.getCount();i++)
            {

                PartyList.add(cur.getString(cur.getColumnIndex(DB.Table.PartyDetail.PartyName.toString())));

                cur.moveToNext();
            }

        }
        Log.v("Resultrequest", PartyList.toString());
        initializeSpinner();
    }
    public void initializeSpinner() {
        spPartys.setAdapter(new Location_adapter_spinner(FinalResult.this, R.layout.spinner_rows, PartyList));

        spPartys.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                String item = PartyList.get(spPartys.getSelectedItemPosition());

                if(item.equals("Select Party"))
                {

                }
                else
                {
                    party =item;
                    Log.e("Sourceasd",item);
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public class Location_adapter_spinner extends ArrayAdapter<String> {

        ArrayList<String> data;

        public Location_adapter_spinner(Context context, int textViewResourceId, ArrayList<String> arraySpinner_time) {

            super(context, textViewResourceId, arraySpinner_time);

            this.data = arraySpinner_time;

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row=inflater.inflate(R.layout.spinner_adapter, parent, false);
            TextView label=(TextView)row.findViewById(R.id.category_name);

            label.setText(data.get(position).toString());

            return row;
        }
    }

    public void onBackPressed()
    {
        finish();
    }
}
