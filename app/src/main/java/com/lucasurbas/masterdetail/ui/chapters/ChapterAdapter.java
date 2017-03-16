package com.lucasurbas.masterdetail.ui.chapters;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lucasurbas.masterdetail.R;
import com.lucasurbas.masterdetail.data.Chapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 04/01/2017.
 */

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.PersonViewHolder> {

    private List<Chapter> peopleList;
    private ChapterView.OnPersonClickListener onPersonClickListener;


    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        public ChapterView chapterView;

        public PersonViewHolder(ChapterView chapterView) {
            super(chapterView);
            this.chapterView = chapterView;
        }
    }

    public ChapterAdapter() {
        this.peopleList = new ArrayList<Chapter>();
    }

    public void setOnPersonClickListener(ChapterView.OnPersonClickListener onPersonClickListener) {
        this.onPersonClickListener = onPersonClickListener;
    }

    @Override
    public ChapterAdapter.PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChapterView view = (ChapterView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_user, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        holder.chapterView.setUser(peopleList.get(position));
        holder.chapterView.setonPersonClickListener(onPersonClickListener);
    }

    public void setPeopleList(List<Chapter> peopleList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ChaptersListDiffCallback(this.peopleList, peopleList));
        this.peopleList.clear();
        this.peopleList.addAll(peopleList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }
}
