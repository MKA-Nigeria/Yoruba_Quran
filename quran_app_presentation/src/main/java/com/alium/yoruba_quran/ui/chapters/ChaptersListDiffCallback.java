package com.alium.yoruba_quran.ui.chapters;

import android.support.v7.util.DiffUtil;


import com.alium.quran_app_domain.entities.ChapterEntity;

import java.util.List;

/**
 * Created by Lucas on 04/01/2017.
 */

public class ChaptersListDiffCallback extends DiffUtil.Callback {

    private List<ChapterEntity> oldList;
    private List<ChapterEntity> newList;

    public ChaptersListDiffCallback(List<ChapterEntity> oldList, List<ChapterEntity> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition) == oldList.get(oldItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).equals(oldList.get(oldItemPosition));
    }
}
