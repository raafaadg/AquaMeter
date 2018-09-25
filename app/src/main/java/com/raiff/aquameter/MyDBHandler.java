package com.raiff.aquameter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MyDBHandler extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dadosDB.db";
    public final String TABLE_NAME = "Dados";
    public final String COLUMN_ID = "DadosID";
    public final String COLUMN_TIME = "DadosTime";
    public final String COLUMN_GPS = "DadosGPS";
    public final String COLUMN_DATA = "DadosCont";
    //initialize the database
    public MyDBHandler(Context context, String nome, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        //forceCreate();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                COLUMN_TIME + " TEXT," +
                COLUMN_GPS + " TEXT," +
                COLUMN_DATA + " TEXT" + ")";
         db.execSQL(CREATE_TABLE);
        Log.v("logSQL","CRIANDO A PORRA DO BD");
    }
    public Cursor getuser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ",
                null);
        return res;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}


    public void forceCreate(){
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                COLUMN_TIME + " TEXT," +
                COLUMN_GPS + " TEXT," +
                COLUMN_DATA + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
        //createEmpty();
    }

    public void createEmpty(){
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_NAME;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        Date date = Calendar.getInstance().getTime();
        if(icount<=0)
            addHandler(new Dados(
                    date.toString(),
                    "0",
                    "0"
            ));
    }


    public ArrayList<String> loadHandler() {
        String result = "";
        ArrayList<String> results = new ArrayList<String>();

        String query = "Select * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int result_0 = cursor.getInt(0);
            String result_1 = cursor.getString(1);
            String result_2 = cursor.getString(2);
            String result_3 = cursor.getString(3);

            result += String.valueOf(result_0) + ";" +
                    result_1 + ";" +
                    result_2 + ";" +
                    result_3 + ";";
            results.add(result);
            result = "";
        }
        cursor.close();
        db.close();
        return results;
    }

    public void addHandler(Dados dados) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, dados.getDadosTime());
        values.put(COLUMN_GPS, dados.getDadosGPS());
        values.put(COLUMN_DATA, dados.getDadosCont());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    

    public Dados findHandler(String dadosID) {
        String query = "Select * FROM " + TABLE_NAME + "WHERE" + COLUMN_TIME + " = " + "'" + dadosID + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Dados dados = new Dados();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            dados.setDadosTime(cursor.getString(1));
            dados.setDadosGPS(cursor.getString(2));
            dados.setDadosCont(cursor.getString(3));
            cursor.close();
        } else {
            dados = null;
        }
        db.close();
        return dados;
    }

    public boolean deleteHandler(int ID) {

        boolean result = false;
        String query = "Select*FROM" + TABLE_NAME + "WHERE" + COLUMN_ID + "= '" + String.valueOf(ID) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Dados dados = new Dados();
        if (cursor.moveToFirst()) {
            //dados.setDadosID(Integer.parseInt(cursor.getString(0)));

            db.delete(TABLE_NAME, COLUMN_ID + "=?",
                    new String[] {
                    String.valueOf(ID)
            });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        forceCreate();
    }

    public String checkTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_NAME + "'", null);
        return String.valueOf(cursor.getCount());
    }

    public boolean updateHandler(Dados dados) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(COLUMN_ID, dados.getDadosID());
        values.put(COLUMN_TIME, dados.getDadosTime());
        values.put(COLUMN_GPS, dados.getDadosGPS());
        values.put(COLUMN_DATA, dados.getDadosCont());
        return db.update(TABLE_NAME, values, COLUMN_ID + "=" + 1, null) > 0;
    }
}