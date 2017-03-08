package com.example.in.kielection;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.in.kielection.Common.Constants;
import com.example.in.kielection.Common.DB;
import com.example.in.kielection.Common.Preferences;
import com.example.in.kielection.Common.Utils;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoginTimerCallback {


    Preferences pref;
    AlertDialog p;
    private EditText etUser;
    private EditText etPassword;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        pref=new Preferences(LoginActivity.this);
        db=new DB(this);
        etUser = (EditText) findViewById(R.id.email);
        p=new SpotsDialog(LoginActivity.this,R.style.Custom);
        p.setCancelable(false);
        p.setCanceledOnTouchOutside(false);


    }

    public void SignIn(View view) {

        if(Utils.isValidPhoneNumber(etUser.getText().toString()))
        {
            p.show();
            Hit_Login_api();
            //Hit_Volley_Login();
            //HIt_GetCandidate();
        }
        else
            Utils.show(this,"Please Enter Correct Mobile !");

    }


    void Hit_Volley_Login()
    {
        p.show();
        String url="http://projects.karvyinsights.com/ElectionService/Election/ValidateElectionUsers?strUID=praveenprakash&strPWD=admin@123";

        //url=url.replace(" ","%20");


        Log.v("HIt_SlipDetailsAPI", url);

        /*RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //pd.dismiss();
                Log.v("reponse", "" + response);
               // parse_Json3(response);

            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("errorresponse","Error: " + error.getMessage());
                Utils.show(LoginActivity.this,"Some Error Found");
                p.cancel();
            }
        });

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsObjRequest);*/

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("Response", response.toString());

                        // Parsing json array response
                        // loop through each json object


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.v("errorresponse","Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                p.cancel();

            }
        });

        req.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(req);


    }

    public void Hit_Login_api() {
        String url="http://projects.karvyinsights.com/ElectionService/Election/ValidateElectionUsers?strMobileNumber="+etUser.getText().toString().trim();

        /*http://projects.karvyinsights.com/ElectionService/Election/ValidateElectionUsers?strUID=praveenprakash&strPWD=admin@123*/

        Log.v("hit_api_get",url);
        AndroidNetworking.get(url) /*.setContentType("application/json; charset=utf-8") */// custom ContentType
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        parseUserJson(response);
                        Log.v("response","value"+response.toString());
                        p.cancel();

                    }
                    @Override
                    public void onError(ANError error) {
                        p.cancel();

                        Log.v("SomeError",error.getErrorDetail().toString());
                        Toast.makeText(getApplicationContext(), "Network Error or Internet Issue", Toast.LENGTH_SHORT).show();

                    }
                });
                /*.*/

    }


    private void parseUserJson(JSONArray response) {

        try {

            db.truncate(DB.Table.Name.CountingRoundList);
            db.truncate(DB.Table.Name.ConstituencyList);
            db.truncate(DB.Table.Name.RegularNotificationList);
            db.truncate(DB.Table.Name.user_detail);

            for(int i=0;i<response.length();i++) {
                JSONObject obj = response.getJSONObject(i);
                pref.set(Constants.Password,obj.getString("Password"));
                pref.set(Constants.userId,obj.getString("UserId"));
                pref.set(Constants.username,obj.getString("Username"));
                pref.set(Constants.email,obj.getString("Email"));
                pref.set(Constants.name,obj.getString("FirstName")+" "+obj.getString("LastName"));
                pref.set(Constants.Mobile,obj.getString("Mobile"));
                pref.set(Constants.stateId,String.valueOf(obj.getInt("StateID")));
                pref.set(Constants.cStationId,String.valueOf(obj.getInt("CStationID")));
                pref.set(Constants.Phone,obj.getString("Phone"));
                pref.commit();

                HashMap<String,String> usermap= new HashMap<>();

                usermap.put(DB.Table.user_detail.id.toString(),obj.getString("UserId"));
                usermap.put(DB.Table.user_detail.username.toString(),obj.getString("Username"));
                usermap.put(DB.Table.user_detail.email.toString(),obj.getString("Email"));
                usermap.put(DB.Table.user_detail.name.toString(),obj.getString("FirstName")+" "+obj.getString("LastName"));
                usermap.put(DB.Table.user_detail.mobile.toString(),obj.getString("Mobile"));
                usermap.put(DB.Table.user_detail.stateid.toString(),obj.getString("StateID"));
                usermap.put(DB.Table.user_detail.cStationId.toString(),obj.getString("CStationID"));
                usermap.put(DB.Table.user_detail.phone.toString(),obj.getString("Phone"));
                usermap.put(DB.Table.user_detail.constituency_status.toString(),"0");



                db.insert(DB.Table.Name.user_detail,usermap);
                //db.autoInsertUpdate(DB.Table.Name.user_detail,usermap,DB.Table.user_detail.id.toString()+"="+obj.getString("UserId"),null);

                JSONArray ConstituencyList=obj.getJSONArray("ConstituencyList");

                if(ConstituencyList.length()==0)
                {
                    Log.v("sizeconst", String.valueOf(ConstituencyList.length()));
                    pref.set(Constants.Constituency_status,"0");
                    pref.commit();
                }
                else if(ConstituencyList.length()>0)
                {
                    pref.set(Constants.Constituency_status,"1");
                    pref.commit();
                }

                for (int l=0;l<ConstituencyList.length();l++)
                {
                    JSONObject Constituency_obj= ConstituencyList.getJSONObject(l);

                    Log.v("ConstituencyList",":-"+Constituency_obj.getString("ConstituencyName"));
                    HashMap<String,String> Constituency_map= new HashMap<>();
                    Constituency_map.put(DB.Table.ConstituencyList.ID.toString(),Constituency_obj.getString("ID"));
                    Constituency_map.put(DB.Table.ConstituencyList.ConstituencyCode.toString(),Constituency_obj.getString("ConstituencyCode"));
                    Constituency_map.put(DB.Table.ConstituencyList.ConstituencyName.toString(),Constituency_obj.getString("ConstituencyName"));
                    Constituency_map.put(DB.Table.ConstituencyList.UserID.toString(),Constituency_obj.getString("UserID"));

                    db.autoInsertUpdate(DB.Table.Name.ConstituencyList,Constituency_map,DB.Table.ConstituencyList.ID.toString()+"="+Constituency_obj.getString("ID"),null);
                }

                JSONArray CountingRoundList=obj.getJSONArray("CountingRoundList");

                for (int l=0;l<CountingRoundList.length();l++)
                {
                    JSONObject CountingRound_obj= CountingRoundList.getJSONObject(l);

                    HashMap<String,String> CountingRound_map= new HashMap<>();
                    CountingRound_map.put(DB.Table.CountingRoundList.ID.toString(),CountingRound_obj.getString("ID"));
                    CountingRound_map.put(DB.Table.CountingRoundList.CountingRoundCode.toString(),CountingRound_obj.getString("CountingRoundCode"));
                    CountingRound_map.put(DB.Table.CountingRoundList.CountingRoundName.toString(),CountingRound_obj.getString("CountingRoundName"));
                    CountingRound_map.put(DB.Table.CountingRoundList.UserID.toString(),obj.getString("UserId"));
                    CountingRound_map.put(DB.Table.CountingRoundList.status.toString(),"0");

                    db.autoInsertUpdate(DB.Table.Name.CountingRoundList,CountingRound_map,DB.Table.ConstituencyList.ID.toString()+"="+CountingRound_obj.getString("ID"),null);
                }

                JSONArray RegularNotificationList=obj.getJSONArray("RegularNotificationList");

                for (int l=0;l<RegularNotificationList.length();l++)
                {
                    JSONObject RegularNotification_obj= RegularNotificationList.getJSONObject(l);

                    HashMap<String,String> RegularNotification_map= new HashMap<>();
                    RegularNotification_map.put(DB.Table.RegularNotificationList.ID.toString(),RegularNotification_obj.getString("ID"));
                    RegularNotification_map.put(DB.Table.RegularNotificationList.RegularNotificationCode.toString(),RegularNotification_obj.getString("RegularNotificationCode"));
                    RegularNotification_map.put(DB.Table.RegularNotificationList.RegularNotificationName.toString(),RegularNotification_obj.getString("RegularNotificationName"));
                    RegularNotification_map.put(DB.Table.RegularNotificationList.UserID.toString(),obj.getString("UserId"));

                    db.autoInsertUpdate(DB.Table.Name.RegularNotificationList,RegularNotification_map,DB.Table.ConstituencyList.ID.toString()+"="+RegularNotification_obj.getString("ID"),null);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HIt_GetCandidate();
    }

    public void HIt_GetCandidate() {
        String url="http://projects.karvyinsights.com/ElectionService/Election/DownloadCandidates?strMobileNumber="+etUser.getText().toString().trim()
                ;


        Log.v("hit_api_get",url);
        AndroidNetworking.post(url) .setContentType("application/json; charset=utf-8") // custom ContentType
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        parseJson(response);
                        Log.v("response","value"+response.toString());
                        p.cancel();

                    }
                    @Override
                    public void onError(ANError error) {
                        p.cancel();
                        Log.v("SomeError2",error.toString());
                        Toast.makeText(getApplicationContext(), "Network Error or Internet Issue", Toast.LENGTH_SHORT).show();

                    }
                });
                /*.*/

    }


    private void parseJson(JSONArray response) {
         db.truncate(DB.Table.Name.PartyDetail);
        try {

            JSONObject objGlobal=null;

            for(int i=0;i<response.length();i++) {
                JSONObject obj = response.getJSONObject(i);
                HashMap<String,String>  map=new HashMap();

                Log.v("CandidateName",":-"+obj.getString("CandidateName"));
                map.put(DB.Table.PartyDetail.CandidateID.toString(),String.valueOf(obj.getInt("CandidateID")));
                map.put(DB.Table.PartyDetail.CandidateName.toString(),obj.getString("CandidateName"));
                map.put(DB.Table.PartyDetail.PartyID.toString(),String.valueOf(obj.getInt("PartyID")));
                map.put(DB.Table.PartyDetail.PartyName.toString(),obj.getString("PartyName"));
                map.put(DB.Table.PartyDetail.PartyCode.toString(),obj.getString("PartyCode"));
                map.put(DB.Table.PartyDetail.CategoryID.toString(),String.valueOf(obj.getInt("CategoryID")));
                map.put(DB.Table.PartyDetail.ConstituencyID.toString(),String.valueOf(obj.getInt("ConstituencyID")));
                map.put(DB.Table.PartyDetail.CountingStationID.toString(),String.valueOf(obj.getInt("CountingStationID")));

                Log.v("CandidateName","PartyName:-"+obj.getString("PartyName"));

                objGlobal=obj;
                db.insert(DB.Table.Name.PartyDetail,map);
            }

            /*HashMap<String,String>  map=new HashMap();
            map.put(DB.Table.PartyDetail.CandidateID.toString(),String.valueOf(Integer.parseInt(String.valueOf(objGlobal.getInt("CandidateID")))+1));
            map.put(DB.Table.PartyDetail.CandidateName.toString(),"NOTA");
            map.put(DB.Table.PartyDetail.PartyID.toString(),String.valueOf(Integer.parseInt(String.valueOf(objGlobal.getInt("PartyID")))+1));
            map.put(DB.Table.PartyDetail.PartyName.toString(),"NOTA");
            map.put(DB.Table.PartyDetail.CategoryID.toString(),String.valueOf(Integer.parseInt(String.valueOf(objGlobal.getInt("CategoryID")))+1));
            db.insert(DB.Table.Name.PartyDetail,map);*/

        } catch (JSONException e) {
            e.printStackTrace();
        }



        String status=Utils.getSpinnerItemId(DB.Table.Name.user_detail,DB.Table.user_detail.constituency_status.toString(),DB.Table.user_detail.id.toString(),pref.get(Constants.userId),LoginActivity.this);

        Log.v("constituencystatus","constituencystatus2"+status+" user_id"+pref.get(Constants.userId));
        //if(status.equals("0"))
        Utils.goAnotherActivity(LoginActivity.this,ConstituencyActivity.class);

        /* commented to be open */

        /*if(pref.get(Constants.Constituency_status).equals("") || pref.get(Constants.Constituency_status).equals("0"))
            Utils.goAnotherActivity(LoginActivity.this,ConstituencyActivity.class);
        else if(pref.get(Constants.Constituency_status).equals("1"))
            Utils.goAnotherActivity(LoginActivity.this,Container.class);*/
        p.dismiss();

        Toast.makeText(LoginActivity.this, "You have Successfully Logged In!", Toast.LENGTH_SHORT).show();
    }

    public boolean check() {
        Boolean check=false;
        if(etUser.getText().toString().equals(""))
        {
            Utils.show(this,"Please Enter Mobile !");
        }
        else if(!Utils.isValidPhoneNumber(etUser.getText().toString()))
        {
            Utils.show(this,"Please Enter Correct Mobile !");
        }

        else
        {
            check=true;
        }
        return check;
    }



    @Override
    public void getTimer(String timer) {


    }
}

