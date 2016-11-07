package com.example.komputer.discogify.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.komputer.discogify.Models.Artist;
import com.example.komputer.discogify.database.DatabaseSchema.ArtistTable;

import java.util.UUID;

/**
 * Created by Komputer on 02/10/2016.
 */
public class ArtistCursorWrapper extends CursorWrapper {
    public ArtistCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Artist getArtist(){
        String uuidString = getString(getColumnIndex(ArtistTable.Columns.UUID));
        String name = getString(getColumnIndex(ArtistTable.Columns.NAME));
        String type = getString(getColumnIndex(ArtistTable.Columns.TYPE));
        String url = getString(getColumnIndex(ArtistTable.Columns.URL));
        int id = getInt(getColumnIndex(ArtistTable.Columns.ID));

        Artist artist = new Artist(UUID.fromString(uuidString));
        artist.setName(name);
        artist.setType(type);
        artist.setUrl(url);
        artist.setId(id);

        return artist;
    }
}
