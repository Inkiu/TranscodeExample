
/*
 * Copyright (C) 2011 Jacquet Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.estsoft.muvigram.transcodeexample.Transcoder.audioresampler;

/**
 * Resample signal data (base on bytes)
 *
 * @author jacquet
 *
 */
public class Resampler {

    private Interpolation interpolation;

    public Resampler() {
        this.interpolation = new LinearInterpolation();
    }

    public short[] reSample( short[] sourceData, int sourceRate, int targetRate ) {
        return interpolation.interpolate(sourceRate, targetRate, sourceData);
    }

}