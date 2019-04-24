package com.example.android.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Sunshine_pocasie4.db";

    public DBHelper(Context context) { // konštruktor databázy
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // vytvorí tabuľku počasie

        String SQL_CREATE_TABLE_POCASIE = "CREATE TABLE " + MyContrast.Pocasie.TABLE_NAME + "("
                + MyContrast.Pocasie.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + MyContrast.Pocasie.COLUMN_DATUM + " TEXT, "
                + MyContrast.Pocasie.COLUMN_PREDPOVED + " TEXT, "
                + MyContrast.Pocasie.COLUMN_STUPEN + " TEXT, "
                + MyContrast.Pocasie.COLUMN_ID_MESTO + " INTEGER )";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_POCASIE);

        // vytvorí tabuľku mesto

        String SQL_CREATE_TABLE_MESTO = "CREATE TABLE " + MyContrast.Mesto.TABLE_NAME + "("
                + MyContrast.Mesto.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + MyContrast.Mesto.COLUMN_NAZOV + " TEXT )";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_MESTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyContrast.Pocasie.TABLE_NAME);
        onCreate(sqLiteDatabase);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyContrast.Mesto.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    // pridá zadané počasie do databazy

    public void addPocasie(Pocasie p) {
        ContentValues values = new ContentValues();
        values.put(MyContrast.Pocasie.COLUMN_DATUM, p.getDatum());
        values.put(MyContrast.Pocasie.COLUMN_PREDPOVED, p.getPredpoved());
        values.put(MyContrast.Pocasie.COLUMN_STUPEN, p.getStupen());
        values.put(MyContrast.Pocasie.COLUMN_ID_MESTO, p.getID_mesto());

        SQLiteDatabase db = getWritableDatabase();
        long newRowId = db.insert(
                MyContrast.Pocasie.TABLE_NAME,
                null,
                values);
        db.close();
    }

    // pridá zadané mesto do databázy

    public void addMesto(Mesto m) {
        ContentValues values = new ContentValues();
        values.put(MyContrast.Mesto.COLUMN_NAZOV, m.getNazov());

        SQLiteDatabase db = getWritableDatabase();
        long newRowId = db.insert(
                MyContrast.Mesto.TABLE_NAME,
                null,
                values);
        db.close();
    }

    // vráti počasie podľa zadaného dátumu

    public Pocasie getPocasie(String datum, String id_mesto) {
        SQLiteDatabase db = getWritableDatabase();

        String[] projection = {MyContrast.Pocasie.COLUMN_ID, MyContrast.Pocasie.COLUMN_DATUM, MyContrast.Pocasie.COLUMN_PREDPOVED, MyContrast.Pocasie.COLUMN_STUPEN, MyContrast.Pocasie.COLUMN_ID_MESTO};
        String selection = MyContrast.Pocasie.COLUMN_DATUM + "=? AND " + MyContrast.Pocasie.COLUMN_ID_MESTO + "=? ";
        String[] selectionArgs = {datum, id_mesto};

        Cursor c = db.query(
                MyContrast.Pocasie.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (c != null && c.moveToFirst()) {
            c.moveToFirst();

            Pocasie s = new Pocasie(
                    c.getInt(c.getColumnIndex(MyContrast.Pocasie.COLUMN_ID)),
                    datum,
                    c.getString(c.getColumnIndex(MyContrast.Pocasie.COLUMN_PREDPOVED)),
                    c.getString(c.getColumnIndex(MyContrast.Pocasie.COLUMN_STUPEN)),
                    c.getInt(c.getColumnIndex(MyContrast.Pocasie.COLUMN_ID_MESTO)));
            c.close();
            db.close();
            return s;
        } else {
            return null;
        }
    }

    // vrati Pocasie pomocou ID

    public Pocasie getPocasie(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = {MyContrast.Pocasie.COLUMN_ID, MyContrast.Pocasie.COLUMN_DATUM, MyContrast.Pocasie.COLUMN_PREDPOVED, MyContrast.Pocasie.COLUMN_STUPEN, MyContrast.Pocasie.COLUMN_ID_MESTO};
        String selection = MyContrast.Pocasie.COLUMN_ID + "=? ";
        String[] selectionArgs = {"" + id};

        Cursor c = db.query(
                MyContrast.Pocasie.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (c != null && c.moveToFirst()) {
            c.moveToFirst();

            Pocasie s = new Pocasie(
                    id,
                    c.getString(c.getColumnIndex(MyContrast.Pocasie.COLUMN_DATUM)),
                    c.getString(c.getColumnIndex(MyContrast.Pocasie.COLUMN_PREDPOVED)),
                    c.getString(c.getColumnIndex(MyContrast.Pocasie.COLUMN_STUPEN)),
                    c.getInt(c.getColumnIndex(MyContrast.Pocasie.COLUMN_ID_MESTO)));
            c.close();
            db.close();
            return s;
        } else {
            return null;
        }
    }

    // vráti názov mesta podľa zadaného ID

    public Mesto getMesto(int ID) {
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = {MyContrast.Mesto.COLUMN_NAZOV};
        String selection = MyContrast.Mesto.COLUMN_ID + "=?";
        String[] selectionArgs = {"" + ID};

        Cursor c = db.query(
                MyContrast.Mesto.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        c.moveToFirst();

        Mesto m = new Mesto(ID,
                c.getString(c.getColumnIndex(MyContrast.Mesto.COLUMN_NAZOV)));
        c.close();
        db.close();
        return m;
    }

    // vráti všetko z počasia

    public List<HashMap<String, String>> getPocasie() {
        SQLiteDatabase db = getWritableDatabase();
        List<HashMap<String, String>> zoznam = new ArrayList<HashMap<String, String>>();
        Cursor c = db.rawQuery("select * from " + MyContrast.Pocasie.TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("_id", c.getString(c.getColumnIndex(MyContrast.Pocasie.COLUMN_ID)));
                map.put("datum", c.getString(c.getColumnIndex(MyContrast.Pocasie.COLUMN_DATUM)));
                map.put("predpoved", c.getString(c.getColumnIndex(MyContrast.Pocasie.COLUMN_PREDPOVED)));
                map.put("stupen", c.getString(c.getColumnIndex(MyContrast.Pocasie.COLUMN_STUPEN)));
                map.put("_idMesto", c.getString(c.getColumnIndex(MyContrast.Pocasie.COLUMN_ID_MESTO)));
                zoznam.add(map);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return zoznam;
    }

    // vráti všetko z mesta

    public List<HashMap<String, String>> getMesto() {
        SQLiteDatabase db = getWritableDatabase();
        List<HashMap<String, String>> zoznam = new ArrayList<HashMap<String, String>>();
        Cursor c = db.rawQuery("select * from " + MyContrast.Mesto.TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("_id", c.getString(c.getColumnIndex(MyContrast.Mesto.COLUMN_ID)));
                map.put("nazov", c.getString(c.getColumnIndex(MyContrast.Mesto.COLUMN_NAZOV)));
                zoznam.add(map);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return zoznam;
    }

    // vymaže počasie podľa zadaného ID

    public void deletePocasie(int ID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                MyContrast.Pocasie.TABLE_NAME,
                MyContrast.Pocasie.COLUMN_ID + "= ?",
                new String[]{"" + ID});
        db.close();
    }

    // vymaže počasie podľa zadaného ID Mesta

    public void deletePocasiePomocouMesta(int IDMesto) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                MyContrast.Pocasie.TABLE_NAME,
                MyContrast.Pocasie.COLUMN_ID_MESTO + "= ?",
                new String[]{"" + IDMesto});
        db.close();
    }
    // vymaže mesto podľa zadaného ID

    public void deleteMesto(int ID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                MyContrast.Mesto.TABLE_NAME,
                MyContrast.Mesto.COLUMN_ID + "= ?",
                new String[]{"" + ID});
        db.close();
    }

    // updatne mesto

    public void updateMesto(Mesto m) {
        ContentValues values = new ContentValues();
        values.put(MyContrast.Mesto.COLUMN_NAZOV, m.getNazov());

        SQLiteDatabase db = getWritableDatabase();
        db.update(
                MyContrast.Mesto.TABLE_NAME,
                values,
                MyContrast.Mesto.COLUMN_ID + "= ?",
                new String[]{"" + m.getID()});
        db.close();
    }

    // updatne počasie

    public void updatePocasie(Pocasie p) {
        ContentValues values = new ContentValues();
        values.put(MyContrast.Pocasie.COLUMN_DATUM, p.getDatum());
        values.put(MyContrast.Pocasie.COLUMN_PREDPOVED, p.getPredpoved());
        values.put(MyContrast.Pocasie.COLUMN_STUPEN, p.getStupen());
        values.put(MyContrast.Pocasie.COLUMN_ID_MESTO, p.getID_mesto());

        SQLiteDatabase db = getWritableDatabase();
        db.update(
                MyContrast.Pocasie.TABLE_NAME,
                values,
                MyContrast.Pocasie.COLUMN_ID + "= ?",
                new String[]{"" + p.getID()});
        db.close();
    }

    // kurzor pre počasie

    public Cursor VratKurzor(int idMesto) {
        SQLiteDatabase db = getWritableDatabase();
        String sstr = "select * from " + MyContrast.Pocasie.TABLE_NAME + " where " + MyContrast.Pocasie.COLUMN_ID_MESTO + " == " + idMesto + " order by " + MyContrast.Pocasie.COLUMN_DATUM;
        Cursor c = db.rawQuery(sstr, null);
        c.moveToFirst();
        db.close();
        return c;
    }

    public Cursor VratKurzor2() {
        SQLiteDatabase db = getWritableDatabase();
        String sstr = "select * from " + MyContrast.Mesto.TABLE_NAME;
        Cursor c = db.rawQuery(sstr, null);
        c.moveToFirst();
        db.close();
        return c;
    }
}
