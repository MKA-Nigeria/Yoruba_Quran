package com.alium.quran_app_domain.entities;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aliumujib on 25/03/2018.
 */

public class ChapterEntity implements Parcelable{
    String name;
    String description;
    int indexID;
    boolean isMeccan;
    int verseCount;
    boolean chapterIsSplitFile;

    public ChapterEntity(String name, String description, int indexID, boolean isMeccan, int verseCount, boolean chapterIsSplitFile) {
        this.name = name;
        this.description = description;
        this.indexID = indexID;
        this.isMeccan = isMeccan;
        this.verseCount = verseCount;
        this.chapterIsSplitFile = chapterIsSplitFile;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChapterEntity that = (ChapterEntity) o;

        if (indexID != that.indexID) return false;
        if (isMeccan != that.isMeccan) return false;
        if (verseCount != that.verseCount) return false;
        if (chapterIsSplitFile != that.chapterIsSplitFile) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + indexID;
        result = 31 * result + (isMeccan ? 1 : 0);
        result = 31 * result + verseCount;
        result = 31 * result + (chapterIsSplitFile ? 1 : 0);
        return result;
    }

    protected ChapterEntity(Parcel in) {
        name = in.readString();
        description = in.readString();
        indexID = in.readInt();
        isMeccan = in.readByte() != 0;
        verseCount = in.readInt();
        chapterIsSplitFile = in.readByte() != 0;
    }

    public static final Creator<ChapterEntity> CREATOR = new Creator<ChapterEntity>() {
        @Override
        public ChapterEntity createFromParcel(Parcel in) {
            return new ChapterEntity(in);
        }

        @Override
        public ChapterEntity[] newArray(int size) {
            return new ChapterEntity[size];
        }
    };

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

    public int getVerseCount() {
        return verseCount;
    }

    public void setVerseCount(int verseCount) {
        this.verseCount = verseCount;
    }

    public boolean isChapterIsSplitFile() {
        return chapterIsSplitFile;
    }

    public void setChapterIsSplitFile(boolean chapterIsSplitFile) {
        this.chapterIsSplitFile = chapterIsSplitFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeInt(indexID);
        parcel.writeByte((byte) (isMeccan ? 1 : 0));
        parcel.writeInt(verseCount);
        parcel.writeByte((byte) (chapterIsSplitFile ? 1 : 0));
    }
}
