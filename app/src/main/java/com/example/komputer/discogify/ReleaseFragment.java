package com.example.komputer.discogify;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.example.komputer.discogify.Models.Release;

import java.util.ArrayList;
import java.util.List;

public class ReleaseFragment extends Fragment {

    private static final String TAG = "ReleaseFragment";
    private static final String ARG_RESOURCE_ID = "resource_id";
    private static final String ARG_MAIN_RELEASE_ID = "main_release_id";

    private RecyclerView mReleaseRecyclerView;
    private List<Release> mReleases = new ArrayList<>();
    private Release mRelease = new Release();
    private TextView mReleaseTitleTextView;
    private TextView mReleaseDateReleasedTextView;

    public static ReleaseFragment newInstance(String resourceId, String mainReleaseId) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_RESOURCE_ID, resourceId);
        args.putSerializable(ARG_MAIN_RELEASE_ID, mainReleaseId);

        ReleaseFragment fragment = new ReleaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedOnInstanceState){
        super.onCreate(savedOnInstanceState);

        String resourceId = (String)getArguments().getSerializable(ARG_RESOURCE_ID);
        String mainReleaseId = (String)getArguments().getSerializable(ARG_MAIN_RELEASE_ID);
        new FetchReleaseContentsTask().execute(resourceId);
        new FetchReleaseInfoTask().execute(mainReleaseId);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedOnInstanceState){
        View v = inflater.inflate(R.layout.fragment_release, container, false);

        mReleaseTitleTextView = (TextView)v.findViewById(R.id.fragment_release_title_text_view);
        mReleaseDateReleasedTextView = (TextView)v.findViewById(R.id.fragment_release_date_released_text_view);

        mReleaseRecyclerView = (RecyclerView)v.findViewById(R.id.release_fragment_recycler_view);
        mReleaseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    private void setupAdapter(){
        if(isAdded()){
            mReleaseRecyclerView.setAdapter(new ReleaseAdapter(mReleases));
        }
    }

    private void setupReleaseInfo(){
        mReleaseTitleTextView.setText(mRelease.getTitle());
        mReleaseDateReleasedTextView.setText(mRelease.getReleaseDate());
    }

    private class Holder extends RecyclerView.ViewHolder {
        private TextView mPositionTextView;
        private TextView mTrackTitleTextView;
        private TextView mDurationTextView;

        public Holder(View itemView) {
            super(itemView);

            mPositionTextView = (TextView)itemView.findViewById(R.id.release_view_position);
            mTrackTitleTextView = (TextView) itemView.findViewById(R.id.release_view_title);
            mDurationTextView = (TextView)itemView.findViewById(R.id.release_view_duration);
        }

        public void bindReleases(Release release) {
            mPositionTextView.setText(release.getPosition());
            mTrackTitleTextView.setText(release.getTrackTitle());
            mDurationTextView.setText(release.getDuration());
        }
    }

    private class ReleaseAdapter extends RecyclerView.Adapter<Holder>{
        private List<Release> mReleases;

        public ReleaseAdapter(List<Release> releases){
            mReleases = releases;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.item_release, viewGroup, false);

            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position){
            Release release = mReleases.get(position);
            holder.bindReleases(release);
        }

        @Override
        public int getItemCount() {
            return mReleases.size();
        }
    }


    private class FetchReleaseContentsTask extends AsyncTask<String,Void,List<Release>> {

        @Override
        protected List<Release> doInBackground(String...params){

            String directLink = params[0].toString();
            return new DiscogsFetchr().fetchReleaseContents(directLink);

        }

        @Override
        protected void onPostExecute(List<Release> releases){
            mReleases = releases;
            setupAdapter();
        }
    }

    private class FetchReleaseInfoTask extends AsyncTask<String,Void,Release>{

        @Override
        protected Release doInBackground(String...params){
            String mainReleaseId = params[0].toString();
            return new DiscogsFetchr().fetchReleaseInfo(mainReleaseId);
        }

        @Override
        protected void onPostExecute(Release release){
            mRelease = release;
            setupReleaseInfo();
        }
    }
}
