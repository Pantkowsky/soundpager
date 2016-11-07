package com.example.komputer.discogify;


import android.support.v4.app.Fragment;


public class SearchActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment(){
        return SearchFragment.newInstance();
    }


}
