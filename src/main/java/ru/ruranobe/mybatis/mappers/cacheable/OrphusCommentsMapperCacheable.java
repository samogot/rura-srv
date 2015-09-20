package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.entities.tables.OrphusComment;
import ru.ruranobe.mybatis.mappers.OrphusCommentsMapper;

import java.util.List;

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

    @Override
    public List<OrphusComment> getLastOrphusCommentsBy(String sortingOrder, long limitFrom, long limitTo) {
        return mapper.getLastOrphusCommentsBy(sortingOrder, limitFrom, limitTo);
    }

    @Override
    public int getOrphusCommentsSize() {
        return mapper.getOrphusCommentsSize();
    }
}
