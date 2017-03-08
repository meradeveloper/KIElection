package com.example.in.kielection.Common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.in.kielection.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunil.ambilpura on 10/22/2016.
 */

public class Utils {


    public static void showToast(Activity mActivity, String s) {
        Toast.makeText(mActivity,s, Toast.LENGTH_LONG).show();
    }
// ******* CHECK EMAIL VALIDATION *******


    public static boolean isEmailValid(String email) {

        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static String getSpinnerItemId(String table_name , String column_name, String condition, String condition_name , Context context  )
    {
        DB db=new DB(context);
        Cursor cur = null;
        String id="";

        String item_query = " SELECT "+ column_name +" FROM " + table_name + " WHERE "+condition+" = "+'"'+condition_name+'"' ;

        cur = db.findCursor(item_query, null);

        //Toast.makeText(this, "length = " + cur.getCount(), Toast.LENGTH_LONG).show();

        if (cur != null && cur.moveToNext()) {


            id= cur.getString(cur.getColumnIndex(column_name));

        }


        cur.close();
        return id;

        //Log.v("Locality= ",Location.toString());
        //Toast.makeText(this, "location_id : " + Location.toString(), Toast.LENGTH_LONG).show();
    }

    // ===================================================================

    public static String getCompleteApiUrl(Context ctx, int api) {

        return "http://" + ctx.getString(R.string.server) + "/"
                + ctx.getString(R.string.api_intermediary_path) + "/"
                + ctx.getString(api);
    }

    // ======================================================================

    public static boolean isNetworkConnectedMainThred(Context ctx) {

        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null)
            return false;
        else
            return true;
    }

    public static void show(Context a , String msg)
    {

        Toast.makeText(a, msg, Toast.LENGTH_SHORT).show();

    }
    public static void goAnotherActivity(Activity a, Class c)
    {

        a.startActivity(new Intent(a,c));
        a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        a.finish();

    }

    public static void goneAnimation(final View v)
    {
        v.animate()
                .translationY(0)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        v.setVisibility(View.GONE);
                    }
                });
    }

    public static void onAnimation(final View v)
    {

        v.animate()
                .translationY(0)
                .alpha(0.6f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        v.setVisibility(View.VISIBLE);
                        v.setAlpha(0.0f);
                    }
                });
    }

    public static boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber) && sameDigit(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        //Log.v("samestatus", String.valueOf(status));
        return false;
    }

    public static boolean isValidEmail(CharSequence email) {
        if (!TextUtils.isEmpty(email)) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
        return false;
    }

    public static boolean sameDigit(CharSequence Number)
    {
        boolean status=false;
        Pattern sameDigits = Pattern.compile("(\\d)(\\1){6,}");

        if (sameDigits.matcher(Number).matches())
            status=false;
        else
            status=true;

        Log.v("samestatus", String.valueOf(status));
        return status;
    }

}
