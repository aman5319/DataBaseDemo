package com.example.amidezcod.databasedemo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by amidezcod on 9/7/17.
 */

public class PetHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pets_home.db";
    private static final int DATABASE_VERSION = 1;

    public PetHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v("AMAN", "create");
        String CREATE_PETS_TABLE = "CREATE TABLE " + PetContract.PetEntry.TABLE_NAME + " ( "
                + PetContract.PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetContract.PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + PetContract.PetEntry.COLUMN_PET_BREED + " TEXT, "
                + PetContract.PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + PetContract.PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

        sqLiteDatabase.execSQL(CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE " + PetContract.PetEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}