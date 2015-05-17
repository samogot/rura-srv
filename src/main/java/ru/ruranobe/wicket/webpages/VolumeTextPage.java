package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.Chapter;
import ru.ruranobe.mybatis.tables.Text;
import ru.ruranobe.mybatis.tables.Volume;
import ru.ruranobe.wicket.components.CommentsPanel;
import ru.ruranobe.wicket.components.ContentsHolder;
import ru.ruranobe.wicket.components.sidebar.ContentsModule;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.UpdatesSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.ArrayList;
import java.util.List;

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
        String volumeFullUrl = projectUrlValue + "/" + volumeUrlValue;


        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        ChaptersMapper chaptersMapper = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
        Chapter chapter = chaptersMapper.getChapterByUrl(chapterFullUrl);
        if (chapter == null)
        {
            throw REDIRECT_TO_404;
        }
        if (chapter.getTextId() == null)
        {
            throw REDIRECT_TO_404;
        }
        TextsMapper textsMapper = session.getMapper(TextsMapper.class);
        Text chapterText = textsMapper.getTextById(chapter.getTextId());
        //if(chapterText.getTextHtml()==null)
        chapterText.setTextHtml(WikiParser.parseText(chapterText.getTextWiki()));


        VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
        Volume volume = volumesMapperCacheable.getVolumeByUrl(volumeFullUrl);
        List<Chapter> chapterList = chaptersMapper.getChaptersByVolumeId(volume.getVolumeId());
        List<ContentsHolder> contentsHolders = new ArrayList<ContentsHolder>();
        Chapter prevChapter = null, nextChapter = null;

        for (int i = 0; i < chapterList.size(); i++)
        {
            Chapter curChapter = chapterList.get(i);
            ContentsHolder ch;
            if (chapterFullUrl.equals(curChapter.getUrl()))
            {
                if (i - 1 >= 0 && (!curChapter.isNested() || chapterList.get(i - 1).isNested()))
                {
                    prevChapter = chapterList.get(i - 1);
                }
                else if (i - 2 >= 0)
                {
                    prevChapter = chapterList.get(i - 2);
                }
                if (i + 1 < chapterList.size())
                {
                    nextChapter = chapterList.get(i + 1);
                }
                ch = new ContentsHolder("#" + chapterUrlValue, curChapter.getTitle());
            }
            else
            {
                ch = new ContentsHolder(urlFor(VolumeTextPage.class, curChapter.getUrlParameters()).toString(),
                                        curChapter.getTitle());
            }
            if (curChapter.isNested() && !contentsHolders.isEmpty())
            {
                contentsHolders.get(contentsHolders.size() - 1).addChild(ch);
            }
            else
            {
                contentsHolders.add(ch);
            }
        }
        contentsHolders.add(new ContentsHolder("#comments", "Комментарии"));

        textPageUtils.setVisible(true);
        textPageUtils.add(homeTextLink = new BookmarkablePageLink("homeTextLink", VolumePage.class, volume.getUrlParameters()));
        if (nextChapter != null)
        {
            textPageUtils.add(nextTextLink = new BookmarkablePageLink("nextTextLink", VolumeTextPage.class, nextChapter.getUrlParameters()));
        }
        if (prevChapter != null)
        {
            textPageUtils.add(prevTextLink = new BookmarkablePageLink("prevTextLink", VolumeTextPage.class, prevChapter.getUrlParameters()));
        }


        Label textHandler = new Label("html", chapterText.getTextHtml());
        textHandler.setEscapeModelStrings(false);
        add(textHandler);

        add(new CommentsPanel("comments"));

        sidebarModules.add(new UpdatesSidebarModule("sidebarModule", volume.getProjectId()));
        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
        sidebarModules.add(new ContentsModule("sidebarModule", contentsHolders));


    }
}
