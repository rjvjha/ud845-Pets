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
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.PetsContract;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    PetDbHelper mDbHelper;

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
        mDbHelper = new PetDbHelper(this);
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String [] projection = {PetsContract.PetsEntry._ID, PetsContract.PetsEntry.COLUMN_PET_NAME, PetsContract.PetsEntry.COLUMN_PET_BREED,
                PetsContract.PetsEntry.COLUMN_PET_GENDER};
        String selection = PetsContract.PetsEntry.COLUMN_PET_GENDER + "=?";
        String [] selectionArgs = {String.valueOf(PetsContract.PetsEntry.GENDER_MALE)};

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.query(
                PetsContract.PetsEntry.TABLE_NAME,
                projection,null,null,
                null, null, null);
        TextView displayView = (TextView) findViewById(R.id.text_view_pet);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
            displayView.append(PetsContract.PetsEntry._ID + " - " +
                    PetsContract.PetsEntry.COLUMN_PET_NAME + " - " + PetsContract.PetsEntry.COLUMN_PET_GENDER +
                    " - " + PetsContract.PetsEntry.COLUMN_PET_BREED + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_NAME);
            int genderColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_GENDER);
            int breedColumnIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_BREED);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentGender = cursor.getInt(genderColumnIndex);
                String currentBreed = cursor.getString(breedColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " + currentGender + " - " + currentBreed));
            }
        }
         finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private void insertData(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PetsContract.PetsEntry.COLUMN_PET_NAME,"Toto");
        values.put(PetsContract.PetsEntry.COLUMN_PET_BREED,"Terrier");
        values.put(PetsContract.PetsEntry.COLUMN_PET_GENDER,1);
        values.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT, PetsContract.PetsEntry.GENDER_MALE);
        long RowId = db.insert(PetsContract.PetsEntry.TABLE_NAME, null, values);
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
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }
}
