package ru.ruranobe.mybatis.mappers.cacheable;

import java.util.List;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.tables.Chapter;

public class ChaptersMapperCacheable implements ChaptersMapper
{

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

    private ChaptersMapper mapper;
}
