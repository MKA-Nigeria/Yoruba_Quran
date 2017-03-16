package com.lucasurbas.masterdetail.ui.chapters;

import android.support.v7.util.DiffUtil;

import com.lucasurbas.masterdetail.data.Chapter;

import java.util.List;

/**
 * Created by Lucas on 04/01/2017.
 */

public class ChaptersListDiffCallback extends DiffUtil.Callback {

    private List<Chapter> oldList;
    private List<Chapter> newList;

    public ChaptersListDiffCallback(List<Chapter> oldList, List<Chapter> newList) {
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
