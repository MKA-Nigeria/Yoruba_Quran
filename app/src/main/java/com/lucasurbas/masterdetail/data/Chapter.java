package com.lucasurbas.masterdetail.data;

import android.os.Parcel;
import android.os.Parcelable;

import fr.xebia.android.freezer.annotations.Model;

/**
 * Created by Lucas on 04/01/2017.
 */

@Model
public class Chapter implements Parcelable {

    String id;
    String name;
    String description;
    int indexID;
    boolean isMeccan;
    int verseCount;
    boolean chapterIsSplitFile;
    String startFileNamePGNumPDFSizeCount; //01_02YOR:1:34 //or for baqarah 01_02YOR:2:34
    String endFileNamePGNumPDFSizeCount; //01_02YOR:1:34 //or for baqarah 02YOR:32:32

    public Chapter() {
    }

    public int getVerseCount() {
        return verseCount;
    }

    public void setVerseCount(int verseCount) {
        this.verseCount = verseCount;
    }

    public int getIndexID() {
        return indexID;
    }

    public void setIndexID(int indexID) {
        this.indexID = indexID;
    }


    public boolean isMeccan() {
        return isMeccan;
    }

    public void setMeccan(boolean meccan) {
        isMeccan = meccan;
    }

    public Chapter(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chapter chapter = (Chapter) o;

        return id != null ? id.equals(chapter.id) : chapter.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getId() {
        return id;
    }

    public boolean isChapterIsSplitFile() {
        return chapterIsSplitFile;
    }

    public void setChapterIsSplitFile(boolean chapterIsSplitFile) {
        this.chapterIsSplitFile = chapterIsSplitFile;
    }

    public String getStartFileNamePGNumPDFSizeCount() {
        return startFileNamePGNumPDFSizeCount;
    }

    public void setStartFileNamePGNumPDFSizeCount(String startFileNamePGNumPDFSizeCount) {
        this.startFileNamePGNumPDFSizeCount = startFileNamePGNumPDFSizeCount;
    }

    public String getEndFileNamePGNumPDFSizeCount() {
        return endFileNamePGNumPDFSizeCount;
    }

    public void setEndFileNamePGNumPDFSizeCount(String endFileNamePGNumPDFSizeCount) {
        this.endFileNamePGNumPDFSizeCount = endFileNamePGNumPDFSizeCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeInt(this.indexID);
        dest.writeByte(this.isMeccan ? (byte) 1 : (byte) 0);
        dest.writeInt(this.verseCount);
        dest.writeByte(this.chapterIsSplitFile ? (byte) 1 : (byte) 0);
        dest.writeString(this.startFileNamePGNumPDFSizeCount);
        dest.writeString(this.endFileNamePGNumPDFSizeCount);
    }

    protected Chapter(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.indexID = in.readInt();
        this.isMeccan = in.readByte() != 0;
        this.verseCount = in.readInt();
        this.chapterIsSplitFile = in.readByte() != 0;
        this.startFileNamePGNumPDFSizeCount = in.readString();
        this.endFileNamePGNumPDFSizeCount = in.readString();
    }

}
