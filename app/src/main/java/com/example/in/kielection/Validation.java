package com.example.in.kielection;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.example.in.kielection.Common.DB;
import com.example.in.kielection.Common.Utils;

import java.util.HashMap;

/**
 * Created by Localadmin on 2/14/2017.
 */

public class Validation {

    Context context;
    DB db;

    public Validation(Context context)
    {
        this.context=context;
        db= new DB(context);
    }

    public void setStatus(String Round,String RoundType ,String CID, String LeadingID )
    {
        HashMap<String,String> map = new HashMap<>();
        map.put(DB.Table.RoundValidation.status.toString(),"1");
        map.put(DB.Table.RoundValidation.roundType.toString(),RoundType);
        map.put(DB.Table.RoundValidation.roundID.toString(),Round);
        map.put(DB.Table.RoundValidation.ConstituencyID.toString(),CID);
        map.put(DB.Table.RoundValidation.LeadingID.toString(),LeadingID);
        //String id= Utils.getSpinnerItemId(DB.Table.Name.CountingRoundList,DB.Table.CountingRoundList.ID.toString(),DB.Table.CountingRoundList.CountingRoundName.toString(),Round,context);

        //Log.v("roundid","id:-"+Round+"Roundtype"+RoundType);
        db.autoInsertUpdate(DB.Table.Name.RoundValidation,map,DB.Table.RoundValidation.roundID.toString()+" = "+Round+" AND "+DB.Table.RoundValidation.roundType.toString()+" = "+"'"+RoundType+"'"+" AND "+DB.Table.RoundValidation.ConstituencyID.toString()+" = "+CID,null);
        //Toast.makeText(context, Round+" is Submit.", Toast.LENGTH_SHORT).show();
    }

    boolean getStatus(String RoundingID,String CID,String roundType)
    {

        boolean bool=false;
        String roundID = getRoundID(DB.Table.Name.RoundValidation,DB.Table.RoundValidation.roundID.toString(),CID,roundType);

        //Log.v("roundstatus","RoundingID:-"+RoundingID+"roundID:"+roundID);

        if(!roundID.isEmpty() && roundID!=null)
        {
            if(Integer.parseInt(RoundingID)<=Integer.parseInt(roundID))
                bool=false;
            else if(Integer.parseInt(RoundingID)>=Integer.parseInt(roundID))
                bool=true;
        }
        else
        {
            bool=true;
        }


        return bool;
    }

    public String getRoundID(String table_name , String column_name , String CID, String roundType)
    {
        DB db=new DB(context);
        Cursor cur = null;
        String roundID="";


        String item_query = " SELECT "+ column_name +" FROM " + table_name + " WHERE "+DB.Table.RoundValidation.status.toString()+" = '1' AND "+DB.Table.RoundValidation.ConstituencyID.toString()+" = '"+CID+"' AND "+DB.Table.RoundValidation.roundType.toString()+" = "+'"'+roundType+'"' ;
        Log.v("itemQuery",item_query);
        cur = db.findCursor(item_query, null);

        //Log.v("roundLength", String.valueOf(cur.getCount()));


        if (cur != null && cur.moveToNext()) {

            for(int i=0;i<cur.getCount();i++)
            {
                roundID = cur.getString(cur.getColumnIndex(column_name));
                //Log.v("roundLengthId", roundID);

                cur.moveToNext();
            }



        }

        cur.close();
        return roundID;

    }

    public String getLeadingCandidate(String CID, String roundType, String roundID)
    {
        DB db=new DB(context);
        Cursor cur = null;
        String LeadingID="";


        String item_query = " SELECT "+ DB.Table.RoundValidation.LeadingID +" FROM " + DB.Table.Name.RoundValidation + " WHERE "+DB.Table.RoundValidation.status.toString()+" = '1' AND "+DB.Table.RoundValidation.ConstituencyID.toString()+" = '"+CID+"' AND "+DB.Table.RoundValidation.roundType.toString()+" = "+'"'+roundType+'"'+" AND "+DB.Table.RoundValidation.roundID.toString()+" = "+'"'+roundID+'"' ;
        Log.v("itemQuery",item_query);
        cur = db.findCursor(item_query, null);

        Log.v("leadLength", String.valueOf(cur.getCount()));


        if (cur != null && cur.moveToNext()) {

            for(int i=0;i<cur.getCount();i++)
            {
                LeadingID = cur.getString(cur.getColumnIndex(DB.Table.RoundValidation.LeadingID.toString()));
                //Log.v("roundLengthId", roundID);

                cur.moveToNext();
            }



        }

        cur.close();
        return LeadingID;

    }
}
