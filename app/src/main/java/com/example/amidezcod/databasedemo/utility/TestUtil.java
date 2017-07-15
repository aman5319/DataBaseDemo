package com.example.amidezcod.databasedemo.utility;

import android.content.ContentValues;
import android.content.Context;

import com.example.amidezcod.databasedemo.data.PetContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amidezcod on 16/7/17.
 */

public class TestUtil {
    public static void insertBulkFakeData(Context context) {
        List<ContentValues> contentValues = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ContentValues values = new ContentValues();
            values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto "+ i);
            values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier "+ i);
            values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
            values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7);
            contentValues.add(values);
        }
        ContentValues a[] = new ContentValues[contentValues.size()];
        context.getContentResolver().bulkInsert(PetContract.PetEntry.CONTENT_URI, contentValues.toArray(a));
    }

    public static void insertSingleDummyData(Context context) {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7);
        context.getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);
    }
}
