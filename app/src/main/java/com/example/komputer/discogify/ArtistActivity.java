package com.example.komputer.discogify;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.example.komputer.discogify.Models.Artist;

/**
 * Created by Komputer on 21/09/2016.
 */
public class ArtistActivity extends SingleFragmentActivity {

    private static final String EXTRA_ARTIST_ID = "com.example.komputer.discogify.artist_id";
    private static final String EXTRA_TYPE_ID = "com.example.komputer.discogify.type_id";
    private static final String EXTRA_URL = "com.example.komputer.discogify.artist_url";



    public static Intent newIntent(Context packageContext, int artistId, String type, String url){
        Intent intent = new Intent(packageContext, ArtistActivity.class);
        intent.putExtra(EXTRA_ARTIST_ID, artistId);
        intent.putExtra(EXTRA_TYPE_ID, type);
        intent.putExtra(EXTRA_URL, url);

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        int artistId = (int)getIntent().getSerializableExtra(EXTRA_ARTIST_ID);
        String type = (String) getIntent().getSerializableExtra(EXTRA_TYPE_ID);
        String url = (String) getIntent().getSerializableExtra(EXTRA_URL);
        return ArtistFragment.newInstance(artistId, type, url);
    }
//    public static Intent newIntent(Context packageContext, int artistId, String type){
//        Intent intent = new Intent(packageContext, ArtistActivity.class);
//        intent.putExtra(EXTRA_ARTIST_ID, artistId);
//        intent.putExtra(EXTRA_TYPE_ID, type);
//        return intent;
//    }
//
//    @Override
//    protected Fragment createFragment() {
//        int artistId = (int)getIntent().getSerializableExtra(EXTRA_ARTIST_ID);
//        String type = (String) getIntent().getSerializableExtra(EXTRA_TYPE_ID);
//        return ArtistFragment.newInstance(artistId, type);
//    }


}
