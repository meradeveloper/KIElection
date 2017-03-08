package com.example.in.kielection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.in.kielection.Common.Constants;

import com.example.in.kielection.Common.DB;
import com.example.in.kielection.Common.Preferences;
import com.example.in.kielection.Common.TopAlignedImageView;
import com.example.in.kielection.Common.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class Container extends AppCompatActivity implements IPickResult,SpinnerCallback {
    CheckBox chCountiong;
    LinearLayoutManager mLayoutManager;
    CoordinatorLayout cordinator;
    FormRecycleAdapter mAdapter;
    RecyclerView RvList;
    LinearLayout llRecycle,llViews;
    RelativeLayout rlNoResult;
    ScrollView scrollView;
    TextView tvCancel;
    String RoundType="";
    Handler handler;

    DB db;
    Validation validation;
    Snackbar snackbar;
    Spinner spinConst;
    public static Spinner spinStatus,spinLead,spinRound1,spinRound2;
    Preferences pref;
    AlertDialog p;
    Spinners spinners;

    String PhotoID="";

    public static EditText etTotalVotes;

    Button btnSubmitLead;

    String base641="",base642="",base643="",picPath1="",picName2="",picName3="";

    private String selectedImagePath , LeadCandidateID="",RegularNotification="",Round="",Round1="",Round2="",RoundID="",RoundID1="",CountingStationID="",StateID="",UserID="";

    public static String ConstituencyID="",RoundID2="";

    Bitmap bitmap=null;

    private ImageView iv1,iv2,iv3;

    private List<FormData> FormList = new ArrayList<>();
    private ArrayList<HashMap<String,String>> Answere_List = new ArrayList<>();
    private ArrayList<HashMap<String,String>> Lead_Answere_List = new ArrayList<>();
    private ArrayList<String> Constituency_List= new ArrayList<>();
    private ArrayList<String> Round_List= new ArrayList<>();
    private ArrayList<String> Status_List= new ArrayList<>();
    private ArrayList<String> Lead_List= new ArrayList<>();

    ArrayList<String> ConstituencyList=new ArrayList<>();
    ArrayList<String> CountingRoundList=new ArrayList<>();
    ArrayList<String> RegularNotificationList=new ArrayList<>();
    ArrayList<HashMap<String, Object>> Objects=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        if (isTablet(this))
        {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        getId();

    }


   public void LogoutAlert(final View view)
    {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(Container.this);

        // Setting Dialog Title
        alertDialog.setTitle("Alert!");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to logout?");

        alertDialog.setCancelable(false);

        // On pressing Settings button
        alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

               logout(view);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    void getId() {

        handler =new Handler();
        db=new DB(this);
        validation= new Validation(this);
        pref=new Preferences(this);
        spinners= new Spinners(this,this);
        p=new SpotsDialog(this,R.style.Custom);
        tvCancel=(TextView) findViewById(R.id.tv1);
        llRecycle=(LinearLayout)findViewById(R.id.ll_rec);
        RvList  = (RecyclerView)findViewById(R.id.RvList);

        etTotalVotes=(EditText)findViewById(R.id.et_to_votes);



        //getData();

        cordinator  = (CoordinatorLayout)findViewById(R.id.cordinator);
        chCountiong  = (CheckBox)findViewById(R.id.chCount);

        setSpinner();


        //llViews=(LinearLayout)findViewById(R.id.llViews);

        scrollView=(ScrollView)findViewById(R.id.scrollv);

        rlNoResult=(RelativeLayout)findViewById(R.id.rl_no_result);

        btnSubmitLead=(Button)findViewById(R.id.submit_lead);
        btnSubmitLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RoundType="lead";
                if(validation.getStatus(RoundID1,ConstituencyID,RoundType) && !Round1.isEmpty() && !LeadCandidateID.isEmpty())
                {
                    Log.v("leadUserID","an+"+pref.get(Constants.userId));
                    Lead_Answere_List.clear();
                    HashMap<String,String> map=new HashMap();
                    map.put("'UserID'","'"+pref.get(Constants.userId)+"'");
                    map.put("'CandidateID'","'"+LeadCandidateID+"'");
                    map.put("'CategoryID'","'"+Utils.getSpinnerItemId(DB.Table.Name.PartyDetail,DB.Table.PartyDetail.CategoryID.toString(),DB.Table.PartyDetail.CandidateID.toString(),LeadCandidateID,Container.this)+"'");
                    map.put("'PartyID'","'"+Utils.getSpinnerItemId(DB.Table.Name.PartyDetail,DB.Table.PartyDetail.PartyID.toString(),DB.Table.PartyDetail.CandidateID.toString(),LeadCandidateID,Container.this)+"'");
                    map.put("'Votes'","'0'");

                    Lead_Answere_List.add(map);
                    Log.v("leadanswerlist","an+"+Lead_Answere_List.toString());
                    db.truncate(DB.Table.Name.Answere);
                    Round=Round1;
                    getAns();
                }


                else if(!validation.getStatus(RoundID1,ConstituencyID,RoundType))
                {
                    spinLead.setSelection(0);
                    Toast.makeText(Container.this, Round1+" is already submitted.", Toast.LENGTH_SHORT).show();
                }
                else if(Round1.isEmpty())
                    Toast.makeText(Container.this, "Select Counting Round First.", Toast.LENGTH_SHORT).show();
                else if(LeadCandidateID.isEmpty())
                    Toast.makeText(Container.this, "Select Leading Candidate First.", Toast.LENGTH_SHORT).show();
            }
        });




        iv1=(ImageView)findViewById(R.id.pic1);
        iv2=(ImageView)findViewById(R.id.pic2);
        iv3=(ImageView)findViewById(R.id.pic3);


        recycleAdapter();

    }



    void setSpinner()
    {
        spinConst=(Spinner)findViewById(R.id.spin_const);
        ConstituencyList.clear();
        //ConstituencyList.add("Select Constituency");

        getSpinnerData(DB.Table.Name.ConstituencyList,ConstituencyList,DB.Table.ConstituencyList.UserID.toString()+" = "+pref.get(Constants.userId));
        Log.v("MYConstituencyList","ConstituencyList-"+ConstituencyList.toString());
        spinners.getSpinner(spinConst,ConstituencyList,R.layout.spinner_adapter);

        spinRound1=(Spinner)findViewById(R.id.spin_round1);
        CountingRoundList.clear();
        CountingRoundList.add("Select Counting Round");
        getSpinnerData(DB.Table.Name.CountingRoundList,CountingRoundList,DB.Table.CountingRoundList.UserID.toString()+" = "+pref.get(Constants.userId));
        spinners.getSpinner(spinRound1,CountingRoundList,R.layout.spinner_adapter);

        spinRound2=(Spinner)findViewById(R.id.spin_round2);
        CountingRoundList.clear();
        CountingRoundList.add("Select Counting Round");
        getSpinnerData(DB.Table.Name.CountingRoundList,CountingRoundList,DB.Table.CountingRoundList.UserID.toString()+" = "+pref.get(Constants.userId));
        spinners.getSpinner(spinRound2,CountingRoundList,R.layout.spinner_adapter);

        spinStatus=(Spinner)findViewById(R.id.spin_status);
        RegularNotificationList.clear();
        RegularNotificationList.add("Select");
        getSpinnerData(DB.Table.Name.RegularNotificationList,RegularNotificationList,DB.Table.RegularNotificationList.UserID.toString()+" = "+pref.get(Constants.userId));
        spinners.getSpinner(spinStatus,RegularNotificationList,R.layout.spinner_adapter_white_bg);

        spinLead=(Spinner)findViewById(R.id.spin_lead);
    }


    public void recycleAdapter()
    {

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        RvList.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        RvList.setLayoutManager(mLayoutManager);
        RvList.setItemAnimator(new DefaultItemAnimator());

        // specify an adapter (see also next example)
        Toast.makeText(this, String.valueOf(FormList.size())+" Result Found", Toast.LENGTH_SHORT).show();
        mAdapter = new FormRecycleAdapter(Container.this,FormList);

        RvList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void clear() {
        int size = this.FormList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.FormList.remove(0);
            }

            mAdapter.notifyItemRangeRemoved(0, size);
        }
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

                if(table.equals(DB.Table.Name.ConstituencyList))
                {
                    Log.v("ConstituencyListCount","ConstituencyList:- "+String.valueOf(cur.getCount()));
                    //ConstituencyList.add(cur.getString(cur.getColumnIndex(DB.Table.ConstituencyList.ID.toString())));
                    //ConstituencyList.add(cur.getString(cur.getColumnIndex(DB.Table.ConstituencyList.ConstituencyCode.toString())));
                    ConstituencyList.add(cur.getString(cur.getColumnIndex(DB.Table.ConstituencyList.ConstituencyName.toString())));
                   // ConstituencyList.add(cur.getString(cur.getColumnIndex(DB.Table.ConstituencyList.UserID.toString())));
                }
                else if(table.equals(DB.Table.Name.CountingRoundList))
                {
                    //CountingRoundList.add(cur.getString(cur.getColumnIndex(DB.Table.CountingRoundList.ID.toString())));
                    //CountingRoundList.add(cur.getString(cur.getColumnIndex(DB.Table.CountingRoundList.CountingRoundCode.toString())));
                    CountingRoundList.add(cur.getString(cur.getColumnIndex(DB.Table.CountingRoundList.CountingRoundName.toString())));
                    //CountingRoundList.add(cur.getString(cur.getColumnIndex(DB.Table.CountingRoundList.UserID.toString())));
                }
                else if(table.equals(DB.Table.Name.RegularNotificationList))
                {
                    //RegularNotificationList.add(cur.getString(cur.getColumnIndex(DB.Table.RegularNotificationList.ID.toString())));
                   // RegularNotificationList.add(cur.getString(cur.getColumnIndex(DB.Table.RegularNotificationList.RegularNotificationCode.toString())));
                    RegularNotificationList.add(cur.getString(cur.getColumnIndex(DB.Table.RegularNotificationList.RegularNotificationName.toString())));
                   // RegularNotificationList.add(cur.getString(cur.getColumnIndex(DB.Table.RegularNotificationList.UserID.toString())));
                }


                cur.moveToNext();
            }

        }

        Log.v("ConstituencyList",ConstituencyList.toString());
        Log.v("CountingRoundList",CountingRoundList.toString());
        Log.v("RegularNotificationList",RegularNotificationList.toString());

    }


    public void getData() {
        db.truncate(DB.Table.Name.Answere);
        Lead_List.clear();
        Lead_List.add("Select Leading Candidate");
        FormList.clear();
        Cursor cur=null;
        String item="Select * from PartyDetail where "+DB.Table.PartyDetail.ConstituencyID+" = "+ConstituencyID;
        cur=db.findCursor(item,null);
        //RvList.setVisibility(View.VISIBLE);
        //llRecycle.setVisibility(View.VISIBLE);
        Log.v("FormList","FormList:- "+FormList.toString());
        if(cur.getCount()==0)
        {

            scrollView.setVisibility(View.GONE);
            rlNoResult.setVisibility(View.VISIBLE);
            Log.v("FormList","FormList:- "+FormList.toString());

        }
        else
        {
            scrollView.setVisibility(View.VISIBLE);
            rlNoResult.setVisibility(View.GONE);
        }


        if(cur!=null && cur.moveToNext())
        {
            for(int i=0;i<cur.getCount();i++)
            {
                String Cname=cur.getString(cur.getColumnIndex(DB.Table.PartyDetail.CandidateName.toString()));
                String Pname=cur.getString(cur.getColumnIndex(DB.Table.PartyDetail.PartyName.toString()));
                String Cid=cur.getString(cur.getColumnIndex(DB.Table.PartyDetail.CandidateID.toString()));
                String Pid=cur.getString(cur.getColumnIndex(DB.Table.PartyDetail.PartyID.toString()));
                String Pcode=cur.getString(cur.getColumnIndex(DB.Table.PartyDetail.PartyCode.toString()));
                String CatId=cur.getString(cur.getColumnIndex(DB.Table.PartyDetail.CategoryID.toString()));
                FormData formData = new FormData(Cname, "", Pname,Cid,Pid,CatId,null,null);
                FormList.add(formData);
                Lead_List.add(Cname+" - "+Pcode);
                cur.moveToNext();
            }

            /*FormData formData = new FormData("NOTA", "", "NOTA","","","");
            FormList.add(formData);*/

          //  mAdapter.notifyDataSetChanged();
        }

        Log.v("FormList","FormList:- "+FormList.toString());
        getJson(FormList);

    }

    void getFormData()
    {
        ArrayList<HashMap<String,String>> AnsList= new ArrayList<>();

        for (int i=0;i<FormList.size();i++)
        {
            FormData fd= FormList.get(i);

            HashMap<String ,String> map = new HashMap<>();
            map.put("vote",fd.getVote().getText().toString());
            map.put("lead",String.valueOf(fd.getLead().isChecked()));

            AnsList.add(map);


        }

        Log.v("ANSLIST",AnsList.toString());
    }

    public  void go(View view)
    {
        startActivity(new Intent(Container.this,Dashboard.class));
    }
    public  void dataCorrection(View view)
    {
        pref.set(Constants.ConstituencyID,ConstituencyID);
        pref.commit();
        startActivity(new Intent(Container.this,DataCorrection.class));
    }
    public void getAns() {
        Answere_List.clear();
        Cursor cur=null;
        String item="Select * from Answere";
        cur=db.findCursor(item,null);
        if(cur!=null && cur.moveToNext())
        {
            for(int i=0;i<cur.getCount();i++)
            {
              HashMap<String,String> map=new HashMap();
                map.put("'UserID'","'"+cur.getString(cur.getColumnIndex(DB.Table.Answere.userID.toString()))+"'");
                UserID=cur.getString(cur.getColumnIndex(DB.Table.Answere.userID.toString()));
                map.put("'CandidateID'","'"+cur.getString(cur.getColumnIndex(DB.Table.Answere.candidateId.toString()))+"'");
                map.put("'CategoryID'","'"+cur.getString(cur.getColumnIndex(DB.Table.Answere.categoryid.toString()))+"'");
                map.put("'PartyID'","'"+cur.getString(cur.getColumnIndex(DB.Table.Answere.partyId.toString()))+"'");
                //map.put("'StateID'","'"+cur.getString(cur.getColumnIndex(DB.Table.Answere.stateId.toString()))+"'");
                //map.put("'CountingStationID'","'"+cur.getString(cur.getColumnIndex(DB.Table.Answere.CstationId.toString()))+"'");
                CountingStationID=cur.getString(cur.getColumnIndex(DB.Table.Answere.CstationId.toString()));
                StateID=cur.getString(cur.getColumnIndex(DB.Table.Answere.stateId.toString()));
                //map.put("'ConstituencyID'","'"+cur.getString(cur.getColumnIndex(DB.Table.Answere.ConsId.toString()))+"'");
                map.put("'Votes'","'"+cur.getString(cur.getColumnIndex(DB.Table.Answere.votes.toString()))+"'");



                /*if(chCountiong.isChecked()==true)
                {
                    map.put("'VotingOver'","'"+"1"+"'");
                }
                else {
                    map.put("'VotingOver'","'"+"0"+"'");
                }*/

                if(!cur.getString(cur.getColumnIndex(DB.Table.Answere.partyId.toString())).equals("999"))
                {
                    Answere_List.add(map);
                }


                cur.moveToNext();
            }

        }
        //p.show();
        //imageUploadApi();
        Log.v("Resultrequest", Answere_List.toString());
        if(Answere_List.size()!=0)
            makeJsonObjReq(Answere_List.toString());
        else
            makeJsonObjReq("");

    }

    public void getJson(List<FormData> a) {
        JSONArray Result_array = new JSONArray();

        for (int ir = 0; ir < a.size(); ir++) {
            try {
                Result_array.put(new JSONObject(String.valueOf(a.get(ir))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v(" plan updated list", a.toString());


        }

        String s = Result_array.toString();

        Log.v("Resultrequest", s.toString());
    }

    public void logout(View view) {
        p.show();
        Thread background = new Thread() {
            public void run() {

                try {

                    sleep(1*10000);

                }
                catch (Exception e) {

                }

                finally{
                    pref.set(Constants.Password,      "");
                    pref.set(Constants.userId ,       "");
                    pref.set(Constants.username ,     "");
                    pref.set(Constants.email,         "");
                    pref.set(Constants.name,          "");
                    pref.set(Constants.Mobile,        "");
                    pref.set(Constants.stateId,       "");
                    pref.set(Constants.cStationId,     "");
                    pref.set(Constants.Phone,         "");
                    pref.set(Constants.ConstituencyID,"");
                    pref.set(Constants.Constituency_status,"0");
                    pref.commit();
                    Intent intentComing = new Intent(Container.this, LoginActivity.class);
                    startActivity(intentComing);
                    finish();



                }
            }
        };

        // start thread
        background.start();
    }

    public void clear(View view) {

        getData();
        RvList.setAdapter(mAdapter);


    }

    public void Submit(View view) {

        //getFormData();
        RoundType="vote";

        if(Round2.isEmpty())
            Toast.makeText(Container.this, "Select Counting Round First.", Toast.LENGTH_SHORT).show();

        else if(!validation.getStatus(RoundID2,ConstituencyID,RoundType))
            Toast.makeText(Container.this, Round2+" is already submitted.", Toast.LENGTH_SHORT).show();
        else
        {
            p.show();
            Lead_Answere_List.clear();
            Round=Round2;
            getAns();
        }
        //getDynamicLYViews();


    }

    public void submitStatus(View view) {

        //getFormData();

        if(RegularNotification.isEmpty())
            Toast.makeText(Container.this, "Select Status First.", Toast.LENGTH_SHORT).show();

        else
        {
            p.show();
            Lead_Answere_List.clear();

            getAns();
        }
        //getDynamicLYViews();


    }

    public void popUp(View view) {
        startActivity(new Intent(Container.this,FinalResult.class));
        overridePendingTransition(R.anim.activity_up, R.anim.no_change);

    }

    public int getMaxId()
    {
        int  max=0;
        Cursor cur=null;
        String itemQuery="Select * from Answere";
        cur=db.findCursor(itemQuery,null);
        if(cur!=null&& cur.moveToNext())
        {
            for(int i=0;i<cur.getCount();i++)
            {
              if(max<Integer.parseInt(cur.getString(cur.getColumnIndex(DB.Table.Answere.votes.toString()))))
                  max=Integer.parseInt(cur.getString(cur.getColumnIndex(DB.Table.Answere.votes.toString())));
                cur.moveToNext();
            }


        }
        return max;
    }

    public void makeJsonObjReq(String Result_array) {

        Log.v("Result_array","Result_array"+Result_array.toString());
        Result_array=Result_array.replace("=",":");
        JSONObject json = new JSONObject();
        JSONObject json_data = new JSONObject();

        try {

            p.dismiss();

            //json_data.put("strUID",            pref.get(Constants.username));
            //json_data.put("strPWD",           pref.get(Constants.Password));
            json_data.put("CountingStationID",           pref.get(Constants.cStationId));
            json_data.put("ConstituencyID",           ConstituencyID);
            json_data.put("StateID",           pref.get(Constants.stateId));
            json_data.put("TotalVotes",           etTotalVotes.getText().toString());


            /* Setting Photo */

            if(base641.isEmpty() && base642.isEmpty() && base643.isEmpty())
                json_data.put("PhotoYN", "0");
            else
            {


                Calendar calendar=Calendar.getInstance();

                PhotoID=StateID+CountingStationID+ConstituencyID+UserID+RoundID+String.valueOf(calendar.getTimeInMillis());
                json_data.put("PhotoYN", "1");
                if (!base641.isEmpty())
                    json_data.put("strPhotoID", PhotoID+"1");
                else
                    json_data.put("strPhotoID", "NA");

                if (!base642.isEmpty())
                    json_data.put("strPhotoID2", PhotoID+"2");
                else
                    json_data.put("strPhotoID2", "NA");

                if (!base643.isEmpty())
                    json_data.put("strPhotoID3", PhotoID+"2");
                else
                    json_data.put("strPhotoID3", "NA");

            }


            /* Setting Counting Round */

            String CountingRoundID="";
            CountingRoundID = Utils.getSpinnerItemId(DB.Table.Name.CountingRoundList,DB.Table.CountingRoundList.CountingRoundCode.toString(),DB.Table.CountingRoundList.CountingRoundName.toString(),Round,this);

            json_data.put("CountingRound",          CountingRoundID);

            /* Setting RegularNotification */

            if (RegularNotification.isEmpty())
            {
                json_data.put("StatusYN","0");
                json_data.put("StatusVal","");
            }

            else
            {
                json_data.put("StatusYN","1");
                json_data.put("StatusVal",RegularNotification);

            }

            /* Setting Leading Candidate ID */

            if (LeadCandidateID.isEmpty())
            {
                json_data.put("LeadYN","0");
                json_data.put("Lead","");
            }

            else
            {
                json_data.put("LeadYN","1");
                json_data.put("Lead",LeadCandidateID);
                json_data.put("strResponse",          Lead_Answere_List.toString().replace("=",":"));
            }

            /* Setting Voting */



            if (Result_array.isEmpty())
            {
                Log.v("leadUserID","an+"+pref.get(Constants.userId));
                HashMap<String,String> map=new HashMap();
                map.put("'UserID'","'"+pref.get(Constants.userId)+"'");
                map.put("'CandidateID'","'0'");
                map.put("'CategoryID'","'0'");
                map.put("'PartyID'","'0'");
                map.put("'Votes'","'0'");

                Lead_Answere_List.add(map);
                Log.v("leadanswerlist","an+"+Lead_Answere_List.toString());

                json_data.put("VoteYN","0");
                if (LeadCandidateID.isEmpty())
                json_data.put("strResponse",          Lead_Answere_List.toString().replace("=",":"));
            }

            else
            {
                json_data.put("VoteYN","1");
                json_data.put("strResponse",          Result_array.toString());
            }


            Log.v("requestDate"                     , json_data.toString());


        } catch (JSONException e) {

            e.printStackTrace();
                /*.addBodyParameter("strBase64",base64 )
                        .addBodyParameter("strFileName", orderNO+String.valueOf(status))*/
        }
        Toast.makeText(this, "Please Wait Data Uploading on Server", Toast.LENGTH_SHORT).show();
        String url="http://projects.karvyinsights.com/ElectionService/Home/SendMessage";
        AndroidNetworking.post(url)
                .addJSONObjectBody(json_data)
                .setContentType("application/json; charset=utf-8") // custom ContentType
                .setPriority(Priority.HIGH)
                .build()
               .getAsString(new StringRequestListener() {
                   @Override
                   public void onResponse(String response) {
                       Log.v("response",response);
                       parseUserJson(response);
                   }

                   @Override
                   public void onError(ANError anError) {
                     p.dismiss();
                       Log.v("ErrorInApi",anError.getErrorDetail().toString());

                       Toast.makeText(Container.this, "Error", Toast.LENGTH_SHORT).show();
                   }
               });
                /*.*/

    }

    private void parseUserJson(String response) {

        Log.v("Response",response);


        if(!response.trim().equals("Failed"))
        {
            showSnackbar("Form Submitted Successfully!");
            spinLead.setSelection(0);
            spinStatus.setSelection(0);
            spinRound1.setSelection(0);
            spinRound2.setSelection(0);
            etTotalVotes.setText("");


            RoundID=Utils.getSpinnerItemId(DB.Table.Name.CountingRoundList,DB.Table.CountingRoundList.ID.toString(),DB.Table.CountingRoundList.CountingRoundName.toString(),Round,Container.this);
            Log.v("RoundId","Roundid:"+RoundID);
            if(!RoundType.isEmpty())
            validation.setStatus(RoundID,RoundType,ConstituencyID,LeadCandidateID);

            if(!base641.equals("")){

                Toast.makeText(this, "Image is uploading..", Toast.LENGTH_SHORT).show();

                imageUploadApi();
               // getData();
            }
            RvList.setAdapter(mAdapter);
        }
        else
        {
            p.cancel();

            showSnackbar("Form not submitted !");
        }
    }

    public void showSnackbar(String msg) {
         snackbar = Snackbar
                .make(cordinator, msg, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    View imageView=null;
    //View textView=null;
    public void selectPic(View v)
    {

        imageView=v;
        PickSetup setup = new PickSetup();

        PickImageDialog.on(Container.this, setup);


        /*PickImageDialog.on(Container.getSupportFragmentManager(), new IPickResult() { // for fragment
            @Override
            public void onPickResult(PickResult r) {

                Log.v("ResultBitmap",r.getBitmap().toString());
                Log.v("ResultUri",r.getUri().toString());
                Log.v("Resultpath",r.getPath().toString());


                selectedImagePath=r.getPath();

                int pos = selectedImagePath.lastIndexOf("/");

                String image_name =selectedImagePath.substring(pos+1 , selectedImagePath.length());
                etUpload.setText(image_name);
                bitmap=getCameraPhotoOrientation(r,getActivity(),r.getUri(),r.getPath());


                PreviewMedia(getCameraPhotoOrientation(r,getActivity(),r.getUri(),r.getPath()),"");
                insertImageData(Integer.parseInt(pref.get(Constants.Fposition)),selectedImagePath,image_name);

                //PreviewMedia(r.getBitmap(),"");
            }
        });*/
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {

            Log.v("ResultBitmap",r.getBitmap().toString());
            Log.v("ResultUri",r.getUri().toString());
            Log.v("Resultpath",r.getPath().toString());

            picPath1=r.getPath();
            selectedImagePath=r.getPath();

            int pos = selectedImagePath.lastIndexOf("/");

            String image_name =selectedImagePath.substring(pos+1 , selectedImagePath.length());
            ///etUpload.setText(image_name);
            bitmap=getCameraPhotoOrientation(r,Container.this,r.getUri(),r.getPath());

            ImageView selectImage= (ImageView)imageView;

            selectImage.setImageBitmap(bitmap);

            switch (imageView.getId())
            {
                case R.id.pic1:base641=bitmapToBase64(bitmap);break;
                case R.id.pic2:base642=bitmapToBase64(bitmap);break;
                case R.id.pic3:base643=bitmapToBase64(bitmap);break;
            }

            //Log.v("ImageID","base641:-"+base641+"base642:-"+base642+"base643:-"+base643);
            Log.v("ImageID","base641:-"+base641);
            Log.v("ImageID","base642:-"+base642);
            Log.v("ImageID","base643:-"+base643);

            ViewGroup vg=(ViewGroup)imageView.getParent();

            View v = vg.getChildAt(1);

            TextView tv=(TextView)v;

            tv.setText("");

            //PreviewMedia(getCameraPhotoOrientation(r,getActivity(),r.getUri(),r.getPath()),"");
            //insertImageData(Integer.parseInt(pref.get(Constants.Fposition)),selectedImagePath,image_name);

            //PreviewMedia(r.getBitmap(),"");
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
        }
    }

    public Bitmap getCameraPhotoOrientation(PickResult r, Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        int orientation=0;
        Bitmap ImageBitmap=null;
        ImageBitmap=r.getBitmap();
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    ImageBitmap =rotateImage(r.getBitmap(),rotate);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    ImageBitmap =rotateImage(r.getBitmap(),rotate);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    ImageBitmap =rotateImage(r.getBitmap(),rotate);
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  ImageBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {


        Matrix matrix = new Matrix();
        if (angle != 0f) {
            matrix.preRotate(angle);
        }
        //matrix.postRotate(angle);
        //retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        Bitmap adjustedBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return adjustedBitmap;
    }

    public void imageCancel(View v) {
        ViewGroup vg=(ViewGroup)v.getParent();

        ViewGroup vgchild=(ViewGroup)vg.getChildAt(0);

        ImageView iv=(ImageView) vgchild.getChildAt(0);

        switch (vgchild.getChildAt(0).getId())
        {
            case R.id.pic1:base641="";break;
            case R.id.pic2:base642="";break;
            case R.id.pic3:base643="";break;
        }

        //Log.v("ImageID","base641:-"+base641+"base642:-"+base642+"base643:-"+base643);
        Log.v("ImageID","base641:-"+base641);
        Log.v("ImageID","base642:-"+base642);
        Log.v("ImageID","base643:-"+base643);

        View tView=vgchild.getChildAt(1);

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.circle_transparent);

        iv.setImageBitmap(icon);

        setText(tView);

    }

    void setText(View v) {
        TextView tv;
        switch (v.getId())
        {
            case R.id.tv1: tv=(TextView)v ;tv.setText("Select Photo 1");break;
            case R.id.tv2: tv=(TextView)v ;tv.setText("Select Photo 2");break;
            case R.id.tv3: tv=(TextView)v ;tv.setText("Select Photo 3");break;
        }
    }

    void addDynamicLYViews() {
        View views;
        //llViews.removeAllViews();

        for (int i = 0; i < FormList.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            views = inflater.inflate(R.layout.add_layout, null);

            HashMap<String, Object> objectMap = new HashMap<>();
            FormData fd = FormList.get(i);

            TextView tvCandidateName = (TextView) views.findViewById(R.id.n);
            objectMap.put(Constants.CandidateName, tvCandidateName);
            tvCandidateName.setText(fd.getCandidateName());

            EditText etVote = (EditText) views.findViewById(R.id.vl);
            objectMap.put(Constants.Vote, etVote);

            TextView tvPartyName = (TextView) views.findViewById(R.id.i);
            objectMap.put(Constants.PartyName, tvPartyName);
            tvPartyName.setText(fd.getPartyName());

            Objects.add(objectMap);

           // llViews.addView(views);


        }
    }

    void getDynamicLYViews() {
        for (int i = 0; i < Objects.size(); i++)
        {
          EditText etVote= (EditText)Objects.get(i).get(Constants.Vote);
            Log.v("etVote",etVote.getText().toString());
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    public void getSpinner(String spinner, String Value) {

        switch (spinner)
        {
            case "Constituency"         :ConstituencyID=Value;
                                        FormList.clear();
                                        getData();
                                        recycleAdapter();
                                        spinners.getSpinner(spinLead,Lead_List,R.layout.spinner_adapter_white_bg);
                                        break;
            case "LeadCandidate"        :LeadCandidateID=Value;Log.v("LeadCandidateID","ll:"+LeadCandidateID);break;
            case "Round1"                :Round1=Value;RoundID1=Utils.getSpinnerItemId(DB.Table.Name.CountingRoundList,DB.Table.CountingRoundList.ID.toString(),DB.Table.CountingRoundList.CountingRoundName.toString(),Round1,this);break;
            case "Round2"                :Round2=Value;RoundID2=Utils.getSpinnerItemId(DB.Table.Name.CountingRoundList,DB.Table.CountingRoundList.ID.toString(),DB.Table.CountingRoundList.CountingRoundName.toString(),Round2,this);break;
            case "RegularNotification"  :RegularNotification=Utils.getSpinnerItemId(DB.Table.Name.RegularNotificationList,DB.Table.RegularNotificationList.ID.toString(),DB.Table.RegularNotificationList.RegularNotificationName.toString(),Value,this);break;
        }

    }

    public void imageUploadApi() {

        JSONObject json= new JSONObject();

        try {

            json.put("strBase64_1",base641);
            json.put("strFileName1",PhotoID+"1");
            json.put("strBase64_2","");
            json.put("strFileName2","");
            json.put("strBase64_3","");
            json.put("strFileName3","");

            Log.v("imagejson",json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        String url="http://projects.karvyinsights.com/ElectionService/Home/UploadImage";
        Log.v("imagejson",base641);
        AndroidNetworking.post(url)
                .addJSONObjectBody(json)
                .setContentType("application/json; charset=utf-8") // custom ContentType
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("response",response);
                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.circle_transparent);

                        iv1.setImageBitmap(icon);
                        tvCancel.setText("Select Photo 1");
                        if(response.trim().contains("Uploaded"))
                        {
                            snackbar.dismiss();
                            showSnackbar("Image Submitted Successfully!");
                            changeStatus("1");
                        }
                        else
                        {
                            snackbar.dismiss();
                            showSnackbar("Image not Submitted!");

                            changeStatus("0");
                        }
                        p.cancel();


                    }

                    @Override
                    public void onError(ANError anError) {
                        p.dismiss();

                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.circle_transparent);
                        iv1.setImageBitmap(icon);
                        tvCancel.setText("Select Photo 1");
                        snackbar.dismiss();
                        showSnackbar("Image not Submitted!");
                        changeStatus("0");
                        Log.v("ErrorInApi",anError.getMessage());
                        Toast.makeText(Container.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void changeStatus(String status){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(DB.Table.imageUploaded.status.toString(), status);
        map.put(DB.Table.imageUploaded.path.toString(), picPath1);
        map.put(DB.Table.imageUploaded.photo_id.toString(), PhotoID);
        map.put(DB.Table.imageUploaded.round_id.toString(), RoundID);
        map.put(DB.Table.imageUploaded.user_id.toString(), pref.get(Constants.userId));
        db.insert(DB.Table.Name.imageUploaded,map);



    }
}
