package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.OrphusComment;
import ru.ruranobe.mybatis.mappers.OrphusCommentsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.webpages.base.BaseLayoutPage;

import java.text.SimpleDateFormat;
import java.util.List;

public class Orphus extends BaseLayoutPage
{
    public Orphus()
    {
        setStatelessHint(true);

        List<OrphusComment> orphusComments;
        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        try
        {
            OrphusCommentsMapper orphusCommentsMapperCacheable = CachingFacade.getCacheableMapper(session, OrphusCommentsMapper.class);
            orphusComments = orphusCommentsMapperCacheable.getLastOrphusCommentsBy("desc", 0, 100);
        }
        finally
        {
            session.close();
        }

        ListView<OrphusComment> orphusRepeater = new ListView<OrphusComment>("orphusRepeater", orphusComments)
        {
            @Override
            protected void populateItem(ListItem<OrphusComment> item)
            {
                final OrphusComment orphusComment = item.getModelObject();

                Label orphusDate = new Label("orphusDate", new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
                        return sdf.format(orphusComment.getCreatedWhen());
                    }
                });
                item.add(orphusDate);

                Label orphusUsername = new Label("orphusUsername", new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        return orphusComment.getUsername();
                    }
                });
                item.add(orphusUsername);

                Label orphusOriginalText = new Label("orphusOriginalText", new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        String comment = orphusComment.getParagrap().getParagraphText();
                        StringBuilder result = new StringBuilder();
                        result.append(comment.substring(0, orphusComment.getStartOffset()));
                        result.append("<span class=\"orphusMark\">");
                        result.append(orphusComment.getOriginalText());
                        result.append("</span>");
                        result.append(comment.substring(orphusComment.getStartOffset() + orphusComment.getOriginalText().length(),
                                comment.length()));
                        return result.toString();
                    }
                });
                orphusOriginalText.setEscapeModelStrings(false);
                item.add(orphusOriginalText);

                Label orphusReplacementText = new Label("orphusReplacementText", new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        String comment = orphusComment.getParagrap().getParagraphText();
                        StringBuilder result = new StringBuilder();
                        result.append(comment.substring(0, orphusComment.getStartOffset()));
                        result.append("<span class=\"orphusMark\">");
                        result.append(orphusComment.getReplacementText());
                        result.append("</span>");
                        result.append(comment.substring(orphusComment.getStartOffset() + orphusComment.getOriginalText().length(),
                                comment.length()));
                        return result.toString();
                    }
                });
                orphusReplacementText.setEscapeModelStrings(false);

                item.add(orphusReplacementText);
                Label orphusOptionalComment = new Label("orphusOptionalComment", new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        return orphusComment.getOptionalComment();
                    }
                });

                item.add(orphusOptionalComment);

                PageParameters chapterPageParameters = Chapter.makeUrlParameters(orphusComment.getChapterUrl().split("/"));
                BookmarkablePageLink orphusChapterUrl = new BookmarkablePageLink("orphusChapterUrl", Text.class, chapterPageParameters);
                Label orphusChapterName = new Label("orphusChapterName", orphusComment.getChapterName());
                orphusChapterName.setRenderBodyOnly(true);
                orphusChapterUrl.add(orphusChapterName);

                item.add(orphusChapterUrl);
            }
        };

        add(orphusRepeater);
    }
}
