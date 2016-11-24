package com.example.komputer.discogify;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.komputer.discogify.Models.Artist;
import com.example.komputer.discogify.Models.ArtistCircle;
import com.example.komputer.discogify.Models.ArtistReleases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class DisplayRecentFragment extends Fragment {

    private static final String TAG = "DisplayRecentFragment";

    private static ArtistCircle sArtistCircle;
    private List<ArtistReleases> mArtistReleases = new ArrayList<>();
    private RecyclerView mDisplayRecyclerView;
//    private boolean mRunning = true;

    public static DisplayRecentFragment newInstance(){
        return new DisplayRecentFragment();
    }

    @Override
    public void onCreate(Bundle savedOnInstanceState){
        super.onCreate(savedOnInstanceState);
        setRetainInstance(true);

        //mRunning = true;
        downloadRecent();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
//        mRunning = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedOnInstanceState){
        View v = inflater.inflate(R.layout.fragment_display_recent, container, false);

        mDisplayRecyclerView = (RecyclerView)v.findViewById(R.id.display_recent_recycler_view);
        mDisplayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    private void setupAdapter(){
        if(isAdded()){
            mDisplayRecyclerView.setAdapter(new ArtistAdapter(mArtistReleases));
        }
    }

    private void downloadRecent(){
        sArtistCircle = ArtistCircle.get(getContext());
        for (Artist artist: sArtistCircle.getArtists()) {
            UUID uuid = artist.getUuid();
            new FetchRecentReleasesTask().execute(uuid);
        }
    }

    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mAuthorTextView;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mDateAddedTextView;
        private TextView mFormatTextView;
        private TextView mHiddenTextView;
        private Context mContext;

        public Holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContext = itemView.getContext();

            mAuthorTextView = (TextView)itemView.findViewById(R.id.release_author_text_view);
            mTitleTextView = (TextView)itemView.findViewById(R.id.release_title_text_view);
            mDateTextView = (TextView)itemView.findViewById(R.id.release_date_text_view);
            mDateAddedTextView = (TextView)itemView.findViewById(R.id.release_added_date_text_view);
            mFormatTextView = (TextView)itemView.findViewById(R.id.release_format_Text_view);
            mHiddenTextView = (TextView)itemView.findViewById(R.id.hidden_tag);
        }

        public void bindArtistReleases(ArtistReleases artistReleases) {
            mAuthorTextView.setText(artistReleases.getProducer());
            mTitleTextView.setText(artistReleases.getTitle());
            mDateTextView.setText(artistReleases.getDateReleased());
            mDateAddedTextView.setText(artistReleases.getDateAdded());
            mFormatTextView.setText(artistReleases.getFormat());
            mAuthorTextView.setTag(artistReleases.getType());
            mHiddenTextView.setTag(artistReleases.getType());
            mDateTextView.setTag(artistReleases.getMainRelease());
            mTitleTextView.setTag(artistReleases.getResource());
            mTitleTextView.setId(Integer.parseInt(artistReleases.getId()));
        }

        public void onClick(View view) throws NullPointerException {

            String resourceId = mTitleTextView.getTag().toString();
            String type = mAuthorTextView.getTag().toString();
            String mainReleaseId;

            if(type.equals("labels")){
                mainReleaseId = String.valueOf(mTitleTextView.getId());
            }
            else{
                if(mHiddenTextView.getTag().equals("release")){
                    mainReleaseId = String.valueOf(mTitleTextView.getId());
                }else {
                    try{
                        mainReleaseId = mDateTextView.getTag().toString();
                    }catch(NullPointerException npe){
                        mainReleaseId = String.valueOf(mTitleTextView.getId());
                    }
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
            View v = inflater.inflate(R.layout.item_display_recent, viewGroup, false);

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

//    private class FetchRecentReleasesTask extends AsyncTask<UUID,Void,List<ArtistReleases>> {
//
//        @Override
//        protected List<ArtistReleases> doInBackground(UUID...uuid){
//
//            Artist artist = sArtistCircle.getArtist(uuid[0]);
//            List<ArtistReleases> unfilteredList = new DiscogsFetchr().fetchRecentReleases(artist.getFormattedName());
//            return unfilteredList;
//        }
//
//        @Override
//        protected void onPostExecute(List<ArtistReleases> artistReleases){
//
//            for (ArtistReleases release: artistReleases) {
//                mArtistReleases.add(release);
//            }
//            setupAdapter();
//        }
//    }


    private class FetchRecentReleasesTask extends AsyncTask<UUID,Void,List<ArtistReleases>> {

        @Override
        protected List<ArtistReleases> doInBackground(UUID...uuid){

            Artist artist = sArtistCircle.getArtist(uuid[0]);
            Log.i(TAG, "artist url is: " + artist.getUrl());
            List<ArtistReleases> unfilteredList = new DiscogsFetchr().fetchRecentReleases(artist.getUrl());
            Log.i(TAG, "Loading...");
            return unfilteredList;
        }

        @Override
        protected void onPostExecute(List<ArtistReleases> artistReleases){

            for (ArtistReleases release: artistReleases) {
                if(sArtistCircle.exists(release.getProducer()))
                mArtistReleases.add(release);
            }
            setupAdapter();
            Log.i(TAG, "Completed.");
        }

    }
}
