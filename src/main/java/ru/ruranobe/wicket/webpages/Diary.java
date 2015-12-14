package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.*;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.entities.tables.Text;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.ContentsHolder;
import ru.ruranobe.wicket.components.sidebar.ContentsModule;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.ArrayList;
import java.util.List;

public class Diary extends SidebarLayoutPage
{
    public Diary()
    {
        setStatelessHint(true);

        ExternalResource diaryImageResource = null;
        StringBuilder diaryText = new StringBuilder();
        List<ContentsHolder> contentsHolders = new ArrayList<ContentsHolder>();

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            Volume diaryVolume = volumesMapperCacheable.getVolumeByUrl("system/diary");
            if (diaryVolume == null)
            {
                throw RuranobeUtils.getRedirectTo404Exception(this);
            }

            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                    getCacheableMapper(session, ExternalResourcesMapper.class);
            diaryImageResource = externalResourcesMapperCacheable.getExternalResourceById(diaryVolume.getImageOne());

            ChaptersMapper chaptersMapper = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            List<Chapter> diaryChapters = chaptersMapper.getChaptersByVolumeId(diaryVolume.getVolumeId());

            TextsMapper textsMapper = CachingFacade.getCacheableMapper(session, TextsMapper.class);
            for (Chapter diaryChapter: diaryChapters)
            {
                Integer textId = diaryChapter.getTextId();
                if (textId != null)
                {
                    Text text = textsMapper.getHtmlInfoById(diaryChapter.getTextId());
                    if (Strings.isEmpty(text.getTextHtml()))
                    {
                        WikiParser wikiParser = new WikiParser(textId, diaryChapter.getChapterId(), text.getTextWiki());
                        text.setTextHtml(wikiParser.parseWikiText(new ArrayList<String>(), true));
                        textsMapper.updateText(text);
                    }

                    String title = "<h2 id=\"" + diaryChapter.getUrlPart() + "\">" + diaryChapter.getTitle() + "</h2>";
                    diaryText.append(title);
                    diaryText.append(text.getTextHtml());

                    String chapterLink = "#" + diaryChapter.getUrlPart();
                    ContentsHolder holder = new ContentsHolder(chapterLink, diaryChapter.getTitle());
                    contentsHolders.add(holder);
                }
            }
        }
        finally
        {
            session.close();
        }

        WebMarkupContainer diaryImage = new WebMarkupContainer("diaryImage");
        if (diaryImageResource != null)
        {
            diaryImage.add(new AttributeModifier("src", diaryImageResource.getUrl()));
        }
        add(diaryImage);
        add(new Label("htmlText", diaryText.toString()).setEscapeModelStrings(false));

        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
        sidebarModules.add(new ContentsModule("sidebarModule", contentsHolders));
    }

	@Override
	protected String getPageTitle() {
		return "Дневник Руйки - РуРанобе";
	}
}
