package com.example.android.fifaapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {
    private Contract(){}
    public static final String CONTENT_AUTHORITY ="com.example.android.fifaapp";
    /**
          * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
          * the content provider.
          */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     +     * Possible path (appended to base content URI for possible URI's)
     +     * For instance, content://com.example.android.pets/pets/ is a valid path for
     +     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     +     * as the ContentProvider hasn't been given any information on what to do with "staff".
     +     */

    public static final String PATH_FIFA = "fifa";
    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class MatchEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FIFA);
        /** Name of database table for pets */
        public final static String TABLE_NAME = "fifa";
        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;
        /**
         * Name of the teamA & teamB
         *
         * Type: TEXT
         */
        public final static String COLUMN_TEAM__A_NAME ="team_A";//name
        public final static String COLUMN_TEAM__B_NAME ="team_B";//breed
        public final static String COLUMN_MATCH_DATE ="date";
        public final static String COLUMN_MATCH_TIME ="time";
        public final static String COLUMN_MATCH_VENUE ="venue";


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FIFA;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FIFA;



        }
}
