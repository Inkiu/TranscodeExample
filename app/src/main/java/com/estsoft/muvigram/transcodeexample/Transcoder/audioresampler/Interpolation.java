package com.estsoft.muvigram.transcodeexample.Transcoder.audioresampler;

/**
 * Created by estsoft on 2017-01-10.
 */

public interface Interpolation {

    short[] interpolate(int oldSampleRate, int newSampleRate, short[] samples);

}
