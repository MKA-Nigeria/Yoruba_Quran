package com.alium.yoruba_quran.ui.chapters;

import android.support.v7.util.DiffUtil;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alium.quran_app_domain.entities.ChapterEntity;
import com.alium.yoruba_quran.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aliumujib on 04/01/2017.
 */

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.PersonViewHolder> {

    private List<ChapterEntity> notToBeEditedPeopleList = new ArrayList<>();

    private List<ChapterEntity> peopleList = new ArrayList<>();
    private ChapterView.OnPersonClickListener onPersonClickListener;


    /**
     * Copy and paste coding made possible by
     * https://stackoverflow.com/questions/30398247/how-to-filter-a-recyclerview-with-a-searchview
     * <p>
     * Shout out to that guy
     */

    public List<ChapterEntity> filter(String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<ChapterEntity> filteredModelList = new ArrayList<>();
        for (ChapterEntity model : notToBeEditedPeopleList) {
            final String text = model.getName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    //COPY AND PASTE CODING ENDS HERE

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        public ChapterView chapterView;

        public PersonViewHolder(ChapterView chapterView) {
            super(chapterView);
            this.chapterView = chapterView;
        }
    }

    public ChapterAdapter() {
        this.peopleList = new ArrayList<ChapterEntity>();
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

    public void setPeopleList(List<ChapterEntity> peopleList) {
        for (ChapterEntity chapterEntity : peopleList) {
            if (!notToBeEditedPeopleList.contains(chapterEntity)) {
                notToBeEditedPeopleList.add(chapterEntity);
            }
        }
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
