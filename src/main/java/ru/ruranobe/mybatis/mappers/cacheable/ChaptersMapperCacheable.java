package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.tables.Chapter;

import java.util.List;

public class ChaptersMapperCacheable implements ChaptersMapper
{

    private ChaptersMapper mapper;

    public ChaptersMapperCacheable(ChaptersMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public Chapter getChapterByUrl(String url)
    {
        return mapper.getChapterByUrl(url);
    }

    @Override
    public List<Chapter> getChaptersByVolumeId(Integer volumeId)
    {
        return mapper.getChaptersByVolumeId(volumeId);
    }
}
