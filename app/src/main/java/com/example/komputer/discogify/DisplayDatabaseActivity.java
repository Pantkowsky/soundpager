package com.example.komputer.discogify;

import android.support.v4.app.Fragment;


public class DisplayDatabaseActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return DisplayDatabaseFragment.newInstance();
    }
}
