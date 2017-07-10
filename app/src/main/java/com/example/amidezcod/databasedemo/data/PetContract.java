package com.example.amidezcod.databasedemo.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by amidezcod on 9/7/17.
 */

public final class PetContract {
    public static final String CONTENT_AUTHORITY = "com.example.amidezcod.databasedemo";
    public static final String PATH = "pets";
    private static final Uri BAS_CONTENT = Uri.parse("content://" + CONTENT_AUTHORITY);
    private PetContract() {

    }

    public static final class PetEntry implements BaseColumns {
        public static final String TABLE_NAME = "pets";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";
        //all possible genders
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BAS_CONTENT, PATH);

    }
}
