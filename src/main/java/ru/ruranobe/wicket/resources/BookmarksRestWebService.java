package ru.ruranobe.wicket.resources;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.BookmarksMapper;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.Bookmark;
import ru.ruranobe.mybatis.tables.Chapter;

import java.util.Date;

public class BookmarksRestWebService extends GsonRestResource
{
    @MethodMapping(value = "/insert", httpMethod = HttpMethod.POST)
    public void insertBookmark(@RequestBody Bookmark bookmark)
    {
        /*
        TODO: switched off since there is not authorization yet.
        if (bookmark.getUserId() == null)
        {
            throw new IllegalArgumentException("userId wasn't specified.");
        }*/
        if (bookmark.getParagraphId() == null)
        {
            throw new IllegalArgumentException("paragraphId wasn't specified.");
        }

        if (bookmark.getChapterId() == null)
        {
            throw new IllegalArgumentException("chapterId wasn't specified.");
        }

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            Chapter chapter = chaptersMapperCacheable.getChapterById(bookmark.getChapterId());
            if (chapter == null)
            {
                throw new IllegalArgumentException("Chapter with the specified chapterId doesn't exist.");
            }

            BookmarksMapper bookmarksMapperCacheable = CachingFacade.getCacheableMapper(session, BookmarksMapper.class);
            bookmark.setCreatedWhen(new Date());
            bookmarksMapperCacheable.insertBookmark(bookmark);
            session.commit();
        }
        finally
        {
            session.close();
        }
    }

}
