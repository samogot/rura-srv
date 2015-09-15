package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.entities.tables.OrphusComment;
import ru.ruranobe.mybatis.mappers.OrphusCommentsMapper;

public class OrphusCommentsMapperCacheable implements OrphusCommentsMapper
{
    private OrphusCommentsMapper mapper;

    public OrphusCommentsMapperCacheable(OrphusCommentsMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public int insertOrphusComment(OrphusComment orphusComment) {
        return mapper.insertOrphusComment(orphusComment);
    }
}
