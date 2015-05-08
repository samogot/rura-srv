package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.ChapterImage;

import java.util.List;

public interface ChapterImagesMapper
{
    public List<ChapterImage> getChapterImagesByVolumeId(Integer volumeId);
}
