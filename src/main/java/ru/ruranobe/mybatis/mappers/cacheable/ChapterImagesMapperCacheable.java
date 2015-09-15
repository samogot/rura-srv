package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.ChapterImagesMapper;
import ru.ruranobe.mybatis.entities.tables.ChapterImage;

import java.util.List;

public class ChapterImagesMapperCacheable implements ChapterImagesMapper
{
    private ChapterImagesMapper mapper;

    public ChapterImagesMapperCacheable(ChapterImagesMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public List<ChapterImage> getChapterImagesByVolumeId(Integer volumeId)
    {
        return mapper.getChapterImagesByVolumeId(volumeId);
    }

    @Override
    public List<ChapterImage> getChapterImagesByChapterId(Integer chapterId)
    {
        return mapper.getChapterImagesByChapterId(chapterId);
    }

    @Override
    public void insertChapterImage(ChapterImage chapterImage)
    {
        mapper.insertChapterImage(chapterImage);
    }

    @Override
    public void deleteChapterImage(int chapterImageId)
    {
        mapper.deleteChapterImage(chapterImageId);
    }

    @Override
    public void updateChapterImage(ChapterImage chapterImage)
    {
        mapper.updateChapterImage(chapterImage);
    }
}
