package com.example.android.fifaapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.fifaapp.data.Contract;
import com.example.android.fifaapp.data.Contract.MatchEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>  {
    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_MATCH_LOADER = 0;
    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentMatchUri;
    private EditText mName_A_EditText;
    private EditText mNAME_B_EditText;
    private EditText mDateEditText;
    private EditText mVenueEditText;
    private EditText mTimeEditText;
    /**
     * Boolean flag that keeps track of whether the pet has been edited (true) or not (false)
     */
    private boolean mFixtureHasChanged = false;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mFixtureHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentMatchUri = intent.getData();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentMatchUri == null) {
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.editor_activity_title_new_pet));
            invalidateOptionsMenu();

        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
            setTitle(getString(R.string.editor_activity_title_edit_pet));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_MATCH_LOADER, null, this);
        }

        mName_A_EditText = (EditText)findViewById(R.id.edit_Team_A_name);
        mNAME_B_EditText = (EditText)findViewById(R.id.edit_Team_B_name);
        mDateEditText = (EditText)findViewById(R.id.match_date);
        mTimeEditText =(EditText)findViewById(R.id.match_time);
        mVenueEditText=(EditText)findViewById(R.id.match_venue);

        mName_A_EditText.setOnTouchListener(mTouchListener);
        mNAME_B_EditText.setOnTouchListener(mTouchListener);
        mDateEditText.setOnTouchListener(mTouchListener);
        mTimeEditText.setOnTouchListener(mTouchListener);
        mVenueEditText.setOnTouchListener(mTouchListener);

    }
    private void savematch(){
        String name_A_string = mName_A_EditText.getText().toString().trim();
        String name_B_string = mNAME_B_EditText.getText().toString().trim();
        String dateString = mDateEditText.getText().toString().trim();
        String venueString = mVenueEditText.getText().toString().trim();
        String timeString = mTimeEditText.getText().toString().trim();

        if (mCurrentMatchUri == null &&
                TextUtils.isEmpty(name_A_string) && TextUtils.isEmpty(name_B_string) &&
                TextUtils.isEmpty(dateString) && TextUtils.isEmpty(timeString)&&
                TextUtils.isEmpty(venueString)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }
        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(Contract.MatchEntry.COLUMN_TEAM__A_NAME,name_A_string);
        values.put(Contract.MatchEntry.COLUMN_TEAM__B_NAME,name_B_string);
        values.put(Contract.MatchEntry.COLUMN_MATCH_DATE,dateString);
        values.put(Contract.MatchEntry.COLUMN_MATCH_TIME,timeString);
        values.put(Contract.MatchEntry.COLUMN_MATCH_VENUE,venueString);

        if (mCurrentMatchUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(Contract.MatchEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentMatchUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                savematch();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mFixtureHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mFixtureHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                MatchEntry._ID,
                MatchEntry.COLUMN_TEAM__A_NAME,
                MatchEntry.COLUMN_TEAM__B_NAME,
                MatchEntry.COLUMN_MATCH_DATE,
                MatchEntry.COLUMN_MATCH_VENUE,
                MatchEntry.COLUMN_MATCH_TIME};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentMatchUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
// Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int name_A_ColumnIndex = cursor.getColumnIndex(MatchEntry.COLUMN_TEAM__A_NAME);
            int name_B_ColumnIndex = cursor.getColumnIndex(MatchEntry.COLUMN_TEAM__B_NAME);
            int dateColumnIndex = cursor.getColumnIndex(MatchEntry.COLUMN_MATCH_DATE);
            int venueColumnIndex = cursor.getColumnIndex(MatchEntry.COLUMN_MATCH_VENUE);
            int timeColumnIndex = cursor.getColumnIndex(MatchEntry.COLUMN_MATCH_TIME);

            // Extract out the value from the Cursor for the given column index
            String name_A_string = cursor.getString(name_A_ColumnIndex);
            String name_B_string = cursor.getString(name_B_ColumnIndex);
            String dateString = cursor.getString(dateColumnIndex);
            String venueString = cursor.getString(venueColumnIndex);
            String timeString = cursor.getString(timeColumnIndex);

            // Update the views on the screen with the values from the database
           mName_A_EditText.setText(name_A_string);
           mNAME_B_EditText.setText(name_B_string);
           mDateEditText.setText(dateString);
           mVenueEditText.setText(venueString);
           mTimeEditText.setText(timeString);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mName_A_EditText.setText("");
        mNAME_B_EditText.setText("");
        mDateEditText.setText("");
        mVenueEditText.setText("");
        mTimeEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * +     * This method is called after invalidateOptionsMenu(), so that the
     * +     * menu can be updated (some menu items can be hidden or made visible).
     * +
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentMatchUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        // TODO: Implement this method
        if (mCurrentMatchUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentMatchUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
            finish();

        }
    }
}
