package ru.ruranobe.wicket.webpages.special;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.basic.Label;
import ru.ruranobe.engine.wiki.parser.ChapterTextParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.sidebar.ActionsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.RequisitesSidebarModule;
import ru.ruranobe.wicket.webpages.admin.Editor;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

public class AboutUs extends SidebarLayoutPage
{
    public AboutUs()
    {
        setStatelessHint(true);

        String textHtml = "";

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        Chapter chapter;
        try (SqlSession session = sessionFactory.openSession())
        {
            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            chapter = chaptersMapperCacheable.getChapterByUrl("system/aboutus/text");

            redirectTo404IfArgumentIsNull(chapter);

            boolean committingNeeded = ChapterTextParser.getChapterText(chapter, session, false);
            textHtml = chapter.getText().getTextHtml();

            if (committingNeeded)
            {
                session.commit();
            }
        }

        add(new Label("htmlText", textHtml).setEscapeModelStrings(false));

        sidebarModules.add(new ActionsSidebarModule(Editor.class, chapter.getUrlParameters()));
        sidebarModules.add(new RequisitesSidebarModule());
        sidebarModules.add(new ProjectsSidebarModule());
        sidebarModules.add(new FriendsSidebarModule());
    }

    @Override
    protected String getPageTitle()
    {
        return "О нас - РуРанобэ";
    }
}
