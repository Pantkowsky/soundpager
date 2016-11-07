package com.example.komputer.discogify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.komputer.discogify.Models.Artist;
import com.example.komputer.discogify.Models.ArtistCircle;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Komputer on 05/10/2016.
 */
public class DisplayDatabaseFragment extends Fragment {

    private static final String TAG = "DisplayDatabaseFragment";

    private RecyclerView mDisplayRecyclerView;
    private Adapter mAdapter;
    private Artist mArtist = new Artist();
    private ArtistCircle mArtistCircle;
    private List<Artist> mArtists;

    public static DisplayDatabaseFragment newInstance(){
        return new DisplayDatabaseFragment();
    }


    @Override
    public void onCreate(Bundle savedOnInstanceState){
        super.onCreate(savedOnInstanceState);
        setHasOptionsMenu(true);

        setRetainInstance(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        setupAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedOnInstanceState){
        View v = inflater.inflate(R.layout.fragment_display_artists, container, false);

        mDisplayRecyclerView = (RecyclerView)v.findViewById(R.id.display_fragment_recycler_view);
        mDisplayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();

        return v;
    }

    private void setupAdapter(){

        mArtistCircle = ArtistCircle.get(getActivity());
        mArtists = mArtistCircle.getArtists();

        if(mAdapter == null){
            mAdapter = new Adapter(mArtists);
            mDisplayRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setArtists(mArtists);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.database_fragment_menu, menu);

        final MenuItem item = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                List<Artist> filteredList = mAdapter.filter(mArtists, newText);
                mAdapter.animateTo(filteredList);
                mDisplayRecyclerView.scrollToPosition(0);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setupAdapter();
                return false;
            }
        });


    }

    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final Context mContext;
        private TextView mArtistTextView;
        private TextView mHiddenTag;

        public Holder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mContext = itemView.getContext();
            mArtistTextView = (TextView)itemView.findViewById(R.id.single_artist_text_view);
            mHiddenTag = (TextView)itemView.findViewById(R.id.hidden_tag);
        }

        public void bindArtist(Artist artist){
            mArtist = artist;
            mArtistTextView.setText(mArtist.getName());
            mArtistTextView.setId(mArtist.getId());
            mArtistTextView.setTag(mArtist.getType());
            mHiddenTag.setText(mArtist.getUrl());
            mHiddenTag.setTag(mArtist.getUuid());
        }

        public void onClick(View view){

            int uniqueId = mArtistTextView.getId();
            String type = mArtistTextView.getTag().toString();
            String url = mHiddenTag.getText().toString();

            Intent intent = ArtistActivity.newIntent(mContext, uniqueId, type, url);
            startActivity(intent);

        }

        public boolean onLongClick(View view){

            new AlertDialog.Builder(mContext)
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete " + mArtistTextView.getText() + " ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String uuid = mHiddenTag.getTag().toString();
                            //mArtistCircle.get(getActivity()).deleteItem(uuid);
                            mArtistCircle.get(getActivity()).deleteArtist(mArtistTextView.getText().toString());
                            mAdapter.animateTo(mArtists);
                            mDisplayRecyclerView.scrollToPosition(0);
                            setupAdapter();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

            return true;
        }

    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        private List<Artist> mArtists;

        public Adapter(List<Artist> artists){
            mArtists = artists;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.item_artist, viewGroup, false);

            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position){
            Artist artist = mArtists.get(position);
            holder.bindArtist(artist);
        }

        @Override
        public int getItemCount(){
            return mArtists.size();
        }

        public void setArtists(List<Artist> artists){
            mArtists = artists;
        }

        public Artist removeItem(int position) {
            final Artist artist = mArtists.remove(position);
            notifyItemRemoved(position);
            return artist;
        }

        public void addItem(int position, Artist artist) {
            mArtists.add(position, artist);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final Artist artist = mArtists.remove(fromPosition);
            mArtists.add(toPosition, artist);
            notifyItemMoved(fromPosition, toPosition);
        }

        public void animateTo(List<Artist> artists){
            applyAndAnimateRemovals(artists);
            applyAndAnimateAdditions(artists);
            applyAndAnimateMovedItems(artists);
        }

        private void applyAndAnimateRemovals(List<Artist> newArtists) {
            for (int i = mArtists.size() - 1; i >= 0; i--) {
                final Artist artist = mArtists.get(i);
                if (!newArtists.contains(artist)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<Artist> newArtists) {
            for (int i = 0, count = newArtists.size(); i < count; i++) {
                final Artist artist = mArtists.get(i);
                if (!mArtists.contains(artist)) {
                    addItem(i, artist);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<Artist> newArtists) {
            for (int toPosition = newArtists.size() - 1; toPosition >= 0; toPosition--) {
                final Artist artist = newArtists.get(toPosition);
                final int fromPosition = newArtists.indexOf(artist);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        private List<Artist> filter(List<Artist> artists, String query) {
            query = query.toLowerCase();

            final List<Artist> filteredItemList = new ArrayList<>();
            for (Artist artist : artists) {
                final String text = artist.getName().toLowerCase();
                if (text.contains(query)) {
                    filteredItemList.add(artist);
                }
            }
            return filteredItemList;
        }
    }


}
