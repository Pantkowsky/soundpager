package com.example.komputer.discogify;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Komputer on 23/09/2016.
 */
public class ReleaseActivity extends SingleFragmentActivity {

    private static final String EXTRA_MAIN_RELEASE_ID = "com.example.komputer.discogify.main_release_id";
    private static final String EXTRA_RESOURCE_ID = "com.example.komputer.discogify.resource_id";

    public static Intent newIntent(Context packageContext, String resourceId, String mainReleaseId){
        Intent intent = new Intent(packageContext, ReleaseActivity.class);
        intent.putExtra(EXTRA_RESOURCE_ID, resourceId);
        intent.putExtra(EXTRA_MAIN_RELEASE_ID, mainReleaseId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String resourceId = (String)getIntent().getSerializableExtra(EXTRA_RESOURCE_ID);
        String mainReleaseId = (String)getIntent().getSerializableExtra(EXTRA_MAIN_RELEASE_ID);
        return ReleaseFragment.newInstance(resourceId, mainReleaseId);
    }

//    public static Intent newIntent(Context packageContext, String resourceId){
//        Intent intent = new Intent(packageContext, ReleaseActivity.class);
//        intent.putExtra(EXTRA_RESOURCE_ID, resourceId);
//        return intent;
//    }
//
//    @Override
//    protected Fragment createFragment() {
//        String resourceId = (String)getIntent().getSerializableExtra(EXTRA_RESOURCE_ID);
//        return ReleaseFragment.newInstance(resourceId);
//    }

}
