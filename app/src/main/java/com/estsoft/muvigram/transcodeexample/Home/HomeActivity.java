package com.estsoft.muvigram.transcodeexample.Home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.estsoft.muvigram.transcodeexample.Models.VideoData;
import com.estsoft.muvigram.transcodeexample.R;
import com.estsoft.muvigram.transcodeexample.Utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final int REQUEST_STORAGE = 1;
    private final int REQUEST_TAKE_VIDEO = 0;
    private List<String> mSelectedVideoPaths = new ArrayList<>();
    private List<SeekBar> mSelectedVideoSeekBars = new ArrayList<>();
    private List<VideoData> mSelectedVideoInfo = new ArrayList<>();

    @BindView(R.id.gallery_button) Button mVideoSelectButton;
    @BindView(R.id.progress_container) LinearLayout mProgressContainer;
    @OnClick(R.id.gallery_button) public void onGalleryButtonClicked() { getVideoFromGallery(); }
    @OnClick(R.id.confirm_button) public void onConfirmButtonClicked() { makeFragment(); }
    Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    REQUEST_STORAGE );
        }

        setContentView(R.layout.activity_home);
        mUnbinder = ButterKnife.bind( this );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE) {
            if ( grantResults[0] != PackageManager.PERMISSION_GRANTED ) this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode != Activity.RESULT_OK ) {
            return;
        }
        Uri uri = data.getData();
        String filePath = Utils.uriParseToAbsolutePath(this, uri);
        mSelectedVideoPaths.add( filePath );
        mSelectedVideoSeekBars.add( inflateVideoController( filePath ) );
    }

    private void makeFragment() {
        for ( int i = 0; i < mSelectedVideoPaths.size(); i ++ ) {
            VideoData data = new VideoData(
                    mSelectedVideoSeekBars.get(i).getProgress(),
                    mSelectedVideoPaths.get(i) );
            mSelectedVideoInfo.add(data);
        }
        Fragment fragment = TranscodeFragment.newInstance( mSelectedVideoInfo.toArray(new VideoData[ mSelectedVideoInfo.size() ]) );
        if ( fragment != null ) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }


    private void getVideoFromGallery( ) {
        if ( mSelectedVideoPaths.size() >= 3 ) {
            Toast.makeText( this, "You can't select 5 more videos ", Toast.LENGTH_SHORT).show();
            for ( String path : mSelectedVideoPaths ) Log.d(TAG, "getVideoFromGallery: " + path );
            return;
        }
        Intent intent = new Intent();
        intent.setType( "video/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( intent.createChooser( intent, "Select Video"), REQUEST_TAKE_VIDEO );
    }

    private SeekBar inflateVideoController(String filePath ) {
        String[] split = filePath.split( File.separator );
        TextView textView = new TextView( this );
        textView.setText( split[ split.length - 1] );

        SeekBar seekBar = new SeekBar( this, null, android.R.attr.seekBarStyle );
        seekBar.setMax( 100 );
        seekBar.setProgress( 50 );
        mProgressContainer.addView( textView );
        mProgressContainer.addView(seekBar);
        return seekBar;
    }


}
