package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetsContract;

import org.w3c.dom.Text;

/**
 * Created by Rajeev on 12-12-2017.
 */

class PetCursorAdapter extends CursorAdapter{

    PetCursorAdapter(Context context, Cursor c){
        super(context, c, 0);

    }
    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent,false);
    }
    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        // Extract values from cursor
        String name = cursor.getString(cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_NAME));
        String breed = cursor.getString(cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_BREED));
        // Populate fields with the extracted values
        nameTextView.setText(name);
        summaryTextView.setText(breed);
    }
}
