package com.example.in.kielection;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.PopupWindow;
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
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ConstituencyActivity extends Activity /*implements LoginTimerCallback*/ {

    Handler handler;
    Preferences pref;
    RecyclerView RvList;
    LinearLayoutManager mLayoutManager;
    ConstituencyRecycleAdapter mAdapter;

    RelativeLayout rlSubmit;

    SpotsDialog p;
    LoginTimerCallback timerCallback;
    LoginTimer loginTimer;
    DB db;

    private List<ConstituencyData> ConstituencyList = new ArrayList<>();
    public static ArrayList<HashMap<String,String>> DataList = new ArrayList<>();
    public static ArrayList<HashMap<String,String>> DataList2 = new ArrayList<>();

    /*public ConstituencyActivity(LoginTimerCallback timer)
    {
        this.timerCallback=timer;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.constituency_activity);

        p=new SpotsDialog(this,R.style.Custom);

        pref= new Preferences(this);

        db= new DB(this);

        //loginTimer= new LoginTimer(this,this);

        RvList= (RecyclerView)findViewById(R.id.rec_constituency);

        //rlSubmit=(RelativeLayout)findViewById(R.id.rl_submit);

        MyList();
        constituencyRecycleAdapter();

        /*rlSubmit.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //Button Pressed
                    Log.v("click","Pressed");
                   // rlSubmit.setBackgroundColor(getResources().getColor(R.color.black));

                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    Log.v("click","PressedUp");
                    //rlSubmit.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
                return false;
            }
        });*/




    }

    public void constituencyRecycleAdapter()
    {

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        RvList.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        RvList.setLayoutManager(mLayoutManager);
        RvList.setItemAnimator(new DefaultItemAnimator());

        // specify an adapter (see also next example)
        //Toast.makeText(this, String.valueOf(FormList.size())+" Result Found", Toast.LENGTH_SHORT).show();
        mAdapter = new ConstituencyRecycleAdapter(this,ConstituencyList);

        RvList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    void MyList()
    {
        for(int i=0;i<30;i++)
        {
            ConstituencyList.add(new ConstituencyData("Constituency "+String.valueOf(i+1),null));
        }

    }

    void getConstituencyData()
    {
        DataList.clear();
        DataList2.clear();
        for(int i=0;i<ConstituencyList.size();i++)
        {
            ConstituencyData data= ConstituencyList.get(i);
            Log.v("Data","data"+data.toString());

            HashMap<String,String> dataMap= new HashMap<>();
            HashMap<String,String> dataMap2= new HashMap<>();

            if(data.getEtConfirmConstituency()!=null && data.getEtConstituency()!=null)
            if(!data.getEtConfirmConstituency().getText().toString().isEmpty())
            {
                dataMap.put("'ConstituencyID'","'"+data.getEtConfirmConstituency().getText().toString()+"'");
                dataMap2.put("ConstituencyID",data.getEtConfirmConstituency().getText().toString());
                DataList.add(dataMap);
                DataList2.add(dataMap2);
            }

        }

        Log.v("DataList","DataList"+DataList.toString());
    }



    public void submitConstituency(View v)
    {
        // show click effect on button pressed
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
        v.startAnimation(buttonClick);
        getConstituencyData();

        if(DataList.size()>0){
            //submitConstituencyApi();
            Utils.goAnotherActivity(ConstituencyActivity.this,UpdateConstituencyActivity.class);
        }
        else
            Toast.makeText(this, "Please enter Constituency Code First !", Toast.LENGTH_SHORT).show();
    }

    public void skipConstituency(View v)
    {
        // show click effect on button pressed
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
        v.startAnimation(buttonClick);

        Utils.goAnotherActivity(ConstituencyActivity.this,Container.class);
    }

    /*public void submitConstituencyApi()
    {

        p.show();

        JSONObject json= new JSONObject();

        try {


            json.put("strUserID",pref.get(Constants.userId));
            json.put("strCStation",pref.get(Constants.cStationId));
            json.put("strStateID",pref.get(Constants.stateId));
            json.put("strResponse",DataList.toString().replace("=",":"));




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

                            String status=Utils.getSpinnerItemId(DB.Table.Name.user_detail,DB.Table.user_detail.constituency_status.toString(),DB.Table.user_detail.id.toString(),pref.get(Constants.userId),ConstituencyActivity.this);
                            Log.v("constituencystatus","constituencystatus1"+status+" user_id"+pref.get(Constants.userId));
                            loginTimer.setTimer("2");

                        }
                        else if(response.equals("\"false\""))
                            Toast.makeText(ConstituencyActivity.this,  "You have entered wrong constituency code!", Toast.LENGTH_SHORT).show();
                        p.cancel();

                    }

                    @Override
                    public void onError(ANError anError) {
                        p.cancel();
                        Log.v("ErrorInApi",anError.getMessage());
                        Toast.makeText(ConstituencyActivity.this,  anError.toString(), Toast.LENGTH_SHORT).show();
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
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(ConstituencyActivity.this);


        // Setting Dialog Title
        alertDialog.setTitle("Alert!");

         *//*final int interval = 1000; // 1 Second
         Handler handler = new Handler();
         Runnable runnable = new Runnable(){
            public void run() {

                // Setting Dialog Message
                alertDialog.setMessage("Please Wait "+String.valueOf(System.currentTimeMillis()+interval)+"Seconds");
            }
        };
        handler.postAtTime(runnable, System.currentTimeMillis()+interval);
        handler.postDelayed(runnable, interval);*//*

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
                Utils.goAnotherActivity(ConstituencyActivity.this,LoginActivity.class);
            }
        }.start();



        alertDialog.setCancelable(false);

        // On pressing Settings button
        *//*alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                logout(view);
            }
        });*//*

        // Showing Alert Message
        //alertDialog.show();
    }*/
}
