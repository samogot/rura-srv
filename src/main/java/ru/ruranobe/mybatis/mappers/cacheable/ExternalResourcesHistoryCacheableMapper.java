package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.entities.tables.ExternalResourceHistory;
import ru.ruranobe.mybatis.mappers.ExternalResourcesHistoryMapper;

public class ExternalResourcesHistoryCacheableMapper implements ExternalResourcesHistoryMapper
{

    private final ExternalResourcesHistoryMapper mapper;

    public ExternalResourcesHistoryCacheableMapper(ExternalResourcesHistoryMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public int insertExternalResourceHistory(ExternalResourceHistory externalResourceHistory)
    {
        return mapper.insertExternalResourceHistory(externalResourceHistory);
    }

    @Override
    public void updateExternalResourceHistory(ExternalResourceHistory externalResourceHistory)
    {
        mapper.updateExternalResourceHistory(externalResourceHistory);
    }
}
