package com.example.android.fifaapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.fifaapp.data.Contract;

public class FifaCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link FifaCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public FifaCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }
    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
// Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        // Find the columns of pet attributes that we're interested in
        int name_A_index = cursor.getColumnIndex(Contract.MatchEntry.COLUMN_TEAM__A_NAME);
        int name_B_index = cursor.getColumnIndex(Contract.MatchEntry.COLUMN_TEAM__B_NAME);
        int date_index = cursor.getColumnIndex(Contract.MatchEntry.COLUMN_MATCH_DATE);
        int time_index = cursor.getColumnIndex(Contract.MatchEntry.COLUMN_MATCH_TIME);

        // Read the pet attributes from the Cursor for the current pet
        String name_A = cursor.getString(name_A_index);
        String name_b = cursor.getString(name_B_index);
        String date = cursor.getString(date_index);
        String time = cursor.getString(time_index);

        nameTextView.setText(name_A + " vs " + name_b);
        summaryTextView.setText(date + ", " + time);
    }
}
