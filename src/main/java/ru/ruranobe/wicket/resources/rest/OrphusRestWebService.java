package ru.ruranobe.wicket.resources.rest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.utils.http.HttpMethod;
import ru.ruranobe.misc.ParagraphService;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.OrphusComment;
import ru.ruranobe.mybatis.entities.tables.Paragraph;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.OrphusCommentsMapper;
import ru.ruranobe.mybatis.mappers.ParagraphsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.resources.rest.base.GsonObjectRestResource;

import java.util.Date;

@ResourcePath("/api/orphus")
public class OrphusRestWebService extends GsonObjectRestResource
{
    @MethodMapping(value = "", httpMethod = HttpMethod.POST)
    public void insertOrphusComment(@RequestBody OrphusComment orphusComment)
    {
        if (orphusComment.getUserId() == null)
        {
            User user = LoginSession.get().getUser();
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

        if (Strings.isEmpty(orphusComment.getOptionalComment()))
        {
            orphusComment.setOptionalComment(null);
        }

        if (orphusComment.getTextId() == null)
        {
            throw new IllegalArgumentException("textId wasn't specified.");
        }

        if (Strings.isEmpty(orphusComment.getFullText()))
        {
            throw new IllegalArgumentException("fullText wasn't specified.");
        }

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        try (SqlSession session = sessionFactory.openSession())
        {
            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            Chapter chapter = chaptersMapperCacheable.getChapterById(orphusComment.getChapterId());
            if (chapter == null)
            {
                throw new IllegalArgumentException("Chapter with the specified chapterId doesn't exist.");
            }

            ParagraphsMapper paragraphsMapperCacheable = CachingFacade.getCacheableMapper(session, ParagraphsMapper.class);
            Paragraph paragraph = new Paragraph();
            paragraph.setParagraphId(orphusComment.getParagraph());
            paragraph.setParagraphText(orphusComment.getFullText());
            paragraph.setTextId(orphusComment.getTextId());

            ParagraphService.databaseHandleParagraph(paragraphsMapperCacheable, paragraph, orphusComment.getParagraph());

            OrphusCommentsMapper orphusCommentsMapperCacheable = CachingFacade.getCacheableMapper(session, OrphusCommentsMapper.class);
            orphusComment.setCreatedWhen(new Date());
            orphusCommentsMapperCacheable.insertOrphusComment(orphusComment);
            session.commit();
        }

    }
}
