package com.estsoft.muvigram.transcodeexample.Main;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.estsoft.muvigram.transcodeexample.PermissionManager;
import com.estsoft.muvigram.transcodeexample.R;

public class MainActivity extends AppCompatActivity {
    private String[] mPermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private PermissionManager mPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionManager = new PermissionManager( this, mPermissions );
        if (mPermissionManager.isRequirePermission()) mPermissionManager.requestPermission();
        setContentView(R.layout.activity_main);
        makeFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allGranted = mPermissionManager.onPermissionResult( permissions, grantResults );
        if (!allGranted) this.finish();
    }

    private void makeFragment() {
        Fragment fragment = MainFragment.newInstance();
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .add( R.id.activity_main, fragment )
                    .commit();
        }
    }
}
