package com.estsoft.muvigram.transcodeexample;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by estsoft on 2017-02-14.
 */

public class PermissionManager {

    private final int REQUEST_CODE = 1;
    private Activity mActivity;
    private String[] mPermissions;
    private String[] mRequirePermissions;

    public PermissionManager(Activity activity, String[] permissions) {
        this.mActivity = activity;
        this.mPermissions = permissions;
        checkRequirePermissions();

    }

    public boolean isRequirePermission() {
        return mRequirePermissions.length != 0;
    }
    public void requestPermission() {
        if (mRequirePermissions.length != 0 )
            ActivityCompat.requestPermissions( mActivity, mRequirePermissions, REQUEST_CODE );
    }

    public boolean onPermissionResult( String[] permissions, int[] grantResults ) {
        for ( int i = 0; i < grantResults.length; i ++ ) {
            if ( grantResults[i] != PackageManager.PERMISSION_GRANTED ) return false;
        }
        return true;
    }

    private void checkRequirePermissions() {
        List<String> requirePermissions = new ArrayList<>();
        for ( String permission : mPermissions ) {
            if ( ActivityCompat.checkSelfPermission( mActivity, permission)
                    != PackageManager.PERMISSION_GRANTED ) {
                requirePermissions.add( permission );
            }
        }
        this.mRequirePermissions = requirePermissions.toArray(new String[ requirePermissions.size() ]);
    }

}
