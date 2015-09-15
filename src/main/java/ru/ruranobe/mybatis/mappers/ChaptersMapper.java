package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.Chapter;

import java.util.List;

public interface ChaptersMapper
{
    public Chapter getChapterById(int chapterId);

    public Chapter getChapterByUrl(String url);

    public Chapter getChapterNextPrevByUrl(String url);

    public List<Chapter> getChaptersByVolumeId(Integer volumeId);

    public void insertChapter(Chapter chapter);

    public void deleteChapter(int activityId);

    public void updateChapter(Chapter chapter);
}
