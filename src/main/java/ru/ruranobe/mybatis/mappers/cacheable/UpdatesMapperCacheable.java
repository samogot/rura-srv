package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.UpdatesMapper;
import ru.ruranobe.mybatis.tables.Update;

import java.util.List;

/* Uncacheable!!! A cache on top of a sortedset might be implemented, but it will be unstable. */
public class UpdatesMapperCacheable implements UpdatesMapper
{

    private final UpdatesMapper mapper;

    public UpdatesMapperCacheable(UpdatesMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public List<Update> getLastUpdatesBy(Integer projectId, Integer volumeId, String updateType, Integer limitFrom, Integer limitTo)
    {
        return mapper.getLastUpdatesBy(projectId, volumeId, updateType, limitFrom, limitTo);
    }

    @Override
    public int getUpdatesCountBy(Integer projectId, Integer volumeId, String updateType)
    {
        return mapper.getUpdatesCountBy(projectId, volumeId, updateType);
    }
}
