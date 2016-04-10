package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.Chapter;

import java.util.List;

public interface ChaptersMapper
{
    Chapter getChapterById(int chapterId);

    Chapter getChapterByUrl(String url);

    Chapter getChapterNextPrevByUrl(String url);

    List<Chapter> getChaptersByVolumeId(Integer volumeId);

    void insertChapter(Chapter chapter);

    void deleteChapter(int activityId);

    void updateChapter(Chapter chapter);

    void updateChapterText(Chapter chapter);

    void updateChapterNoText(Chapter chapter);
}
