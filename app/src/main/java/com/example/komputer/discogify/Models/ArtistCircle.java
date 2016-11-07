package com.example.komputer.discogify.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.komputer.discogify.database.ArtistCursorWrapper;
import com.example.komputer.discogify.database.ArtistDatabase;
import com.example.komputer.discogify.database.DatabaseSchema;
import com.example.komputer.discogify.database.DatabaseSchema.ArtistTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Komputer on 02/10/2016.
 */
public class ArtistCircle {
    private static final String TAG = "artist";

    private static ArtistCircle sArtistCircle;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ArtistCircle get(Context context){
        if(sArtistCircle == null){
            sArtistCircle = new ArtistCircle(context);
        }
        return sArtistCircle;
    }

    private ArtistCircle(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new ArtistDatabase(mContext).getWritableDatabase();
    }

    public void addArtist(Artist a){
        ContentValues values = getContentValues(a);

        mDatabase.insert(ArtistTable.NAME, null, values);
    }

//    public void deleteItem(String itemId){
//
//
//        mDatabase.delete(ArtistTable.NAME, ArtistTable.Columns.UUID + " = ?", new String[]{itemId});
//        Log.d(TAG, "is deleted");
//
//    }

    public void deleteArtist(String name){

        mDatabase.delete(ArtistTable.NAME, ArtistTable.Columns.NAME + " = ?", new String[]{name});
        Log.d(TAG, "is deleted");
    }

    public List<Artist> getArtists(){
        List<Artist> artists = new ArrayList<>();

        ArtistCursorWrapper cursor = queryArtist(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                artists.add(cursor.getArtist());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }

        return artists;
    }

    public Artist getArtist(UUID id){
        ArtistCursorWrapper cursor = queryArtist(
                ArtistTable.Columns.UUID + " = ?",
                new String[] { id.toString() }
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getArtist();
        }finally {
            cursor.close();
        }
    }

    public long getItemCount(){
        return DatabaseUtils.queryNumEntries(mDatabase, ArtistTable.NAME);
    }

    public int getSize(){
        return getArtists().size();
    }

    public void updateArtist(Artist artist){
        String uuidString = artist.getUuid().toString();
        ContentValues values = getContentValues(artist);

        mDatabase.update(ArtistTable.NAME, values,
                ArtistTable.Columns.UUID + " = ?",
                new String[] { uuidString });

    }

    public static ContentValues getContentValues(Artist artist){
        ContentValues values = new ContentValues();
        values.put(ArtistTable.Columns.UUID, artist.getUuid().toString());
        values.put(ArtistTable.Columns.NAME, artist.getName());
        values.put(ArtistTable.Columns.TYPE, artist.getType());
        values.put(ArtistTable.Columns.URL, artist.getUrl());
        values.put(ArtistTable.Columns.ID, artist.getId());

        return values;
    }

    private ArtistCursorWrapper queryArtist(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                ArtistTable.NAME,
                null, //columns
                whereClause,
                whereArgs,
                null, //groupBy
                null, //having
                null //orderBy
        );
        return new ArtistCursorWrapper(cursor);
    }

//    public boolean exists(String name){
//
//        String[] columns = { ArtistTable.Columns.NAME};
//        String selection = ArtistTable.Columns.NAME + " =?";
//        String[] selectionArgs = { name };
//        String limit = "1";
//
//        Cursor cursor = mDatabase.query(ArtistTable.NAME, columns, selection, selectionArgs, null, null, null, limit);
//        boolean exists = (cursor.getCount() > 0);
//        cursor.close();
//        return exists;
//    }

    public boolean exists(String name) {
        String selectString = "SELECT * FROM " + ArtistTable.NAME + " WHERE " + ArtistTable.Columns.NAME + " =?";

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = mDatabase.rawQuery(selectString, new String[] {name});

        boolean hasObject = false;
        if(cursor.moveToFirst()){
            hasObject = true;

            //region if you had multiple records to check for, use this region.

            int count = 0;
            while(cursor.moveToNext()){
                count++;
            }
            //here, count is records found
            Log.d(TAG, String.format("%d records found", count));

            //endregion

        }

        cursor.close();          // Dont forget to close your cursor
        //mDatabase.close();              //AND your Database!
        return hasObject;
    }
}
