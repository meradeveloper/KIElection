package com.example.in.kielection;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;

import com.example.in.kielection.Common.Constants;
import com.example.in.kielection.Common.DB;
import com.example.in.kielection.Common.Preferences;
import com.example.in.kielection.Common.Utils;

public class MainActivity extends Activity {

    Handler handler;
    Preferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler=new Handler();
        pref=new Preferences(this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(pref.get(Constants.userId).equals(""))
                    Utils.goAnotherActivity(MainActivity.this, LoginActivity.class);
                else
                {
                    //Utils.goAnotherActivity(MainActivity.this,ConstituencyActivity.class);
                    String status=Utils.getSpinnerItemId(DB.Table.Name.user_detail,DB.Table.user_detail.constituency_status.toString(),DB.Table.user_detail.id.toString(),pref.get(Constants.userId),MainActivity.this);
                    //if(status.equals("0"))

                    /* commented to be open*/

                    if(pref.get(Constants.Constituency_status).equals("") || pref.get(Constants.Constituency_status).equals("0"))
                        Utils.goAnotherActivity(MainActivity.this,ConstituencyActivity.class);
                    else if(pref.get(Constants.Constituency_status).equals("1"))
                        Utils.goAnotherActivity(MainActivity.this,Container.class);
                }

            }
        },3000);
    }
}
