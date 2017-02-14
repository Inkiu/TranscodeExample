package com.estsoft.muvigram.transcodeexample.Result;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.estsoft.muvigram.transcodeexample.Main.MainFragment;
import com.estsoft.muvigram.transcodeexample.Model.VideoData;
import com.estsoft.muvigram.transcodeexample.R;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private final static String EXTRA_VIDEO_DATA = "ResultActivity.video_datas";
    private ArrayList<VideoData> mVideoDatas;

    public static Intent getIntent(Context packageContext, ArrayList<VideoData> datas ) {
        Intent intent = new Intent( packageContext, ResultActivity.class );
        intent.putParcelableArrayListExtra( EXTRA_VIDEO_DATA, datas );
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mVideoDatas = getIntent().getParcelableArrayListExtra( EXTRA_VIDEO_DATA );
        makeFragment();
    }

    private void makeFragment() {
        Fragment fragment = ResultFragment.newInstance( mVideoDatas );
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .add( R.id.activity_result, fragment )
                    .commit();
        }
    }
}
