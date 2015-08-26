package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.BookmarksMapper;
import ru.ruranobe.mybatis.tables.Bookmark;

public class BookmarksMapperCacheable implements BookmarksMapper
{

    private BookmarksMapper mapper;

    public BookmarksMapperCacheable(BookmarksMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public void insertBookmark(Bookmark bookmark)
    {
        mapper.insertBookmark(bookmark);
    }
}
