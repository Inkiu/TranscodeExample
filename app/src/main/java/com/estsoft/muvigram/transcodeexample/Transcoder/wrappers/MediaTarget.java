package com.estsoft.muvigram.transcodeexample.Transcoder.wrappers;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.estsoft.muvigram.transcodeexample.Transcoder.utils.MediaFormatExtraInfo;
import com.estsoft.muvigram.transcodeexample.Transcoder.utils.TranscodeUtils;

import java.nio.ByteBuffer;

/**
 * Created by estsoft on 2016-12-08.
 */

public class MediaTarget {
    private static final String TAG = "MediaTarget";

    MediaFormat videoOutputFormat;
    private final String videoCodec = MediaFormatExtraInfo.MIMETYPE_VIDEO_AVC;
    private final int colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;

    MediaFormat audioOutputFormat;
    private final String audioCodec = MediaFormatExtraInfo.MIMETYPE_AUDIO_AAC;
    private final int audioAACProfile = MediaCodecInfo.CodecProfileLevel.AACObjectLC;

    public void initVideoTarget(int interval, int frameRate, int bitrate, int width, int height ) {
        videoOutputFormat = MediaFormat.createVideoFormat( videoCodec, width, height );
        videoOutputFormat.setInteger( MediaFormat.KEY_I_FRAME_INTERVAL, interval );
        videoOutputFormat.setInteger( MediaFormat.KEY_FRAME_RATE, frameRate );
        videoOutputFormat.setInteger( MediaFormat.KEY_BIT_RATE, bitrate );
        videoOutputFormat.setInteger( MediaFormat.KEY_COLOR_FORMAT, colorFormat );
    }

    public void initAudioTarget( int sampleRate, int channelCount, int bitrate ) {
        audioOutputFormat = MediaFormat.createAudioFormat( audioCodec, sampleRate, channelCount );
        audioOutputFormat.setInteger( MediaFormat.KEY_AAC_PROFILE, audioAACProfile );
        audioOutputFormat.setInteger( MediaFormat.KEY_BIT_RATE, bitrate );
    }

}
