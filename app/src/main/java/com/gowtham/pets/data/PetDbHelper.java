package com.gowtham.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.gowtham.pets.data.PetContract.PetEntry;

public class PetDbHelper extends SQLiteOpenHelper {

    public final static int DATABASE_VERSION = 1;
    public final static String DATABASE_NAME = "shelter.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PetEntry.TABLE_NAME + " (" +
                    PetEntry._ID + " INTEGER PRIMARY KEY NOT NULL," +
                    PetEntry.COLUMN_PET_NAME + " TEXT," + PetEntry.COLUMN_PET_BREED + " TEXT," + PetEntry.COLUMN_PET_AGE + " INTEGER," +
                    PetEntry.COLUMN_PET_ADOPTED + " INTEGER," + PetEntry.COLUMN_PET_GENDER + " INTEGER," + PetEntry.COLUMN_PET_WEIGHT + " INTEGER," +
                    PetEntry.COLUMN_PET_HEIGHT + " INTEGER," + PetEntry.COLUMN_PET_HEALTH_NOTE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
