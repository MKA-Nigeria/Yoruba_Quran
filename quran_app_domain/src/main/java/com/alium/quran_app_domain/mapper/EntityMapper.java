package com.alium.quran_app_domain.mapper;

import com.alium.quran_app_data.model.Chapter;
import com.alium.quran_app_domain.entities.ChapterEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aliumujib on 28/03/2018.
 */

public class EntityMapper {

    public static ChapterEntity mapFrom(Chapter chapter) {
        return new ChapterEntity(chapter.getName(), chapter.getDescription(), chapter.getIndexID(),
                chapter.isMeccan(), chapter.getVerseCount(), chapter.isChapterIsSplitFile());
    }


    public static List<ChapterEntity> mapFrom(List<Chapter> chapters) {
        List<ChapterEntity> chapterEntities = new ArrayList<>();
        for (Chapter chapter : chapters) {
            chapterEntities.add(EntityMapper.mapFrom(chapter));
        }
        return chapterEntities;
    }

}
