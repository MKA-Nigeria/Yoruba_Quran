package com.alium.yoruba_quran.ui.chapters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alium.quran_app_domain.entities.ChapterEntity;
import com.alium.yoruba_quran.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lucas on 04/01/2017.
 */

public class ChapterView extends FrameLayout {

    @BindView(R.id.item_view_user__row) View row;
    @BindView(R.id.item_view_user__name) TextView name;
    @BindView(R.id.item_view_user__description) TextView description;
    @BindView(R.id.item_view_user__action) View action;
    @BindView(R.id.item_view_verse_count) TextView verseCount;
    @BindView(R.id.index_id) TextView indexID;


    private ChapterEntity chapter;
    private ChapterView.OnPersonClickListener onPersonClickListener;

    public interface OnPersonClickListener {

        void onPersonClick(ChapterEntity chapter);

        void onPersonActionClick(ChapterEntity chapter);
    }

    public ChapterView(Context context) {
        super(context);
        init();
    }

    public ChapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_view_user_internal, this, true);
        ButterKnife.bind(this);
    }

    public void setUser(ChapterEntity chapter) {
        this.chapter = chapter;
        name.setText(chapter.getName());
        description.setText(chapter.getDescription());
        verseCount.setText(String.valueOf(chapter.getVerseCount()));
        indexID.setText(String.valueOf(chapter.getIndexID()));
    }

    public void setonPersonClickListener(final OnPersonClickListener onPersonClickListener) {
        this.onPersonClickListener = onPersonClickListener;
        if (onPersonClickListener != null) {
            row.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPersonClickListener.onPersonClick(chapter);
                }
            });
            action.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPersonClickListener.onPersonActionClick(chapter);
                }
            });
        } else {
            row.setOnClickListener(null);
            action.setOnClickListener(null);
        }
    }
}
