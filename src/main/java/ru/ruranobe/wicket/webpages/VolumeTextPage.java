package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.tables.Chapter;
import ru.ruranobe.mybatis.tables.Text;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.UpdatesSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VolumeTextPage extends SidebarLayoutPage
{
    private static final RedirectToUrlException REDIRECT_TO_404 = new RedirectToUrlException("http://404");

    public VolumeTextPage(final PageParameters parameters)
    {
        setStatelessHint(true);
        String projectUrlValue = parameters.get("project").toString();
        String volumeUrlValue = parameters.get("volume").toString();
        String chapterUrlValue = parameters.get("chapter").toString();
        if (projectUrlValue == null || volumeUrlValue == null || chapterUrlValue == null)
        {
            throw REDIRECT_TO_404;
        }
        String chapterFullUrl = projectUrlValue + "/" + volumeUrlValue + "/" + chapterUrlValue;

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        ChaptersMapper chaptersMapper = session.getMapper(ChaptersMapper.class);
        Chapter chapter = chaptersMapper.getChapterByUrl(chapterFullUrl);
        if (chapter == null) throw REDIRECT_TO_404;
        if (chapter.getTextId() == null) throw REDIRECT_TO_404;
        TextsMapper textsMapper = session.getMapper(TextsMapper.class);
        Text chapterText = textsMapper.getTextById(chapter.getTextId());
        //if(chapterText.getTextHtml()==null)
            chapterText.setTextHtml(WikiParser.parseText(chapterText.getTextWiki()));

        Label textHandler = new Label("html", chapterText.getTextHtml());
        textHandler.setEscapeModelStrings(false);
        add(textHandler);

        textPageUtils.setVisible(true);
        //sidebarModules.add(new UpdatesSidebarModule("sidebarModule", volume.getProjectId()));
        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));


    }
}
