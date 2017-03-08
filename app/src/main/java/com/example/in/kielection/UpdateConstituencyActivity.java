package com.example.in.kielection;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.in.kielection.Common.Constants;
import com.example.in.kielection.Common.DB;
import com.example.in.kielection.Common.Preferences;
import com.example.in.kielection.Common.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class UpdateConstituencyActivity extends AppCompatActivity implements LoginTimerCallback {

  RecyclerView recyclerView;
    UpdatetConstituencyRecycleAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    Preferences pref;
    DB db;
    RelativeLayout rlSubmit;
    LoginTimerCallback timerCallback;
    LoginTimer loginTimer;
    SpotsDialog p;
    ArrayList<HashMap<String,String>> list=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_constituency);

        pref= new Preferences(this);

        p=new SpotsDialog(this,R.style.Custom);

        db= new DB(this);

        loginTimer= new LoginTimer(this,this);

        recyclerView = (RecyclerView) findViewById(R.id.ll);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Confirm Constituency");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        constituencyRecycleAdapter();
        //setValues();
    }

    /*void setValues() {
        HashMap<String,String> map=new HashMap();
        map.put("value","BSP");
        map.put("isChecked","1");
        list.add(map);
        map=new HashMap();
        map.put("value","CON");
        map.put("isChecked","1");
        list.add(map);
        map=new HashMap();
        map.put("value","SP");
        map.put("isChecked","1");
        list.add(map);
        map=new HashMap();
        map.put("value","AAP");
        map.put("isChecked","1");
        list.add(map);
        map=new HashMap();
        map.put("value","BJP");
        map.put("isChecked","1");
        list.add(map);

        Log.v("List",list.toString());
        constituencyRecycleAdapter();
    }*/

    public void constituencyRecycleAdapter()
    {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // specify an adapter (see also next example)
        //Toast.makeText(this, String.valueOf(FormList.size())+" Result Found", Toast.LENGTH_SHORT).show();
        Log.v("Datalist",ConstituencyActivity.DataList.toString());
        mAdapter = new UpdatetConstituencyRecycleAdapter(this,ConstituencyActivity.DataList2);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    void getConstituencyData()
    {
        list.clear();
        for(int i=0;i<ConstituencyActivity.DataList.size();i++)
        {

            HashMap<String,String> dataMap= new HashMap<>();

            String value=((EditText)UpdatetConstituencyRecycleAdapter.mObjectset.get(i).get("edittext")).getText().toString();

            if(value!=null)
                if(!value.isEmpty())
                {
                    Log.v("DataList","DataList"+value);
                    dataMap.put("'ConstituencyID'","'"+value+"'");
                    list.add(dataMap);
                }

        }

        Log.v("DataList","DataList"+list.toString());
    }

    public void submitConstituencyData(View v)
    {
        // show click effect on button pressed
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
        v.startAnimation(buttonClick);
        getConstituencyData();

        if(ConstituencyActivity.DataList.size()>0){
            submitConstituencyApi();
        }

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

    /*@Override
    public void onBackPressed() {

        Utils.goAnotherActivity(UpdateConstituencyActivity.this,ConstituencyActivity.class);
        super.onBackPressed();
    }*/

    public void submitConstituencyApi()
    {

        p.show();

        JSONObject json= new JSONObject();

        try {


            json.put("strUserID",pref.get(Constants.userId));
            json.put("strCStation",pref.get(Constants.cStationId));
            json.put("strStateID",pref.get(Constants.stateId));
            json.put("strResponse",list.toString().replace("=",":"));




            Log.v("strrequest",json.toString());
            //Log.v("imagejson",base641);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        String url="http://projects.karvyinsights.com/ElectionService/Election/InsertConstituency";
        Log.v("url",url);
        AndroidNetworking.post(url)
                .addJSONObjectBody(json)
                .setContentType("application/json; charset=utf-8") // custom ContentType
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("response",response);

                        if(response.equalsIgnoreCase("\"true\""))
                        {
                            // pref.set(Constants.Constituency_status,"1");
                            //pref.commit();
                            HashMap<String,String> usermap =  new HashMap<String, String>();
                            usermap.put(DB.Table.user_detail.constituency_status.toString(),"1");
                            db.update(DB.Table.Name.user_detail,usermap,DB.Table.user_detail.id.toString()+"="+pref.get(Constants.userId),null);

                            String status=Utils.getSpinnerItemId(DB.Table.Name.user_detail,DB.Table.user_detail.constituency_status.toString(),DB.Table.user_detail.id.toString(),pref.get(Constants.userId),UpdateConstituencyActivity.this);
                            Log.v("constituencystatus","constituencystatus1"+status+" user_id"+pref.get(Constants.userId));
                            loginTimer.setTimer("2");

                        }
                        else if(response.equals("\"false\""))
                            Toast.makeText(UpdateConstituencyActivity.this,  "You have entered wrong constituency code!", Toast.LENGTH_SHORT).show();
                        p.cancel();

                    }

                    @Override
                    public void onError(ANError anError) {
                        p.cancel();
                        Log.v("ErrorInApi",anError.getMessage());
                        Toast.makeText(UpdateConstituencyActivity.this,  anError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void getTimer(String timer) {

        Log.v("getTimer","getTimer");
        timerPopup();

        ToolTipWindow toolTipWindow= new ToolTipWindow(this,3000);
        //toolTipWindow.showAtLocation(toolTipWindow.getContentView(),0,0,0);

    }

    public void timerPopup()
    {
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(UpdateConstituencyActivity.this);


        // Setting Dialog Title
        alertDialog.setTitle("Alert!");

         /*final int interval = 1000; // 1 Second
         Handler handler = new Handler();
         Runnable runnable = new Runnable(){
            public void run() {

                // Setting Dialog Message
                alertDialog.setMessage("Please Wait "+String.valueOf(System.currentTimeMillis()+interval)+"Seconds");
            }
        };
        handler.postAtTime(runnable, System.currentTimeMillis()+interval);
        handler.postDelayed(runnable, interval);*/

        final AlertDialog alert = new AlertDialog.Builder(this).create();
        //alert.setTitle("Alert");
        // Get the layout inflater
        LayoutInflater inflater = (this).getLayoutInflater();
        View popupview=inflater.inflate(R.layout.timer_popup, null);
        alert.setView(popupview);
        //alert.setMessage("00:10");


        final TextView tvTimer= (TextView)popupview.findViewById(R.id.tvtimer);
        tvTimer.setText("00:10");
        alert.show();

        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //alert.setMessage("Please Wait 00:"+ (millisUntilFinished/1000));
                tvTimer.setText("Please Wait 00:"+ (millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                //info.setVisibility(View.GONE);
                alert.dismiss();

                if(pref.get(Constants.Constituency_status).equals("") || pref.get(Constants.Constituency_status).equals("0"))
                    Utils.goAnotherActivity(UpdateConstituencyActivity.this,LoginActivity.class);
                else if(pref.get(Constants.Constituency_status).equals("1"))
                    Utils.goAnotherActivity(UpdateConstituencyActivity.this,Container.class);


            }
        }.start();



        alertDialog.setCancelable(false);

        // On pressing Settings button
        /*alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                logout(view);
            }
        });*/

        // Showing Alert Message
        //alertDialog.show();
    }
}
