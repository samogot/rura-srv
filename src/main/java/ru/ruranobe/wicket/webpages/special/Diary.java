package ru.ruranobe.wicket.webpages.special;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import ru.ruranobe.engine.wiki.parser.ChapterTextParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.CommentsPanel;
import ru.ruranobe.wicket.components.ContentsHolder;
import ru.ruranobe.wicket.components.sidebar.ActionsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ContentsModule;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.admin.VolumeEdit;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Diary extends SidebarLayoutPage
{
    public Diary()
    {
        setStatelessHint(true);

        final List<ContentsHolder> contentsHolders = new ArrayList<>();

        Volume diaryVolume;
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        List<Chapter> diaryChapters;
        try (SqlSession session = sessionFactory.openSession())
        {
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            diaryVolume = volumesMapperCacheable.getVolumeByUrl("system/diary");
            redirectTo404IfArgumentIsNull(diaryVolume);

            ChaptersMapper chaptersMapper = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            diaryChapters = chaptersMapper.getChaptersByVolumeId(diaryVolume.getVolumeId());

            boolean committingNeeded = false;
            TextsMapper textsMapper = CachingFacade.getCacheableMapper(session, TextsMapper.class);
            for (Chapter diaryChapter : diaryChapters)
            {
                committingNeeded = ChapterTextParser.getChapterText(diaryChapter, session, textsMapper, false) || committingNeeded;
                if (diaryChapter.getText().getTextWiki() == null)
                {
                    diaryChapter.getText().setTextWiki(textsMapper.getTextById(diaryChapter.getTextId()).getTextWiki());
                }
                ContentsHolder holder = new ContentsHolder("#" + diaryChapter.getUrlPart(), diaryChapter.getTitle());
                contentsHolders.add(holder);
            }

            if (committingNeeded)
            {
                session.commit();
            }
        }

        add(new ListView<Chapter>("commentsRepeater", diaryChapters)
        {
            @Override
            protected void populateItem(ListItem<Chapter> item)
            {
                Chapter chapter = item.getModelObject();
                item.setMarkupId(chapter.getUrlPart());
                item.add(new Label("htmlText", chapter.getText() == null ? "" : chapter.getText().getTextHtml()).setEscapeModelStrings(false));
                item.add(new Label("date", chapter.getTitle()).add(new AttributeModifier("href", "#" + chapter.getUrlPart())));
                WebMarkupContainer avatar = new WebMarkupContainer("avatar");
                if (chapter.getText() != null)
                {
                    Pattern p = Pattern.compile("^\\s*<!--\\s*img(\\d+)\\s*-->");
                    Matcher matcher = p.matcher(chapter.getText().getTextWiki());
                    if (matcher.find())
                    {
                        avatar.add(new AttributeModifier("src", "/img/journal/" + matcher.group(1) + ".png"));
                    }
                }
                item.add(avatar);
            }
        });

        if (diaryVolume.getTopicId() != null)
        {
            add(new CommentsPanel("comments", diaryVolume.getTopicId()));
            contentsHolders.add(new ContentsHolder("#comments", "Комментарии"));
        }
        else
        {
            add(new WebMarkupContainer("comments"));
        }

        sidebarModules.add(new ActionsSidebarModule(VolumeEdit.class, diaryVolume.getUrlParameters()));
        sidebarModules.add(new ProjectsSidebarModule());
        sidebarModules.add(new FriendsSidebarModule());
        sidebarModules.add(new ContentsModule(contentsHolders));
    }

    @Override
    protected String getPageTitle()
    {
        return "Дневник Руйки - РуРанобэ";
    }
}
