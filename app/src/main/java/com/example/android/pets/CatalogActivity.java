/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.pets.data.PetsContract;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 0;
    private PetCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView listView = (ListView) findViewById(R.id.list_view_pet);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        cursorAdapter = new PetCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);
        // Kick off the loader
        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
        // OnClickItem Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Uri with appwnded id
                Uri petUri = ContentUris.withAppendedId(PetsContract.PetsEntry.CONTENT_URI, id);
                Intent editPetIntent = new Intent(CatalogActivity.this, EditorActivity.class);
                editPetIntent.setData(petUri);
                startActivity(editPetIntent);

            }
        });

    }

    private void insertData(){
        ContentValues values = new ContentValues();
        values.put(PetsContract.PetsEntry.COLUMN_PET_NAME,"Toto");
        values.put(PetsContract.PetsEntry.COLUMN_PET_BREED,"Terrier");
        values.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, PetsContract.PetsEntry.GENDER_MALE);
        values.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT,10);
        Uri newUriId = getContentResolver().insert(PetsContract.PetsEntry.CONTENT_URI, values);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertData();
                //displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                int rows = getContentResolver().delete(PetsContract.PetsEntry.CONTENT_URI, null, null);
                Toast.makeText(this,"No of rows deleted: "+ rows,Toast.LENGTH_SHORT).show();
                //displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Loader Callback methods are implemented here:

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = {
                PetsContract.PetsEntry._ID,
                PetsContract.PetsEntry.COLUMN_PET_NAME,
                PetsContract.PetsEntry.COLUMN_PET_BREED};
        return new CursorLoader(this,
                PetsContract.PetsEntry.CONTENT_URI,
                projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        cursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        cursorAdapter.swapCursor(null);

    }
}
