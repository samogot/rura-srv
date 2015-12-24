package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.ChapterImage;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.mybatis.mappers.ChapterImagesMapper;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.sidebar.ActionsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Contact extends SidebarLayoutPage
{
    public Contact()
    {
        setStatelessHint(true);

        String textHtml = "";

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        Chapter chapter;
        try
        {
            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            chapter = chaptersMapperCacheable.getChapterByUrl("system/contact/text");

            if (chapter == null)
            {
                throw RuranobeUtils.getRedirectTo404Exception(this);
            }

            TextsMapper textsMapperCacheable = CachingFacade.getCacheableMapper(session, TextsMapper.class);
            ChapterImagesMapper chapterImagesMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterImagesMapper.class);
            boolean committionNeeded = false;

            Integer textId = chapter.getTextId();
            ru.ruranobe.mybatis.entities.tables.Text chapterText = null;
            if (textId != null)
            {
                chapterText = textsMapperCacheable.getHtmlInfoById(textId);
                textHtml = chapterText.getTextHtml();
            }

            if (Strings.isEmpty(textHtml) && textId != null)
            {
                committionNeeded = true;

                chapterText = textsMapperCacheable.getTextById(textId);
                List<ChapterImage> chapterImages = chapterImagesMapperCacheable.getChapterImagesByChapterId(chapter.getChapterId());

                List<Map.Entry<Integer, String>> images = new ArrayList<Map.Entry<Integer, String>>();
                for (ChapterImage chapterImage : chapterImages)
                {
                    Map.Entry<Integer, String> image = new AbstractMap.SimpleEntry<Integer, String>(-1, "unknownSource");
                    ExternalResource coloredImage = chapterImage.getColoredImage();
                    if (coloredImage != null && !Strings.isEmpty(coloredImage.getUrl()))
                    {
                        image = new AbstractMap.SimpleEntry<Integer, String>
                                (
                                        coloredImage.getResourceId(),
                                        coloredImage.getUrl()
                                );
                    }
                    else
                    {
                        ExternalResource nonColoredImage = chapterImage.getNonColoredImage();
                        if (nonColoredImage != null && !Strings.isEmpty(nonColoredImage.getUrl()))
                        {
                            image = new AbstractMap.SimpleEntry<Integer, String>
                                    (
                                            nonColoredImage.getResourceId(),
                                            nonColoredImage.getUrl()
                                    );
                        }
                    }
                    images.add(image);
                }

                WikiParser wikiParser = new WikiParser(chapterText.getTextId(), chapter.getChapterId(), chapterText.getTextWiki());
                chapterText.setTextHtml(wikiParser.parseWikiText(images, true));

                textsMapperCacheable.updateText(chapterText);

                textHtml = chapterText.getTextHtml();
            }
            chapter.setText(chapterText);

            if (committionNeeded)
            {
                session.commit();
            }
        }
        finally
        {
            session.close();
        }

        add(new Label("htmlText", textHtml).setEscapeModelStrings(false));

        sidebarModules.add(new ActionsSidebarModule("sidebarModule", Editor.class, chapter.getUrlParameters()));
        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
    }

    @Override
    protected String getPageTitle()
    {
        return "Связь - РуРанобе";
    }
}