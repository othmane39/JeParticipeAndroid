package com.misrotostudio.jeparticipeapp.helper;

/**
 * Created by othmaneelmassari on 09/07/15.
 */
import java.lang.reflect.Array;
import java.sql.SQLInput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.misrotostudio.jeparticipeapp.app.AppConfig;

import org.apache.http.auth.NTUserPrincipal;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_LOGIN = "login";

    // Event table name
    private static final String TABLE_EVENT = "event";

    // Participe table name
    private static final String TABLE_PARTICIPE = "participe";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    //Event Table Columns names
    private static final String KEY_TYPE = "type";
    private static final String KEY_DATE_D = "date_debut";
    private static final String KEY_HEURE_D = "heure_debut";
    private static final String KEY_DATE_F = "date_fin";
    private static final String KEY_HEURE_F = "heure_fin";
    private static final String KEY_LIEU = "lieu";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";// + " DEFAULT CHARSET=utf8";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " VARCHAR(255) NOT NULL,"
                + KEY_TYPE + " VARCHAR(255) NOT NULL, " + KEY_DATE_D + " VARCHAR(255) NOT NULL,"
                + KEY_HEURE_D + " VARCHAR(255) NOT NULL," + KEY_DATE_F + " VARCHAR(255) DEFAULT NULL," + KEY_HEURE_F + " VARCHAR(255) DEFAULT NULL,"
                + KEY_LIEU + " VARCHAR(255) NOT NULL," + KEY_DESCRIPTION + " TEXT NOT NULL," + KEY_IMAGE + " VARCHAR(255)" + ")";// + " DEFAULT CHARSET=utf8";
        db.execSQL(CREATE_EVENT_TABLE);

        String CREATE_PARTICIPE_TABLE = "CREATE TABLE " + TABLE_PARTICIPE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " VARCHAR(255) NOT NULL,"
                + KEY_TYPE + " VARCHAR(255) NOT NULL, " + KEY_DATE_D + " VARCHAR(255) NOT NULL,"
                + KEY_HEURE_D + " VARCHAR(255) NOT NULL," + KEY_DATE_F + " VARCHAR(255) DEFAULT NULL," + KEY_HEURE_F + " VARCHAR(255) DEFAULT NULL,"
                + KEY_LIEU + " VARCHAR(255) NOT NULL," + KEY_DESCRIPTION + " VARCHAR(255) NOT NULL," + KEY_IMAGE + " VARCHAR(255)" + ")";// + " DEFAULT CHARSET=utf8";
        db.execSQL(CREATE_PARTICIPE_TABLE);

        Log.d(TAG, "Database tables created");


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPE);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // UID
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public void addDataEvent(String name, String type, String date_d, String heure_d,
                         String date_f, String heure_f, String lieu, String description, String image){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TYPE, type);
        values.put(KEY_DATE_D, date_d);
        values.put(KEY_HEURE_D, heure_d);
        values.put(KEY_DATE_F, date_f);
        values.put(KEY_HEURE_F, heure_f);
        values.put(KEY_LIEU, lieu);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_IMAGE, image);

        //Insert Row
        long ir = db.insert(TABLE_EVENT, null, values);
        db.close();

        Log.d(TAG, "New event inserted into sqlite: " + ir);

    }

    public void addDataParticipe(String name, String type, String date_d, String heure_d,
                             String date_f, String heure_f, String lieu, String description, String image){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TYPE, type);
        values.put(KEY_DATE_D, date_d);
        values.put(KEY_HEURE_D, heure_d);
        values.put(KEY_DATE_F, date_f);
        values.put(KEY_HEURE_F, heure_f);
        values.put(KEY_LIEU, lieu);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_IMAGE, image);

        //Insert Row
        long ir = db.insert(TABLE_PARTICIPE, null, values);
        db.close();

        Log.d(TAG, "New participe inserted into sqlite: " + ir);

    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public ArrayList<HashMap<String, ?>> getDataEventDetails(int i) {

        ArrayList<HashMap<String, ?>> eventList = new ArrayList<HashMap<String, ?>>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                HashMap<String, Object> event = new HashMap<String, Object>();
                event.put(KEY_NAME, (String) cursor.getString(1));
                event.put(KEY_TYPE, (String) cursor.getString(2));
                event.put(KEY_DATE_D, (String) cursor.getString(3));
                event.put(KEY_HEURE_D, (String) cursor.getString(4));
                event.put(KEY_DATE_F, (String) cursor.getString(5));
                event.put(KEY_HEURE_F, (String) cursor.getString(6));
                event.put(KEY_LIEU, (String) cursor.getString(7));
                event.put(KEY_DESCRIPTION, cursor.getString(8));
                if (i == 2){
                    Bitmap b = null;
                    /*
                    try {
                       new ImageDownloader(v).execute(AppConfig.URL_IMAGE + cursor.getString(9));
                    }catch(NullPointerException e){
                        e.printStackTrace();
                        Log.e("TAAAAAAAAAAAAA", "Failed to othmane spki");
                    }
                    */
                    try {
                        event.put(KEY_IMAGE, (Bitmap) b);
                        event.put("image_url", (String) cursor.getString(9));
                    }catch (NullPointerException e){
                        e.printStackTrace();
                        Log.e("MAAAAAAAAAAAA", "Failed to put othmane");
                    }
                }
                else{
                    event.put(KEY_IMAGE, (String) cursor.getString(9));
                }
                eventList.add(event);
                //Log.d("Event Element", event.toString());
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        Log.d(TAG, "Fetching event from sqlite: " + eventList.toString());

        return eventList;

    }

    public ArrayList<HashMap<String, ?>> getDataParticipeDetails(int i) {

        ArrayList<HashMap<String, ?>> eventList = new ArrayList<HashMap<String, ?>>();
        String selectQuery = "SELECT * FROM " + TABLE_PARTICIPE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
            HashMap<String, Object> event = new HashMap<String, Object>();
            event.put(KEY_NAME, cursor.getString(1));
            event.put(KEY_TYPE, cursor.getString(2));
            event.put(KEY_DATE_D, cursor.getString(3));
            event.put(KEY_HEURE_D, cursor.getString(4));
            event.put(KEY_DATE_F, cursor.getString(5));
            event.put(KEY_HEURE_F, cursor.getString(6));
            event.put(KEY_LIEU, cursor.getString(7));
            event.put(KEY_DESCRIPTION, cursor.getString(8));
                if (i == 2){
                    Bitmap b = null;
                    /*
                    try {
                       new ImageDownloader(v).execute(AppConfig.URL_IMAGE + cursor.getString(9));
                    }catch(NullPointerException e){
                        e.printStackTrace();
                        Log.e("TAAAAAAAAAAAAA", "Failed to othmane spki");
                    }
                    */
                    try {
                        event.put(KEY_IMAGE, (Bitmap) b);
                        event.put("image_url", (String) cursor.getString(9));
                    }catch (NullPointerException e){
                        e.printStackTrace();
                        Log.e("MAAAAAAAAAAAA", "Failed to put othmane");
                    }
                }
                else{
                    event.put(KEY_IMAGE, (String) cursor.getString(9));
                }
            eventList.add(event);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        Log.d(TAG, "Fetching participe event from sqlite: " + eventList.toString());

        return eventList;

    }

    /**
     * Getting user login status return true if rows are there in table
     */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    public int getEventRowCount(){
        String countQuery = "SELECT  * FROM" + TABLE_EVENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        return rowCount;
    }

    public int getParticipeRowCount(){
        String countQuery = "SELECT  * FROM" + TABLE_PARTICIPE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        return rowCount;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
    public void deleteDataEvent() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_EVENT, null, null);
        db.close();

        Log.d(TAG, "Deleted all event info from sqlite");
    }

    public void deleteDataEvent(String name, String dateD){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_EVENT, KEY_NAME + " = '" + name + "' AND " + KEY_DATE_D + "= '" + dateD + "'", null);
        db.close();

        Log.d(TAG, "Event " + name + " deleted from sqlite event table");
    }

    public void deleteDataParticipe() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PARTICIPE, null, null);
        db.close();

        Log.d(TAG, "Deleted all event info from sqlite");
    }

    public void deleteDataParticipe(String name, String dateD){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PARTICIPE, KEY_NAME + "= '" + name + "' AND " + KEY_DATE_D + "= '" + dateD + "'", null);
        db.close();

        Log.d(TAG, "Event " + name + " deleted from sqlite participe table");
    }

}