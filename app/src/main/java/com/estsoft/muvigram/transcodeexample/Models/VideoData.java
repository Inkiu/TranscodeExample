package com.estsoft.muvigram.transcodeexample.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by estsoft on 2017-02-13.
 */

public class VideoData implements Parcelable {

    public int cutOffPercent;
    public String videoPath;

    public VideoData(int percent, String videoPath) {
        this.cutOffPercent = percent;
        this.videoPath = videoPath;
    }

    public VideoData( Parcel src ) {
        this.cutOffPercent = src.readInt();
        this.videoPath = src.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt( cutOffPercent );
        parcel.writeString( videoPath );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getCutOffPercent() {
        return cutOffPercent;
    }

    public void setCutOffPercent(int cutOffPercent) {
        this.cutOffPercent = cutOffPercent;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.ClassLoaderCreator<VideoData>() {

        @Override
        public VideoData createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return createFromParcel(parcel);
        }

        @Override
        public VideoData createFromParcel(Parcel parcel) {
            return new VideoData(parcel);
        }

        @Override
        public VideoData[] newArray(int i) {
            return new VideoData[i];
        }
    };
}
