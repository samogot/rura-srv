package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.ChapterImage;

import java.util.List;

public interface ChapterImagesMapper
{
    public List<ChapterImage> getChapterImagesByVolumeId(Integer volumeId);

    public void insertChapterImage(ChapterImage chapterImage);

    public void deleteChapterImage(int chapterImageId);

    public void updateChapterImage(ChapterImage chapterImage);
}
