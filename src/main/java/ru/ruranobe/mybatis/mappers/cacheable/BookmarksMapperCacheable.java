package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.BookmarksMapper;
import ru.ruranobe.mybatis.entities.tables.Bookmark;

import java.util.List;

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

    @Override
    public List<Bookmark> getBookmarksByUser(Integer userId) {
        return mapper.getBookmarksByUser(userId);
    }

    @Override
    public void deleteBookmark(Integer bookmarkId) {
        mapper.deleteBookmark(bookmarkId);
    }
}
