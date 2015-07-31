package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.ChapterImagesMapper;
import ru.ruranobe.mybatis.tables.ChapterImage;

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
}
