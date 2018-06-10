package com.example.android.fifaapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.fifaapp.data.Contract.MatchEntry;

public class FifaDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = FifaDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "shelter.db";
    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link FifaDbHelper}.
     *
     * @param context of the app
     */
    public FifaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_CREATE_FIFA_TABLE = "CREATE TABLE " + MatchEntry.TABLE_NAME + " ("
                +MatchEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +MatchEntry.COLUMN_TEAM__A_NAME + " TEXT NOT NULL, "
                +MatchEntry.COLUMN_TEAM__B_NAME + " TEXT NOT NULL, "
                +MatchEntry.COLUMN_MATCH_DATE + " TEXT NOT NULL, "
                +MatchEntry.COLUMN_MATCH_TIME + " TEXT NOT NULL, "
                +MatchEntry.COLUMN_MATCH_VENUE + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_FIFA_TABLE);
    }
}
