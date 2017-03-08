package com.example.in.kielection.Common;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;


public class DB {

// ******* DECLARING VARIABLES *******

    public static String _DB_NAME = "kielection.sqlite";

    public static String _DB_BAK_NAME = "kielection_backup.sqlite";

    public static String _DB_PATH;

    private static int _DB_VERSION = 1;

    private Context _context;

    private SQLiteDatabase sqdb;

    private DBHelper helper;

    private static boolean hasBackUp = false;

    @SuppressWarnings("unused")
    private DBListener dbListener;

    // CONSTRUCTOR
    public DB(Context context) {

        initialize(context, _DB_NAME, true);

    }

    // CONSTRUCTOR
    public DB(Context context, boolean initialize) {

        initialize(context, _DB_NAME, initialize);
    }

    // CONSTRUCTOR
    public DB(Context context, String dbName, boolean initialize) {

        initialize(context, dbName, initialize);
    }

    // INITIALIZE METHOD
    @SuppressLint("SdCardPath")
    private void initialize(Context context, String dbName, boolean initialize) {

        this._context = context;
		/* handler = new Handler(); */
        _DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        helper   = new DBHelper(this._context, dbName, null, _DB_VERSION);

        if (initialize)
            createorUpgradeDatabse();
    }

    // CLEAR METHOD
    public void clear() {

        _context = null;
        sqdb = null;
        helper = null;
        dbListener = null;
    }

    // com.propliance.megainfomatix.Common.DB LISTENER
    public void setDBListener(DBListener listener) {
        dbListener = listener;
    }

    // OPEN com.propliance.megainfomatix.Common.DB IN WRITABLE MODE
    public void open() {
        sqdb = helper.getWritableDatabase();
    }

    // CLOSE com.propliance.megainfomatix.Common.DB
    public void close() {

        if (sqdb != null && sqdb.isOpen())
            sqdb.close();
    }

    public SQLiteDatabase getSqliteDB() {
        return sqdb;
    }

    // CREATE DATABASE BACKUP
    public static boolean backUp(Context c){

        Log.d("Back Up Called","");
        boolean status = false;

        File sf = new File(_DB_PATH,_DB_NAME);
        File df = new File(_DB_PATH,_DB_BAK_NAME);

        if(sf.exists()){

            try {

                c.deleteDatabase(_DB_BAK_NAME);
                df.createNewFile();
                copyDataBase(sf, df);
                hasBackUp = true; status = true;
            }
            catch (IOException e) {

            }
        }
        return status;
    }


    // RESTORE DATABASE
    public synchronized static boolean restore(Context c){

        //	Log.d("Restore Called");

        boolean status = false;

        File sf = new File(_DB_PATH,_DB_BAK_NAME);
        File df = new File(_DB_PATH,_DB_NAME);

        if(sf.exists() && hasBackUp){

            c.deleteDatabase(_DB_NAME);
            try {
                df.createNewFile();
                copyDataBase(sf, df);
            }
            catch (IOException e) {

                e.printStackTrace();
            }

            hasBackUp = true;
            status = true;
			

            hasBackUp = false;
            Log.d("FIle name ", "src " + sf.getName() + "  BC" + df.getName());
            Log.d("CheckDB","main" + sf.exists() + "    BC" + df.exists());

            status = true;
				

        }

        Log.d("Restore Completed", "***********");

        return status;
    }

    // CREATE OR UPGRADE DATABASE
    public void createorUpgradeDatabse() {

        boolean dbExist = checkDataBase();

        Log.d("ash_db", "Database Existence === " + dbExist);

        if (!dbExist) {

            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.

            try {
                sqdb = helper.getWritableDatabase();
            }
            catch (SQLException e) {
                Log.d("ash_exp", e.getMessage()	+ " at open(database not open for writing)");
            }

            try {
                copyDataBase();
                Log.d("ash_db", "Database Copied");

            }
            catch (IOException e) {

                Log.d("ash_exp", e.getMessage()	+ " at open (Database not Copied)");
            }
        }
        else {
            sqdb = helper.getWritableDatabase();
        }
    }

    // CHECK DATABASE
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = _DB_PATH + _DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        }
        catch (SQLiteException e) {

            // Log.d("ash","Database not exist yet");
        }

        if (checkDB != null) {

            checkDB.close();
        }

        return checkDB != null ? true : false;
    }
	

    // COPY DATABASE
    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = _context.getAssets().open(_DB_NAME);

        // Path to the just created empty db
        String outFileName = _DB_PATH + _DB_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[10240];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();

        // Close the streams
        myOutput.close();
        myInput.close();
    }

    // COPY DATABASE ONE FILE TO ANOTHER
    private static void copyDataBase(File from, File to) throws IOException {

        // Open your local db as the input stream
        InputStream myInput = new FileInputStream(from);

        // Path to the just created empty db

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(to);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[10240];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }



    //
    /** Clears rows as per where clause, else complete table. */
    public void clear(String tableName, String where) {

        getSqliteDB().execSQL("delete from " + tableName + (where == null ? "" : " where " + where));
    }

    // AUTO INSERT AND UPDATE
    public void autoInsertUpdate(String tableName, HashMap<String, String> values, String where, String[] args) {


        if (where != null && isRecordExist(tableName, where, args) != Constants.kZero) {
            if (tableName.equalsIgnoreCase("sg_users")) {

                Log.e("x", " Updated to database.................................");
            }
            update(tableName, values, where, args);
        } else {

            if (tableName.equalsIgnoreCase("sg_users")) {

                Log.e("xxx", " added to database.................................");
            }
            insert(tableName, values);
        }
    }

    // CHECK RECORD IS EXIST
    public int isRecordExist(String tableName, String where, String[] args) {

        int status = Constants.kZero;

        Log.e("isRecordExist", "tableName " + tableName + " where " + where);

        Cursor c = getSqliteDB().query(tableName, null, where, args, null, null, null);

        if (c.getCount() > 0) {

            c.moveToNext();
            status = c.getInt(0);
        }

        c.close();
        Log.e("isRecordExist", "status " + status);
        return status;
    }

    // CHECK RECORD IS EXIST
    public int isRecordExist(String query, String[] args) {

        int status = Constants.kZero;

        Cursor c = getSqliteDB().rawQuery(query, args);

        if (c.getCount() > 0) {

            c.moveToNext();
            status = c.getInt(0);
        }

        c.close();

        return status;
    }

    // INSERT
    public long insert(String tableName, HashMap<String, String> values) {

        ContentValues vals = createContentValues(values);
        Log.e("Inserting_data ", tableName+vals.toString());
        return getSqliteDB().insert(tableName, null, vals);

    }

    // UPDATE
    public int update(String tableName, HashMap<String, String> values, String where, String[] args) {

        ContentValues vals = createContentValues(values);
        Log.e("updating_data ", vals.toString());
        //+where.toString()+args.toString()
        return getSqliteDB().update(tableName, vals, where, args);
    }

    // DELETE
    public int delete(String tableName, String where, String[] args) {

        Log.v("deleted",tableName.toString());

        return getSqliteDB().delete(tableName, where, args);

    }

    // TRUNCATE
    public void truncate(String tableName) {

        getSqliteDB().execSQL("delete from " + tableName);
        Log.v("truncated",tableName.toString());
    }


    public Cursor findCursor(String sql, String[] args) {

        Log.d("SQL STRING IS", "" + sql);

        Cursor cursor = getSqliteDB().rawQuery(sql, args);
        return cursor;
    }

    // GET MAX VALUE
    public int getMaxValue(String tableName, String fieldName, String whereCaluse,String colName) {

        int maxVal = 0;
        int  id=0;
        Cursor c = null;

        String query = "SELECT MAX(" + fieldName + ") FROM " + tableName;

       /* if (whereCaluse != null) {
            query = query + " WHERE " + whereCaluse;
        }*/


        c = findCursor(query, null);
        if (c!=null&&c.moveToNext())
             id=c.getInt(c.getColumnIndex(colName));
        return id;

    }



    // GET MIN VALUE
    public int getMinValue(String tableName, String fieldName, String whereCaluse) {

        int minVal = 0;

        String query = "SELECT MIN(" + fieldName + ") FROM " + tableName;

        if (whereCaluse != null) {
            query = query + " WHERE " + whereCaluse;
        }

        Cursor c = findCursor(query, null);

        if (c.moveToNext())
            minVal = c.getInt(0);

        return minVal;
    }



    // GET COUNT
    public int getCount(String tableName, String whereCaluse) {

        int minVal = 0;

        String query = "SELECT COUNT(*) FROM " + tableName;

        if (whereCaluse != null) {
            query = query + " WHERE " + whereCaluse;
        }

        Cursor c = findCursor(query, null);

        if (c.moveToNext())
            minVal = c.getInt(0);

        return minVal;
    }


    public Cursor findCursor(String tableName, String where, String[] args, String other) {

        String sql = "select * from " + tableName + (where == null ? "" : " where " + where) + (other == null ? "" : " " + other);

        Log.d("Find Cursor Query == ", "" + sql);

        Cursor c = findCursor(sql, args);

        return c;
    }

    public Cursor findCursorNoWhere(String tableName, String where, String[] args, String other) {

        String sql = "select * from " + tableName + (where == null ? "" : " " + where) + (other == null ? "" : " " + other);

        Log.d("Find Cursor Query == ", "" + sql);
        Cursor c = findCursor(sql, args);
        return c;
    }

    // CONTENT VALUES
    private ContentValues createContentValues(HashMap<String, String> values) {

        ContentValues vals = new ContentValues();
        String[] keys = values.keySet().toArray(new String[] {});
        for (String key : keys)
            vals.put(key, values.get(key));

        return vals;
    }

    // CLASS TABLE
    public static class Table {

        // CLASS NAME
        public static class Name
        {

            public final static String Answere		               = "Answere";
            public final static String PartyDetail		               = "PartyDetail";
            public final static String ConstituencyList		               = "ConstituencyList";
            public final static String CountingRoundList		               = "CountingRoundList";
            public final static String RegularNotificationList		               = "RegularNotificationList";
            public final static String   imageUploaded 		               = "imageUploaded";
            public final static String   user_detail 		               = "user_detail";
            public final static String   RoundValidation 		               = "RoundValidation";
        }
        // ------------------------Answere------------------------
        public enum imageUploaded {
            status,photo_id,path,round_id, user_id
        }
        // ------------------------Answere------------------------
        public enum Answere {
            userID,candidateId,categoryid,partyId,stateId,CstationId,ConsId,votes,time,extra,imageStatus,path,round_id
        }

        // ------------------------PartyDetail------------------------
        public enum PartyDetail {
            CandidateID,CandidateName,PartyID,PartyCode,PartyName,CategoryID,checked,ConstituencyID,CountingStationID

        }

        // ------------------------ConstituencyList------------------------
        public enum ConstituencyList {
            ID,ConstituencyCode,ConstituencyName,UserID

        }

        // ------------------------CountingRoundList------------------------
        public enum CountingRoundList {
            ID,CountingRoundCode,CountingRoundName,UserID,status

        }

        // ------------------------RegularNotificationList------------------------
        public enum RegularNotificationList {
            ID,RegularNotificationCode,RegularNotificationName,UserID

        }

        // ------------------------User Detail------------------------
        public enum user_detail {
            id,name,email,username,mobile,stateid,cStationId,phone,constituency_status

        }


        // ------------------------User Detail------------------------
        public enum RoundValidation {
            roundID,ConstituencyID,LeadingID,roundType,status

        }

    }
    // =================================
    public interface DBListener {

        /** Executed when data is fetched via rawQuery */
        public void onDataFetchedSucessfully(int queryId, Cursor c);
    }

    // ============ com.propliance.megainfomatix.Common.DB HELPER CLASS =====================

    class DBHelper extends SQLiteOpenHelper {

        //
        public DBHelper(Context context, String name, CursorFactory factory, int version) {

            super(context, name, factory, version);
        }

        //
        @Override
        public void onCreate(SQLiteDatabase db) {

            // createTables(db);
        }


        //
        @SuppressWarnings("unused")
        private void insertAnyInitialRecords(SQLiteDatabase db) {

        }

        //
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {

                // String str =
                // "create table comment_master(comment_id INTEGER NOT NULL, comment_text TEXT, comment_type INTEGER, status INTEGER, feed_id INTEGER, add_date TEXT, user_id TEXT);";
                // db.execSQL(str);

            }
        }
    }

    public static void copyDBFromAssets(Context _context, File df) throws IOException {

        // Open your local db as the input stream
        InputStream myInput = _context.getAssets().open(_DB_NAME);

        // Path to the just created empty db
        // String outFileName = _DB_PATH + _DB_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(df);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[10240];
        int length;

        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
}