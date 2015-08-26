package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.TextsHistoryMapper;
import ru.ruranobe.mybatis.tables.TextHistory;

public class TextsHistoryMapperCacheable implements TextsHistoryMapper
{

    public TextsHistoryMapperCacheable(TextsHistoryMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public void insertTextHistory(TextHistory textHistory)
    {
        mapper.insertTextHistory(textHistory);
    }

    TextsHistoryMapper mapper;
}
