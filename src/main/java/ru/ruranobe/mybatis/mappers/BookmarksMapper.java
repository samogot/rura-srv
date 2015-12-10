package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.Bookmark;

import java.util.List;

public interface BookmarksMapper
{
    void insertBookmark(Bookmark bookmark);

    List<Bookmark> getBookmarksByUser(Integer userId);

    List<Bookmark> getBookmarksExtendedByUser(Integer userId);

    void deleteBookmark(Integer bookmarkId);
}
