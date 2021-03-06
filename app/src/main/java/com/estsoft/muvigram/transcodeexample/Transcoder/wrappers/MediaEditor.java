package com.estsoft.muvigram.transcodeexample.Transcoder.wrappers;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;


import com.estsoft.muvigram.transcodeexample.Transcoder.transcoders.BufferListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by estsoft on 2016-12-21.
 */

public
class MediaEditor {
    private static final String TAG = "MediaEditor";
    public static final int NORMAL = -12;
    public static final int MUTE_AND_ADD_MUSIC = -13;
    public static final int ADD_MUSIC = -14;

    private final int CURRENT_MODE;
    private final int outputContainer = MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4;

    private final MuxerWrapper mMuxerWrapper;
    private final String mOutputFilePath;
    private final MediaTarget mTarget;
    private final List<MediaSegment> mSegmentLists;

    private final ProgressListener mListener;

    private MediaSegment mMusicSegment;
    private long mTotalEstimatedDuration;
    private long mSegmentTargetDuration;
    private boolean musicSegmentAdded;

    private int progressInterval;


    public MediaEditor(String outputPath, int transcodeMode, ProgressListener progressListener ) {
        this.mOutputFilePath = outputPath;
        this.CURRENT_MODE = transcodeMode;
        this.mTarget = new MediaTarget();
        this.mSegmentLists = new ArrayList<>();

        MediaMuxer muxer;
        try {
            muxer = new MediaMuxer( this.mOutputFilePath, outputContainer );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        this.mMuxerWrapper = new MuxerWrapper( muxer, CURRENT_MODE );
        this.mListener = progressListener;
    }

    public void initVideoTarget(int interval, int frameRate, int bitrate, int rotation, int width, int height ) {
        mTarget.initVideoTarget( interval, frameRate, bitrate, width, height );
        mMuxerWrapper.setOrientation( rotation );
        mMuxerWrapper.setVideoParams( frameRate );
    }

    public void initAudioTarget( int sampleRate, int channelCount, int bitrate ) {
        mTarget.initAudioTarget( sampleRate, channelCount, bitrate );
        mMuxerWrapper.setAudioParams( sampleRate );
    }


    /**
     * one video (with start point, end point) is a segment
     **/
    public void addSegment(String inputFilePath, long startTimeUs, long endTimeUs, int audioVolume  ) {
        if ( musicSegmentAdded )
            throw new IllegalStateException( "music segment can be added after all segments added " );
        if ( !(endTimeUs < 0) && startTimeUs >= endTimeUs)
            throw new IllegalStateException( "start can't be later than end " );

        int mode = MediaSegment.NORMAL;
        if ( CURRENT_MODE == MUTE_AND_ADD_MUSIC ) mode = MediaSegment.VIDEO_ONLY;
        MediaSegment segment = new MediaSegment( mTarget, inputFilePath, mBufferListener,
                startTimeUs, endTimeUs, audioVolume, mode );

        if ( segment.getStartTimeUs() < segment.getEndTimeUs() ) {
            mSegmentLists.add(segment);
            mTotalEstimatedDuration += segment.getEndTimeUs() - segment.getStartTimeUs();
            Log.e(TAG, "addSegment: Adding segment ... " + inputFilePath + " / "
                    + segment.getStartTimeUs() + " to " + segment.getEndTimeUs() );
        } else {
            Log.e(TAG, "addSegment: Skipping segment ... " + inputFilePath + " / "
                    + segment.getStartTimeUs() + " to " + segment.getEndTimeUs());
        }
    }


    /**
     * if musinSegment is added, origin video's audio will mute.
     **/
    public void addMusicSegment(String inputFilePath, long offset, int audioVolume ) {
        if ( CURRENT_MODE == NORMAL )
            throw new IllegalStateException( "to add MusicSegment, mode should be ADD_MUSIC or MUTE_AND_ADD_MUSIC " );

        musicSegmentAdded = true;
        mMusicSegment = new MediaSegment( mTarget, inputFilePath, mBufferListener,
                offset, mTotalEstimatedDuration + offset, audioVolume, MediaSegment.AUDIO_ONLY);
        Log.d(TAG, "addMusicSegment: " + mTotalEstimatedDuration);
    }

    public void startWork() {
        if ( mListener != null ) callListener( ProgressListener.START );
        if ( mMusicSegment != null ) mMusicSegment.prepare();

        boolean segmentStepped;
        long videoSyncBufferTimeUs = 0;
        long audioSyncBufferTimeUs = 0;
        for ( MediaSegment segment : mSegmentLists ) {
            Log.d(TAG, "start: Start of new segment");
            segment.prepare();
            segment.setSmallSync( videoSyncBufferTimeUs, audioSyncBufferTimeUs );

            long segmentDuration = segment.getEndTimeUs() - segment.getStartTimeUs();
            mSegmentTargetDuration += segmentDuration;

            while ( !segment.checkFinished() ) {
                segmentStepped = segment.stepOnce();
                if ( !segmentStepped ) sleepWhile( 20 );
                else if ( mMusicSegment != null && segment.isVideoEncodingStarted() ) {
                    musicSegmentStepping( segment.getVideoCurrentWrittenTimeUs() + mSegmentTargetDuration - segment.getEndTimeUs());
                }
                if (mListener != null) callListener( ProgressListener.PROGRESS );
            }

            videoSyncBufferTimeUs = (mMuxerWrapper.getVideoPresentationTimeUs() - mSegmentTargetDuration);
            audioSyncBufferTimeUs = (mMuxerWrapper.getAudioPresentationTimeUs() - mSegmentTargetDuration);

            Log.d(TAG, "start: End of this segment ... target Duration is " + mSegmentTargetDuration );
            segment.release();
        }

        // NOTE this method order is important
        if (mMusicSegment != null) flushMusicSegment();
        release();
        if (mListener != null) callListener( ProgressListener.COMPLETE );
    }

    public void release() {
        if (mMusicSegment != null) mMusicSegment.release();
        if (!mMuxerWrapper.isStopped()) mMuxerWrapper.stop();
        mMuxerWrapper.release();
    }

    private void musicSegmentStepping( long totalProcessed ) {
        long musicProcessed = mMusicSegment.getAudioCurrentWrittenTimeUs() - mMusicSegment.getStartTimeUs();
        long musicExtracted;
        while ( !mMusicSegment.checkFinished()
                && (!mMusicSegment.isAudioEncodingStarted() || totalProcessed >= musicProcessed) ) {
            boolean stepped = mMusicSegment.stepOnce();
            if (!stepped) sleepWhile( 20 );
            musicProcessed = mMusicSegment.getAudioCurrentWrittenTimeUs() - mMusicSegment.getStartTimeUs();
            musicExtracted = mMusicSegment.getAudioCurrentExtractedTimeUs() - mMusicSegment.getStartTimeUs();
            // NOTE for safety
            if ( musicExtracted >= mTotalEstimatedDuration )  mMusicSegment.forceStop();
        }
    }

    private void flushMusicSegment() {
        while ( !mMusicSegment.checkFinished() ) {
            Log.e(TAG, "flushMusicSegment: FLUSHING");
            boolean stepped = mMusicSegment.stepOnce();
            if (!stepped) sleepWhile( 20 );
        }
    }

    private void sleepWhile( long sleepUs ) {
        try { Thread.sleep( sleepUs ); }
        catch ( Exception e ) { throw new RuntimeException( e ); }
    }

    private void callListener( int mode ) {
        if ( mListener == null ) return;
        switch ( mode ) {
            case ProgressListener.START :
                mListener.onStart( mTotalEstimatedDuration );
                break;
            case ProgressListener.PROGRESS :
                if ( ++ progressInterval < ProgressListener.PROGRESS_INTERVAL ) return;
                progressInterval %= ProgressListener.PROGRESS_INTERVAL;
                mListener.onProgress( mMuxerWrapper.getVideoPresentationTimeUs(), (int) (mMuxerWrapper.getVideoPresentationTimeUs() * 100 / mTotalEstimatedDuration) );
                break;
            case ProgressListener.COMPLETE :
                mListener.onComplete( mMuxerWrapper.getVideoPresentationTimeUs() );
                break;
            case ProgressListener.ERROR :
                mListener.onError( new Exception( "Exception Occurred" ) );
                break;
            default :
                throw new IllegalStateException( "check listener mode" );
        }
    }

    /**
     * multiple video's encoded buffer goes one video Muxer
     */
    private final BufferListener mBufferListener = new BufferListener() {
        @Override
        public void onBufferAvailable(BufferType type, ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo) {
            mMuxerWrapper.writeSampleData(
                    type == BufferType.VIDEO ? MuxerWrapper.SampleType.VIDEO : MuxerWrapper.SampleType.AUDIO,
                    buffer,
                    bufferInfo );
        }

        @Override
        public void onOutputFormat(BufferListener.BufferType type, MediaFormat format) {
            mMuxerWrapper.setOutputFormat(
                    type == BufferType.VIDEO ? MuxerWrapper.SampleType.VIDEO : MuxerWrapper.SampleType.AUDIO,
                    format);
        }
    };
}
