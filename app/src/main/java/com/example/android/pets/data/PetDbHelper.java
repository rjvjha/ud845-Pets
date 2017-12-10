package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rahul on 30-10-2017.
 */

public class PetDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = PetDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME =  "shelter.db"  ;
    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static int DATABASE_VERSION = 1;

    // SQL statement to create table
    private static final String SQL_CREATE_PETS_TABLE = "CREATE TABLE " +
            PetsContract.PetsEntry.TABLE_NAME + " ("
            + PetsContract.PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PetsContract.PetsEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
            + PetsContract.PetsEntry.COLUMN_PET_BREED + " TEXT, "
            + PetsContract.PetsEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
            + PetsContract.PetsEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
    // SQL statement to drop table
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + PetsContract.PetsEntry.TABLE_NAME + ";";



    public PetDbHelper(Context context){
       super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
