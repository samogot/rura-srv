package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.*;
import ru.ruranobe.mybatis.entities.tables.Text;
import ru.ruranobe.mybatis.mappers.*;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.ArrayList;
import java.util.List;

public class Recruit extends SidebarLayoutPage
{
    public Recruit()
    {
        setStatelessHint(true);

        String textHtml = "";

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            Chapter chapter = chaptersMapperCacheable.getChapterByUrl("system/recruit/text");

            if (chapter == null)
            {
                throw RuranobeUtils.getRedirectTo404Exception(this);
            }

            TextsMapper textsMapperCacheable = CachingFacade.getCacheableMapper(session, TextsMapper.class);
            ChapterImagesMapper chapterImagesMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterImagesMapper.class);
            boolean committionNeeded = false;

            Integer textId = chapter.getTextId();
            Text chapterText = null;
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

                List<String> imageUrls = new ArrayList<String>();
                for (ChapterImage chapterImage : chapterImages)
                {
                    String imageUrl = "unknownSource";
                    ExternalResource coloredImage = chapterImage.getColoredImage();
                    if (coloredImage != null && !Strings.isEmpty(coloredImage.getUrl()))
                    {
                        imageUrl = coloredImage.getUrl();
                    }
                    else
                    {
                        ExternalResource nonColoredImage = chapterImage.getNonColoredImage();
                        if (nonColoredImage != null && !Strings.isEmpty(nonColoredImage.getUrl()))
                        {
                            imageUrl = nonColoredImage.getUrl();
                        }
                    }
                    imageUrls.add(imageUrl);
                }

                WikiParser wikiParser = new WikiParser(chapterText.getTextId(), chapter.getChapterId(), chapterText.getTextWiki());
                chapterText.setTextHtml(wikiParser.parseWikiText(imageUrls, true));

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

        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
    }

    @Override
    protected String getPageTitle()
    {
        return "Набор в команду - РуРанобе";
    }
}