package com.example.komputer.discogify;

import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.komputer.discogify.Models.ArtistCircle;
import com.example.komputer.discogify.Models.SearchQuery;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private static ArtistCircle sArtistCircle;
    private TextView mDatabaseSizeTextView;
    private RecyclerView mSearchRecyclerView;
    private Button mSearchButton;
    private Button mDatabaseButton;
    private List<SearchQuery> mSearchQuery = new ArrayList<>();

    public static TextView mEnterArtistTextView;

    public static SearchFragment newInstance(){
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        sArtistCircle = ArtistCircle.get(getContext());
        mDatabaseSizeTextView = (TextView)v.findViewById(R.id.database_size);
        mDatabaseButton = (Button)v.findViewById(R.id.database_button);
        mDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DisplayDatabaseActivity.class);
                startActivity(intent);
            }
        });
        mEnterArtistTextView = (TextView)v.findViewById(R.id.enter_artist_view);
        mSearchButton = (Button)v.findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEnterArtistTextView.length() > 0){
                    new FetchMainTask().execute();
                }
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        mSearchRecyclerView = (RecyclerView)v.findViewById(R.id.search_fragment_recycler_view);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateDatabaseSize();
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateDatabaseSize();
    }

    private void updateDatabaseSize(){
        int x = sArtistCircle.get(getContext()).getSize();

        if(x>0){
            mDatabaseSizeTextView.setText("Currently the list contains " + String.valueOf(sArtistCircle.getItemCount()) + " items.");
        } else {
            mDatabaseSizeTextView.setText("The list does not have any records.");
        }
    }

    //method that gets the input from searchbox and passes it to the URL builder in DiscogsFetchr
    public static String getArtistName(){
        return mEnterArtistTextView.getText().toString();
    }

    private void setupAdapter(){
        if(isAdded()){
            mSearchRecyclerView.setAdapter(new Adapter(mSearchQuery));
        }
    }

    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Context mContext;
        private TextView mTitleTextView;
        private TextView mTypeTextView;

        public Holder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mContext = itemView.getContext();

            mTitleTextView = (TextView)itemView.findViewById(R.id.search_query_title_view);
            mTypeTextView = (TextView)itemView.findViewById(R.id.search_query_type_view);
        }

        public void bindSearchQuery(SearchQuery searchQuery){
            mTitleTextView.setText(searchQuery.getName());                  //name of fetched item
            mTitleTextView.setId(Integer.parseInt(searchQuery.getId()));    //ID of the item
            mTitleTextView.setTag(searchQuery.getResource());               //Resource_url of the item
            mTypeTextView.setText(searchQuery.getType().toUpperCase());     //Type of item
        }

        public void onClick(View view){

            int uniqueId = mTitleTextView.getId();                          // ID of an artist/label
            String url = mTitleTextView.getTag().toString();                // URL to releases
            String check = mTypeTextView.getText().toString();
            String type;                                                    //Type artist/label
            if(check.equals("ARTIST")){
                type = "artists";
            }else{
                type = "labels";
            }
            Intent intent = ArtistActivity.newIntent(mContext, uniqueId, type, url);
            startActivity(intent);
        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder>{
        private List<SearchQuery> mSearchQueries;

        public Adapter(List<SearchQuery> searchQuery){
            mSearchQueries = searchQuery;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.item_search_query, viewGroup, false);

            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position){
            SearchQuery searchQuery = mSearchQueries.get(position);
            holder.bindSearchQuery(searchQuery);
        }

        @Override
        public int getItemCount(){
            return mSearchQueries.size();
        }
    }

    private class FetchMainTask extends AsyncTask<Void,Void,List<SearchQuery>>{

        @Override
        protected List<SearchQuery> doInBackground(Void...params){

            List<SearchQuery> unfilteredList = new DiscogsFetchr().fetchSearchQuery();
            return filter(unfilteredList);
        }

        @Override
        protected void onPostExecute(List<SearchQuery> searchQuery){
            mSearchQuery = searchQuery;
            updateDatabaseSize();
            setupAdapter();
        }

        private List<SearchQuery> filter(List<SearchQuery> list){
            List<SearchQuery> filteredList = new ArrayList<>();
            for(SearchQuery type: list){
                if (type.getType().equals("artist") || type.getType().equals("label")){
                    filteredList.add(type);
                }
            }
            return filteredList;
        }
    }
}
