package ru.ruranobe.wicket.webpages.special;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import ru.ruranobe.engine.wiki.parser.ChapterTextParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.CommentsPanel;
import ru.ruranobe.wicket.components.sidebar.ActionsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.admin.Editor;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

public class Recruit extends SidebarLayoutPage
{
    public Recruit()
    {
        setStatelessHint(true);

        String textHtml = "";

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        Volume recruitVolume;
        Chapter chapter;
        try (SqlSession session = sessionFactory.openSession())
        {
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            recruitVolume = volumesMapperCacheable.getVolumeByUrl("system/recruit");
            redirectTo404IfArgumentIsNull(recruitVolume);

            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            chapter = chaptersMapperCacheable.getChapterByUrl("system/recruit/text");

            redirectTo404IfArgumentIsNull(chapter);

            boolean committingNeeded = ChapterTextParser.getChapterText(chapter, session, false);
            textHtml = chapter.getText().getTextHtml();

            if (committingNeeded)
            {
                session.commit();
            }
        }

        add(new Label("htmlText", textHtml).setEscapeModelStrings(false));
        if (recruitVolume.getTopicId() != null)
        {
            add(new CommentsPanel("comments", recruitVolume.getTopicId()));
        }
        else
        {
            add(new WebMarkupContainer("comments"));
        }

        sidebarModules.add(new ActionsSidebarModule(Editor.class, chapter.getUrlParameters()));
        sidebarModules.add(new ProjectsSidebarModule());
        sidebarModules.add(new FriendsSidebarModule());
    }

    @Override
    protected String getPageTitle()
    {
        return "Набор в команду - РуРанобэ";
    }
}