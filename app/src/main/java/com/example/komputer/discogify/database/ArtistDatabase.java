package com.example.komputer.discogify.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.komputer.discogify.database.DatabaseSchema.ArtistTable;

/**
 * Created by Komputer on 02/10/2016.
 */
public class ArtistDatabase extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "artistBase.db";

    public ArtistDatabase(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + ArtistTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ArtistTable.Columns.UUID + ", " +
                ArtistTable.Columns.NAME + ", " +
                ArtistTable.Columns.TYPE + ", " +
                ArtistTable.Columns.URL + ", " +
                ArtistTable.Columns.ID +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }


}
