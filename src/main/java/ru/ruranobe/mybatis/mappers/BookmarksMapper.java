package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.Bookmark;

import java.util.List;

public interface BookmarksMapper
{
    public void insertBookmark(Bookmark bookmark);
    public List<Bookmark> getBookmarksByUser(Integer userId);
    public List<Bookmark> getBookmarksExtendedByUser(Integer userId);
    public void deleteBookmark(Integer bookmarkId);
}
