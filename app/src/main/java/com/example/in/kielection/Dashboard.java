package com.example.in.kielection;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.in.kielection.Common.Constants;
import com.example.in.kielection.Common.DB;
import com.example.in.kielection.Common.Preferences;
import com.example.in.kielection.Container;
import com.example.in.kielection.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class Dashboard extends AppCompatActivity {

    TextView tvSubmit,tvUpload,tvPending;

    Preferences pref;

    DB db;

    CoordinatorLayout cordinator;

    String base64="";

    int uploded=0,pending=0,count=0;

    ImageView iv;

    AlertDialog p;

    public static ArrayList<HashMap<String,String>>  Imagelist=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dashboard");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        p         =new SpotsDialog(Dashboard.this,R.style.Custom);

        pref      =new Preferences(this);

        db        =new DB(this);

        cordinator  = (CoordinatorLayout)findViewById(R.id.cordinator);

        iv  = (ImageView)findViewById(R.id.iv);

        tvUpload  = (TextView)findViewById(R.id.upload);
        tvPending = (TextView)findViewById(R.id.pending);
        tvSubmit  = (TextView)findViewById(R.id.submit);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                p.show();
                hitImageApi();
            }
        });
        getUploadedNo();
    }

    void hitImageApi() {
        Toast.makeText(this, String.valueOf(count)+"and"+String.valueOf(pending), Toast.LENGTH_SHORT).show();
       if(count==pending){
           p.cancel();
           pending=0;
           uploded=0;
           count=0;
           getUploadedNo();
       }
        else
       {
           p.show();
           Log.v("values",Imagelist.get(count).get("path")+"++"+Imagelist.get(count).get("photo_id")+"++"+Imagelist.get(count).get("round_id"));
           base64=bitmapToBase64(getBitmap(Imagelist.get(count).get("path")));
           imageUploadApi1(Imagelist.get(count).get("photo_id"),Imagelist.get(count).get("round_id"));

       }
    }

    public void imageUploadApi(String base64,String photoId,final String roundID) {
p.show();
        JSONObject json= new JSONObject();

        try {

            json.put("strBase64_1",base64);
            json.put("strFileName1",photoId+"1");
            json.put("strBase64_2","");
            json.put("strFileName2","");
            json.put("strBase64_3","");
            json.put("strFileName3","");

            Log.v("imagejson",json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url="http://projects.karvyinsights.com/ElectionService/Home/UploadImage";
        Log.v("imagejson",base64);
        AndroidNetworking.post(url)
                .addJSONObjectBody(json)
                .setContentType("application/json; charset=utf-8") // custom ContentType
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("response",response);
                        p.cancel();
                        if(!response.trim().contains("Uploaded"))
                        {
                            //showSnackbar("Image not Submitted!");
                        }
                        else
                        {
                            showSnackbar("Image Submitted Successfully!");
                            changeStatus("1",roundID);
                        }

                    }
                    @Override
                    public void onError(ANError anError) {

                        p.cancel();
                        Log.v("ErrorInApi","abd"+anError.getErrorDetail());
                    //    showSnackbar(" Submitted!");
                     //
                      //  Toast.makeText(Dashboard.this, "Error", Toast.LENGTH_SHORT).show();


                    }
                });

        count++;
      //  hitImageApi();
    }

    public void imageUploadApi1(String PhotoID,final String roundID) {

        JSONObject json= new JSONObject();


        try {


            json.put("strFileName1",PhotoID+"1");
            json.put("strBase64_1",base64);
            json.put("strFileName2","");
            json.put("strBase64_2","");
            json.put("strBase64_3","");
            json.put("strFileName3","");

            Log.v("imagejson",json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        String url="http://projects.karvyinsights.com/ElectionService/Home/UploadImage";
        Log.v("imagejson",base64);
        AndroidNetworking.post(url)
                .addJSONObjectBody(json)
                .setContentType("application/json; charset=utf-8") // custom ContentType
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("response",response);

                        if(response.trim().contains("Uploaded"))
                        {

                            showSnackbar("Image Submitted Successfully!");
                            changeStatus("1",roundID);
                        }
                        else
                        {
                            showSnackbar("Image not Submitted!");

                            changeStatus("0",roundID);
                        }
                        p.cancel();


                    }

                    @Override
                    public void onError(ANError anError) {
                        p.dismiss();
                        showSnackbar("Image not Submitted!");
                        changeStatus("0",roundID);
                        Toast.makeText(Dashboard.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void getUploadedNo() {
        Cursor cur=null;
        String query="Select * from imageUploaded where user_id = '"+pref.get(Constants.userId)+"'";
        cur=db.findCursor(query,null);

        if(cur!=null&& cur.moveToNext())
        {
            for(int i=0;i<cur.getCount();i++)
            {
                Log.v("Status_imageStatus","Status_imageStatus");
               if(cur.getString(cur.getColumnIndex("status")).trim().equals("1")){
                   uploded++;
                   Log.e("Status_Uploaded",String.valueOf(uploded));
               }
                else   if(cur.getString(cur.getColumnIndex("status")).trim().equals("0")){
                  pending++;
                   HashMap<String,String> map=new HashMap();
                   map.put("path",cur.getString(cur.getColumnIndex("path")).trim());
                   map.put("round_id",cur.getString(cur.getColumnIndex("round_id")).trim());
                   map.put("photo_id",cur.getString(cur.getColumnIndex("photo_id")).trim());
                   Imagelist.add(map);
                   Log.e("Status_pending",String.valueOf(pending));
               }
                cur.moveToNext();

            }
        }

        cur.close();
        Log.v("ImageList",Imagelist.toString());
        tvPending.setText(String.valueOf(pending));
        tvUpload.setText(String.valueOf(uploded));
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

    void changeStatus(String status,String RoundID){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(DB.Table.Answere.imageStatus.toString(), status);
        db.update("Answere", map, "userID = " + "'" + pref.get(Constants.userId) + "'" + " AND round_id = " + "'" + RoundID + "'", null);
    }

    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar
                .make(cordinator, msg, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    /////////////////// For Media Upload //////////////////////////////

    public Bitmap getBitmap(String path) {
        Bitmap bitmap = null;


        BitmapFactory.Options options = new BitmapFactory.Options();
        bitmap = null;
        Log.v("path", path.toString());
        bitmap = BitmapFactory.decodeFile(path,
                options);

        return getCameraPhotoOrientation(path, bitmap);
    }

    public Bitmap getCameraPhotoOrientation(String imagePath, Bitmap bitmap) {
        int rotate = 0;
        int orientation = 0;
        Bitmap ImageBitmap = null;
        ImageBitmap = bitmap;
        try {

            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    ImageBitmap = rotateImage(bitmap, rotate);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    ImageBitmap = rotateImage(bitmap, rotate);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    ImageBitmap = rotateImage(bitmap, rotate);
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ImageBitmap;
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

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
