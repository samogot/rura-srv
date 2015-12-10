package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.ChapterImage;

import java.util.List;

public interface ChapterImagesMapper
{
    List<ChapterImage> getChapterImagesByVolumeId(Integer volumeId);

    List<ChapterImage> getChapterImagesByChapterId(Integer chapterId);

    void insertChapterImage(ChapterImage chapterImage);

    void deleteChapterImage(int chapterImageId);

    void updateChapterImage(ChapterImage chapterImage);
}
