package ru.ruranobe.wicket.resources.rest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;
import ru.ruranobe.misc.ParagraphService;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Bookmark;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.Paragraph;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.BookmarksMapper;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.ParagraphsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;

import java.util.Date;

@ResourcePath("/api/bookmarks")
public class BookmarksRestWebService extends GsonRestResource
{
    @MethodMapping(value = "", httpMethod = HttpMethod.POST)
    public void insertBookmark(@RequestBody Bookmark bookmark)
    {
        if (bookmark.getUserId() == null)
        {
            User user = ((LoginSession) LoginSession.get()).getUser();
            if (user != null)
            {
                bookmark.setUserId(user.getUserId());
            }
            else
            {
                throw new IllegalArgumentException("userId wasn't specified.");
            }
        }

        if (bookmark.getParagraphId() == null)
        {
            throw new IllegalArgumentException("paragraphId wasn't specified.");
        }

        if (bookmark.getChapterId() == null)
        {
            throw new IllegalArgumentException("chapterId wasn't specified.");
        }

        if (bookmark.getTextId() == null)
        {
            throw new IllegalArgumentException("textId wasn't specified.");
        }

        if (Strings.isEmpty(bookmark.getFullText()))
        {
            throw new IllegalArgumentException("fullText wasn't specified.");
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

            ParagraphsMapper paragraphsMapperCacheable = CachingFacade.getCacheableMapper(session, ParagraphsMapper.class);
            Paragraph paragraph = new Paragraph();
            paragraph.setParagraphId(bookmark.getParagraphId());
            paragraph.setParagraphText(bookmark.getFullText());
            paragraph.setTextId(bookmark.getTextId());

            ParagraphService.databaseHandleParagraph(paragraphsMapperCacheable,paragraph,bookmark.getParagraphId());

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
