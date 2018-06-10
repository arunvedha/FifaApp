package com.example.android.fifaapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.fifaapp.data.Contract;
import com.example.android.fifaapp.data.Contract.MatchEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int MATCH_LOADER=0;
    FifaCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setup FAB to open EditorActivity
        Button button=(Button)findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });
        // Find the ListView which will be populated with the pet data
        ListView MatchListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        MatchListView.setEmptyView(emptyView);
        mCursorAdapter=new FifaCursorAdapter(this,null);
        MatchListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(MATCH_LOADER,null,this);

        MatchListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(Contract.MatchEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);
                startActivity(intent);

            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection of columns we care abt
        String[] projection={MatchEntry._ID,MatchEntry.COLUMN_TEAM__A_NAME,MatchEntry.COLUMN_TEAM__B_NAME,MatchEntry.COLUMN_MATCH_DATE,
        MatchEntry.COLUMN_MATCH_TIME};

        return new CursorLoader(this,MatchEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
