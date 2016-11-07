package com.example.komputer.discogify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.komputer.discogify.Models.Artist;
import com.example.komputer.discogify.Models.ArtistCircle;
import com.example.komputer.discogify.Models.ArtistReleases;
import com.example.komputer.discogify.database.DatabaseSchema;

import java.util.ArrayList;
import java.util.List;


public class ArtistFragment extends Fragment {

    private static final String TAG = "ArtistFragment";
    private static final String ARG_ARTIST_ID = "artist_id";
    private static final String ARG_TYPE = "type_id";
    private static final String ARG_URL = "url_id";
    private static ArtistCircle sArtistCircle;
    private RecyclerView mArtistRecyclerView;
    private TextView mArtistTitleTextView;
    private TextView mArtistInfoTextView;
    private List<ArtistReleases> mArtistReleases = new ArrayList<>();
    private Artist mArtist = new Artist();
    private MenuItem mFavoriteButton;
    private int mId;
    private String mUrl;
    private String mType;

    public static ArtistFragment newInstance(int artistId, String type, String url){
        Bundle args = new Bundle();
        args.putSerializable(ARG_URL, url);
        args.putSerializable(ARG_ARTIST_ID, artistId);
        args.putSerializable(ARG_TYPE, type);

        ArtistFragment fragment = new ArtistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mId = (int)getArguments().getSerializable(ARG_ARTIST_ID);
        mUrl = (String)getArguments().getSerializable(ARG_URL);
        mType = (String) getArguments().getSerializable(ARG_TYPE);

        new FetchArtistInfoTask().execute(mId);
        new FetchArtistReleasesTask().execute(mUrl);

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_artist, container, false);

        sArtistCircle = ArtistCircle.get(getContext());
        mArtistTitleTextView = (TextView) v.findViewById(R.id.fragment_artist_name);
        mArtistInfoTextView = (TextView)v.findViewById(R.id.fragment_artist_description);
        mArtistRecyclerView = (RecyclerView)v.findViewById(R.id.artist_fragment_recycler_view);
        mArtistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.artist_fragment_menu, menu);

        mFavoriteButton = menu.findItem(R.id.menu_item_favorite);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_favorite:
                if(sArtistCircle.exists(mArtist.getName())){
                    sArtistCircle.deleteArtist(mArtist.getName());
                    Toast.makeText(getContext(), "Artist deleted", Toast.LENGTH_SHORT).show();
                }else{
                    sArtistCircle.addArtist(new Artist(mArtist.getName(), mType, mUrl, mId));
                    int color = Color.parseColor("#FFFF00");
                    item.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    Toast.makeText(getContext(), "Artist added", Toast.LENGTH_SHORT).show();
                }
                invalidateOptionsMenu();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void invalidateOptionsMenu(){
        if(sArtistCircle.exists(mArtist.getName())){
            int color = Color.parseColor("#FFFF00");
            mFavoriteButton.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        } else{
            mFavoriteButton.getIcon().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void setupAdapter(){
        if(isAdded()){
            mArtistRecyclerView.setAdapter(new ArtistAdapter(mArtistReleases));
        }
    }

    private void setupArtistInfo(){
        mArtistTitleTextView.setText(mArtist.toString());
        mArtistTitleTextView.setTextSize(20);
        mArtistInfoTextView.setText(mArtist.getProfile());
    }

    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mYearTextView;
        private TextView mHiddenTextView;
        private Context mContext;

        public Holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContext = itemView.getContext();
            mTitleTextView = (TextView) itemView.findViewById(R.id.artist_release_title_text_view);
            mYearTextView = (TextView)itemView.findViewById(R.id.artist_release_year_text_view);
            mHiddenTextView = (TextView)itemView.findViewById(R.id.hidden_tag);
        }

        public void bindArtistReleases(ArtistReleases artistReleases) {
            mTitleTextView.setText(artistReleases.getTitle());
            mYearTextView.setText(artistReleases.getYear());
            mTitleTextView.setId(Integer.parseInt(artistReleases.getId()));
            mTitleTextView.setTag(artistReleases.getResource());
            if(artistReleases.getMainRelease() != null){
                mYearTextView.setTag(artistReleases.getMainRelease());
            }
            if(artistReleases.getType() != null){
                mHiddenTextView.setTag(artistReleases.getType());
            }
        }

        public void onClick(View view) {
            Toast.makeText(getActivity(), mTitleTextView.getText() + "clicked", Toast.LENGTH_SHORT).show();

            String resourceId = mTitleTextView.getTag().toString();
            String mainReleaseId;

            if(mType.equals("labels")){
                mainReleaseId = String.valueOf(mTitleTextView.getId());
            }
            else{
                if(mHiddenTextView.getTag().equals("release")){
                    mainReleaseId = String.valueOf(mTitleTextView.getId());     //if release doesn't have main_release value then set mainReleaseId as its ID
                }else {
                    mainReleaseId = mYearTextView.getTag().toString();
                }
            }
            Intent intent = ReleaseActivity.newIntent(mContext, resourceId, mainReleaseId);
            startActivity(intent);
        }
    }

    private class ArtistAdapter extends RecyclerView.Adapter<Holder>{
        private List<ArtistReleases> mArtistReleases;

        public ArtistAdapter(List<ArtistReleases> artistReleases){
            mArtistReleases = artistReleases;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.item_artist_release, viewGroup, false);

            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position){
            ArtistReleases artistReleases = mArtistReleases.get(position);
            holder.bindArtistReleases(artistReleases);
        }

        @Override
        public int getItemCount() {
            return mArtistReleases.size();
        }
    }

    private class FetchArtistReleasesTask extends AsyncTask<String,Void,List<ArtistReleases>> {

        @Override
        protected List<ArtistReleases> doInBackground(String...link){
            if(mType.equals("artists")){
                List<ArtistReleases> unfilteredList = new DiscogsFetchr().fetchReleases(mUrl);
                return filter(unfilteredList);
            }else{
                return new DiscogsFetchr().fetchReleases(mUrl);
            }
        }

        @Override
        protected void onPostExecute(List<ArtistReleases> artistReleases){
            mArtistReleases = artistReleases;
            setupAdapter();
        }

        private List<ArtistReleases> filter(List<ArtistReleases> list){
            List<ArtistReleases> filteredList = new ArrayList<>();
            for(ArtistReleases release: list){
                if (release.getProducer().contains(mArtist.getName())){
                    filteredList.add(release);
                }
            }
            return filteredList;
        }
    }

    private class FetchArtistInfoTask extends AsyncTask<Integer,Void,Artist> {

        @Override
        protected Artist doInBackground(Integer...artistId){

            int id = artistId[0].intValue();
            return new DiscogsFetchr().fetchInfo(id, mType);
        }

        @Override
        protected void onPostExecute(Artist artist) {
            mArtist = artist;
            setupArtistInfo();
            invalidateOptionsMenu();
        }
    }
}
