package com.example.android.fifaapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class FixtureProvider extends ContentProvider {
    /**
     * URI matcher code for the content URI for the pets table
     */
    private static final int MATCH = 100;

    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int MATCH_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI("com.example.android.fifaapp", "fifa", MATCH);
        sUriMatcher.addURI("com.example.android.fifaapp", "fifa/#", MATCH_ID);
    }

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = FixtureProvider.class.getSimpleName();
    private FifaDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new FifaDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCH:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // TODO: Perform database query on pets table
                cursor = database.query(Contract.MatchEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MATCH_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = Contract.MatchEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(Contract.MatchEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCH:
                return Contract.MatchEntry.CONTENT_LIST_TYPE;
            case MATCH_ID:
                return Contract.MatchEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCH:
                return insertMatch(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertMatch(Uri uri,ContentValues values){
        String name_A = values.getAsString(Contract.MatchEntry.COLUMN_TEAM__A_NAME);
        String name_B = values.getAsString(Contract.MatchEntry.COLUMN_TEAM__B_NAME);

        if (name_A == null || name_B == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert the new pet with the given values
        long id = database.insert(Contract.MatchEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Track the number of rows that were deleted
        int rowsDeleted;
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case MATCH:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(Contract.MatchEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MATCH_ID:
                // Delete a single row given by the ID in the URI
                selection = Contract.MatchEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(Contract.MatchEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // Delete all rows that match the selection and selection arg

// If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);

            // Return the number of rows deleted
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCH:
                return updateMatch(uri, contentValues, selection, selectionArgs);
            case MATCH_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = Contract.MatchEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMatch(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    private int updateMatch(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if (values.containsKey(Contract.MatchEntry.COLUMN_TEAM__A_NAME)) {
            String name = values.getAsString(Contract.MatchEntry.COLUMN_TEAM__A_NAME);
            if (name == null) {
                throw new IllegalArgumentException("the team requires a name");
            }
        }
        if (values.containsKey(Contract.MatchEntry.COLUMN_TEAM__B_NAME)) {
            String name = values.getAsString(Contract.MatchEntry.COLUMN_TEAM__B_NAME);
            if (name == null) {
                throw new IllegalArgumentException("The team requires a name");
            }
        }
        if (values.containsKey(Contract.MatchEntry.COLUMN_MATCH_DATE)){
            int date = values.getAsInteger(Contract.MatchEntry.COLUMN_MATCH_DATE);
            if (date>31 || date==0){
                throw new IllegalArgumentException("enter a proper date");
            }
        }
        if (values.containsKey(Contract.MatchEntry.COLUMN_MATCH_TIME)){
            Integer time = values.getAsInteger(Contract.MatchEntry.COLUMN_MATCH_TIME);
            if (time == null){
                throw new IllegalArgumentException("enter a proper time");
            }
        }
        if (values.containsKey(Contract.MatchEntry.COLUMN_MATCH_VENUE)) {
            String venue = values.getAsString(Contract.MatchEntry.COLUMN_MATCH_VENUE);
            if (venue == null) {
                throw new IllegalArgumentException("venue must be typed");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        //doubt here
        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(Contract.MatchEntry.TABLE_NAME, values, selection, selectionArgs);

        // Perform the update on the database and get the number of rows affected

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;

        // Returns the number of database rows affected by the update statement

        //doubt here
    }
}
