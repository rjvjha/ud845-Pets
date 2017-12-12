package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Rahul on 10-12-2017.
 */

public class PetProvider extends ContentProvider {
    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private PetDbHelper mDbHelper;
    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PETS_PATH, PETS);
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PETS_PATH + "/#",PET_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // get readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Figure out if the URI matcher can match the URI to a specific code

        Cursor cursor ;
        int match = sUriMatcher.match(uri);

        switch(match){
            case PETS:
                cursor = db.query(PetsContract.PetsEntry.TABLE_NAME,projection,
                        selection,selectionArgs,null,null,sortOrder);
                break;
            case PET_ID:
                selection = PetsContract.PetsEntry._ID +"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetsContract.PetsEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown uri " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        // Check if name is null
        String name = values.getAsString(PetsContract.PetsEntry.COLUMN_PET_NAME);
        if(name == null){
            throw new IllegalArgumentException("Pet requires a name");
        }
        // Check for gender
        Integer gender = values.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_GENDER);
        if(gender == null ||!PetsContract.PetsEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Pet requires a valid gender");
        }
        // Check for weight
        Integer weight = values.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);
        if(weight!=null && weight < 0){
            throw new IllegalArgumentException("Pet requires a valid weight");
        }

        SQLiteDatabase db  = mDbHelper.getWritableDatabase();
        long id = db.insert(PetsContract.PetsEntry.TABLE_NAME, null ,values);
        if(id ==-1){
            Log.e(LOG_TAG,"Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }



    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return updatePet(uri, contentValues, selection,selectionArgs);
            case PET_ID:
                selection = PetsContract.PetsEntry._ID +"=?";
                selectionArgs= new String []{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("update is not supported here " + uri);

        }
    }
    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if(values.containsKey(PetsContract.PetsEntry.COLUMN_PET_NAME)){
            String name = values.getAsString(PetsContract.PetsEntry.COLUMN_PET_NAME);
            if(name == null){
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if(values.containsKey(PetsContract.PetsEntry.COLUMN_PET_GENDER)){
            Integer gender = values.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_GENDER);
            if(gender == null ||!PetsContract.PetsEntry.isValidGender(gender)){
                throw new IllegalArgumentException("Pet requires a valid gender");
            }
        }
        if(values.containsKey(PetsContract.PetsEntry.COLUMN_PET_WEIGHT)){
            Integer weight = values.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);
            if(weight!=null && weight < 0){
                throw new IllegalArgumentException("Pet requires a valid weight");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = db.update(PetsContract.PetsEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        // return no of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                rowsDeleted = db.delete(PetsContract.PetsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PET_ID:
                selection = PetsContract.PetsEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(PetsContract.PetsEntry.TABLE_NAME, selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete id not supported here" +uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has change
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return PetsContract.PetsEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetsContract.PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
