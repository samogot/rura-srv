package ru.ruranobe.wicket.resources;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.OrphusCommentsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.OrphusComment;
import ru.ruranobe.wicket.LoginSession;

import java.util.Date;

public class OrphusRestWebService extends GsonRestResource
{
    @MethodMapping(value = "/insert", httpMethod = HttpMethod.POST)
    public void insertOrphusComment(@RequestBody OrphusComment orphusComment)
    {
        if (orphusComment.getUserId() == null)
        {
            User user = ((LoginSession) LoginSession.get()).getUser();
            if (user != null)
            {
                orphusComment.setUserId(user.getUserId());
            }
        }

        if (orphusComment.getParagraph() == null)
        {
            throw new IllegalArgumentException("paragraph wasn't specified.");
        }

        if (orphusComment.getChapterId() == null)
        {
            throw new IllegalArgumentException("chapterId wasn't specified.");
        }

        if (orphusComment.getStartOffset() == null)
        {
            throw new IllegalArgumentException("startOffset wasn't specified.");
        }

        if (orphusComment.getOriginalText() == null)
        {
            throw new IllegalArgumentException("originalText wasn't specified.");
        }

        if (orphusComment.getReplacementText() == null)
        {
            throw new IllegalArgumentException("replacementText wasn't specified.");
        }

        if ("".equals(orphusComment.getOptionalComment()))
        {
            orphusComment.setOptionalComment(null);
        }

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            Chapter chapter = chaptersMapperCacheable.getChapterById(orphusComment.getChapterId());
            if (chapter == null)
            {
                throw new IllegalArgumentException("Chapter with the specified chapterId doesn't exist.");
            }

            OrphusCommentsMapper orphusCommentsMapperCacheable = CachingFacade.getCacheableMapper(session, OrphusCommentsMapper.class);
            orphusComment.setCreatedWhen(new Date());
            orphusCommentsMapperCacheable.insertOrphusComment(orphusComment);
            session.commit();
        }
        finally
        {
            session.close();
        }

    }
}
