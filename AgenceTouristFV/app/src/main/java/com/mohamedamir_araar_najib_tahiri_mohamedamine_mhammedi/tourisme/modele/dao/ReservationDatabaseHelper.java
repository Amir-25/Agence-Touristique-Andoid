package com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.modele.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReservationDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "reservations.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "reservations";
    public static final String COL_ID = "id";
    public static final String COL_VOYAGE_ID = "voyage_id";
    public static final String COL_DATE = "date";
    public static final String COL_NB_PLACES = "nb_places";
    public static final String COL_TOTAL = "total";
    public static final String COL_EMAIL = "email_client";

    public ReservationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_VOYAGE_ID + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_NB_PLACES + " INTEGER, " +
                COL_TOTAL + " INTEGER, " +
                COL_EMAIL + " TEXT," +
                "statut TEXT DEFAULT 'Confirmée')";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
