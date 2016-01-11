package ru.ruranobe.wicket.webpages.special;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.*;
import ru.ruranobe.mybatis.mappers.ChapterImagesMapper;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.CommentsPanel;
import ru.ruranobe.wicket.components.sidebar.ActionsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.admin.Editor;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            if (recruitVolume == null)
            {
                throw RuranobeUtils.getRedirectTo404Exception(this);
            }

            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            chapter = chaptersMapperCacheable.getChapterByUrl("system/recruit/text");

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

                List<Map.Entry<Integer, String>> images = new ArrayList<>();
                for (ChapterImage chapterImage : chapterImages)
                {
                    Map.Entry<Integer, String> image = new AbstractMap.SimpleEntry<>(-1, "unknownSource");
                    ExternalResource coloredImage = chapterImage.getColoredImage();
                    if (coloredImage != null && !Strings.isEmpty(coloredImage.getUrl()))
                    {
                        image = new AbstractMap.SimpleEntry<>
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
                            image = new AbstractMap.SimpleEntry<>
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