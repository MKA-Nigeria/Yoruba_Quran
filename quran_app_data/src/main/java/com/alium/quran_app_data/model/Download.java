package com.alium.quran_app_data.model;

import android.arch.persistence.room.Entity;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abdulmujibaliu on 3/13/17.
 */

@Entity
public class Download implements Parcelable {

    int inDexID;
    long downloadID;
    boolean downLoadDone;

    public int getInDexID() {
        return inDexID;
    }

    public void setInDexID(int inDexID) {
        this.inDexID = inDexID;
    }

    public long getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(long downloadID) {
        this.downloadID = downloadID;
    }

    public boolean isDownLoadDone() {
        return downLoadDone;
    }

    public void setDownLoadDone(boolean downLoadDone) {
        this.downLoadDone = downLoadDone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.inDexID);
        dest.writeLong(this.downloadID);
        dest.writeByte(this.downLoadDone ? (byte) 1 : (byte) 0);
    }

    public Download() {
    }

    protected Download(Parcel in) {
        this.inDexID = in.readInt();
        this.downloadID = in.readLong();
        this.downLoadDone = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Download> CREATOR = new Parcelable.Creator<Download>() {
        @Override
        public Download createFromParcel(Parcel source) {
            return new Download(source);
        }

        @Override
        public Download[] newArray(int size) {
            return new Download[size];
        }
    };
}
