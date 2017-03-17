package com.lucasurbas.masterdetail.ui.chapters;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.lucasurbas.masterdetail.app.Constants;
import com.lucasurbas.masterdetail.data.Chapter;
import com.lucasurbas.masterdetail.data.ChapterEntityManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import static com.lucasurbas.masterdetail.app.Constants.FOLDER_NAME;

/**
 * Created by Lucas on 04/01/2017.
 */

public class ChaptersPresenter implements ChapterContract.Presenter {

    private final ChapterEntityManager mChapterEntityManager;
    private Context context;
    private ChapterContract.View view;
    private ChapterContract.Navigator navigator;

    private List<Chapter> chapterList;
    private List<String> names;

    @Inject
    public ChaptersPresenter(Context context, ChapterContract.Navigator navigator) {
        this.navigator = navigator;
        this.context = context;
        mChapterEntityManager = new ChapterEntityManager();
    }

    @Override
    public void attachView(ChapterContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void getPeople() {

        chapterList = mChapterEntityManager.select()
                .asList();
        if (chapterList.size() == 0) {
            getData();
        }
        view.showPeopleList(chapterList);
    }

    private void getData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.CHAPTER_CHAPTERS_TABLE_NAME);
        query.setLimit(200);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("CHAPTER", "OBJECT SIZE" + objects.size());
                    chapterList = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject object = objects.get(i);
                        Chapter chapter = new Chapter(object.getObjectId());
                        chapter.setName(object.getString(Constants.CHAPTER_TITLE));
                        String desc;

                        if (object.getBoolean(Constants.CHAPTER_IS_MECCAN)) {
                            desc = "Mecca";
                        } else {
                            desc = "Medina";
                        }

                        chapter.setDescription(desc);
                        chapter.setIndexID(object.getInt(Constants.CHAPTER_INDEX_ID));
                        chapter.setVerseCount(object.getInt(Constants.CHAPTER_VERSE_COUNT));
                        mChapterEntityManager.add(chapter);
                    }
                    getPeople();
                } else {
                    e.printStackTrace();
                    //TODO show error dialog
                }
            }
        });
    }

    private String getRandomName() {
        Random r = new Random();
        return names.get(r.nextInt(names.size()));
    }

    @Override
    public void clickPerson(Chapter chapter) {
        final String fileURl = Environment.getExternalStorageDirectory() + FOLDER_NAME + "/" + chapter.getIndexID() + "YOR.PDF";
        File file = new File(fileURl);
        if (file.exists()) {
            navigator.goToPersonDetails(chapter);
        }else {
            view.showToast("Quran File for " + chapter.getName() + " not found, please download data files!");
        }
    }

    @Override
    public void clickPersonAction(Chapter chapter) {
        view.showToast("Action clicked: " + chapter.getName());
    }

    @Override
    public void loadMorePeople() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    view.hideLoading();
                    /*Chapter chapter = new Chapter(UUID.randomUUID().toString());
                    chapter.setName(getRandomName());
                    chapter.setDescription(context.getString(R.string.fragment_people__lorem_ipsum));
                    chapterList.add(0, chapter);
                    view.showPeopleList(chapterList);*/
                }
            }
        }, 2000);
    }
}
