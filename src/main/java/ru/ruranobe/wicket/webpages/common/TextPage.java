package ru.ruranobe.wicket.webpages.common;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;

import ru.ruranobe.cache.Cache;
import ru.ruranobe.cache.keys.SectionProjectUrl;
import ru.ruranobe.engine.wiki.parser.ChapterTextParser;
import ru.ruranobe.engine.wiki.parser.ContentItem;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.SectionProject;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.InstantiationSecurityCheck;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.components.CommentsPanel;
import ru.ruranobe.wicket.components.ContentsHolder;
import ru.ruranobe.wicket.components.sidebar.*;
import ru.ruranobe.wicket.webpages.admin.Editor;
import ru.ruranobe.wicket.webpages.admin.VolumeEdit;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TextPage extends SidebarLayoutPage implements InstantiationSecurityCheck
{
    protected String titleName;
    private Chapter chapter;

    public TextPage(PageParameters parameters)
    {
        setStatelessHint(true);

        String projectUrl = parameters.get("project").toString();
        redirectTo404(Strings.isEmpty(projectUrl));

        String volumeUrl = parameters.get("volume").toString();
        redirectTo404(Strings.isEmpty(volumeUrl));

        SectionProject sectionProject = Cache.SECTION_PROJECTS_BY_URL.get(new SectionProjectUrl(sectionId, projectUrl));

        redirectTo404IfArgumentIsNull(sectionProject);

        Project project = Cache.PROJECTS.get(sectionProject.getProjectId());

        redirectTo404IfArgumentIsNull(project);

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        StringBuilder volumeText = new StringBuilder();
        StringBuilder volumeFootnotes = new StringBuilder();
        Chapter currentChapter = null;
        Volume volume;
        List<Chapter> allChapterList;

        try (SqlSession session = sessionFactory.openSession())
        {
            if (!sectionProject.getMain())
            {
                addBodyClassAttribute("works");
            }

            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            volume = volumesMapperCacheable.getVolumeByUrl(projectUrl + "/" + volumeUrl);

            redirectTo404IfArgumentIsNull(volume);
            redirectTo404((sectionProject.getProjectHidden() && sectionProject.getMain()
                           || volume.getVolumeStatus().equals(RuraConstants.VOLUME_STATUS_HIDDEN))
                          && !LoginSession.get().isProjectEditAllowedByUser(projectUrl));

            titleName = volume.getNameTitle();

            allChapterList = chaptersMapperCacheable.getChaptersByVolumeId(volume.getVolumeId());

            String chapterUrl = parameters.get("chapter").toString();
            boolean preferNested = LoginSession.get().getUser() != null &&
                                   Objects.equals(LoginSession.get().getUser().getNavigationType(), "Подглавам");
            if (Strings.isEmpty(chapterUrl))
            {
                for (Chapter chapter : allChapterList)
                {
                    if (allChapterList.size() == 1)
                    {
                        this.chapter = chapter;
                        doInstantiationSecurityCheck();
                        chapter.setVisibleOnPage(true);
                    }
                    else if (chapter.isPublished() || LoginSession.get().isProjectShowHiddenAllowedByUser(projectUrl))
                    {
                        chapter.setVisibleOnPage(true);
                    }
                }
            }
            else
            {
                Chapter parent = null;
                Chapter prevNested = null;
                String curUrl = projectUrl + "/" + volumeUrl + "/" + chapterUrl;
                for (Chapter chapter : allChapterList)
                {
                    if (chapter.getUrl().equals(curUrl))
                    {
                        currentChapter = chapter;
                        this.chapter = currentChapter;
                        doInstantiationSecurityCheck();
                    }
                    if (chapter.isNested())
                    {
                        if (parent != null)
                        {
                            chapter.setParentChapter(parent);
                            parent.addChildChapter(chapter);
                        }
                        if (prevNested != null)
                        {
                            prevNested.setNextChapter(chapter);
                            chapter.setPrevChapter(prevNested);
                        }
                        else if (parent != null)
                        {
                            chapter.setPrevChapter(parent.getPrevChapter());
                            if (preferNested)
                            {
                                parent.getPrevChapter().setNextChapter(chapter);
                            }
                        }
                        prevNested = chapter;
                    }
                    else
                    {
                        if (parent != null)
                        {
                            parent.setNextChapter(chapter);
                            chapter.setPrevChapter(parent);
                        }
                        if (prevNested != null)
                        {
                            if (prevNested.getParentChapter() != parent)
                            {
                                prevNested = null;
                            }
                            else
                            {
                                prevNested.setNextChapter(chapter);
                                if (preferNested)
                                {
                                    chapter.setPrevChapter(prevNested);
                                }
                            }

                        }
                        parent = chapter;
                    }
                }

                redirectTo404IfArgumentIsNull(currentChapter);

                //noinspection ConstantConditions
                currentChapter.setVisibleOnPage(true);
                if (currentChapter.hasChildChapters())
                {
                    for (Chapter chapter : currentChapter.getChildChapters())
                    {
                        chapter.setVisibleOnPage(true);
                    }
                }
                else if (currentChapter.getParentChapter() != null && currentChapter == currentChapter.getParentChapter().getChildChapters().get(0))
                {
                    currentChapter.getParentChapter().setVisibleOnPage(true);
                }
            }

            TextsMapper textsMapper = CachingFacade.getCacheableMapper(session, TextsMapper.class);
            boolean committingNeeded = false;

            for (Chapter chapter : allChapterList)
            {
                if (chapter.isVisibleOnPage())
                {
                    committingNeeded = ChapterTextParser.getChapterText(chapter, session, textsMapper, true) || committingNeeded;
                    String chapterFootnotes = chapter.getText() != null ? chapter.getText().getFootnotes() : null;

                    String textHtml = ChapterTextParser.getChapterHeading(chapter) +
                                      (chapter.getText() != null ? chapter.getText().getTextHtml() : "");

                    ChapterTextParser.addFootnotes(volumeFootnotes, chapterFootnotes);
                    volumeText.append(textHtml);
                }
            }

            if (committingNeeded)
            {
                session.commit();
            }
        }

        ChapterTextParser.endFootnotes(volumeFootnotes);
        volumeText.append(volumeFootnotes);

        add(new Label("htmlText", volumeText.toString()).setEscapeModelStrings(false));

        List<ContentsHolder> contentsHolders = new ArrayList<>();
        for (Chapter chapter : allChapterList)
        {
            List<ContentsHolder> tempHolderList = contentsHolders;
            int level = 2;
            if (chapter.isNested() && !contentsHolders.isEmpty())
            {
                ContentsHolder lastContentsHolder = contentsHolders.get(contentsHolders.size() - 1);
                if (lastContentsHolder.getChildren() == null)
                {
                    lastContentsHolder.setChildren(new ArrayList<ContentsHolder>());
                }
                tempHolderList = lastContentsHolder.getChildren();
                level = 3;
            }
            processChapterContents(chapter, tempHolderList, level);
        }
        if (!Strings.isEmpty(volumeFootnotes))
        {
            contentsHolders.add(new ContentsHolder("#footnotes", "Примечания"));
        }
        contentsHolders.add(new ContentsHolder("#comments", "Комментарии"));


        textPageUtils.setVisible(true);
        textPageUtils.add(homeTextLink = volume.makeBookmarkablePageLink("homeTextLink"));
        if (currentChapter != null && currentChapter.getNextChapter() != null)
        {
            textPageUtils.add(nextTextLink = currentChapter.getNextChapter().makeBookmarkablePageLink("nextTextLink"));
        }
        if (currentChapter != null && currentChapter.getPrevChapter() != null)
        {
            textPageUtils.add(prevTextLink = currentChapter.getPrevChapter().makeBookmarkablePageLink("prevTextLink"));
        }

        add(new CommentsPanel("comments", volume.getTopicId()));
        sidebarModules.add(new DownloadsSidebarModule(volume.getUrlParameters()));
        if (currentChapter != null)
        {
            sidebarModules.add(new ActionsSidebarModule(Editor.class, currentChapter.getUrlParameters()));
        }
        else
        {
            sidebarModules.add(new ActionsSidebarModule(VolumeEdit.class, volume.getUrlParameters()));
        }
        sidebarModules.add(new UpdatesSidebarModule(volume.getProjectId()));
        sidebarModules.add(new RequisitesSidebarModule());
        sidebarModules.add(new ProjectsSidebarModule());
        sidebarModules.add(new FriendsSidebarModule());
        sidebarModules.add(new ContentsModule(contentsHolders));
    }

    private void processChapterContents(Chapter chapter, List<ContentsHolder> contentsHolders, int level)
    {
        String chapterLink = chapter.isVisibleOnPage() ? "#" + chapter.getUrlPart() : chapter.getBookmarkablePageUrlString(this);
        ContentsHolder holder = new ContentsHolder(chapterLink, chapter.getTitle());
        contentsHolders.add(holder);
        if (chapter.getText() != null && !Strings.isEmpty(chapter.getText().getContents()))
        {
            String[] contents = chapter.getText().getContents().split(ChapterTextParser.DELIMITER);
            List<ContentItem> chapterContents = new LinkedList<>();
            for (int i = 0; i < contents.length; i += 3)
            {
                chapterContents.add(new ContentItem(contents[i], contents[i + 1], contents[i + 2]));
            }
            processChapterTextContents(level, level, contentsHolders, chapterContents);
        }
    }

    private void processChapterTextContents(int minLevel, int prevLevel, List<ContentsHolder> contentsHolders, List<ContentItem> chapterContents)
    {
        if (chapterContents.isEmpty())
        {
            return;
        }
        ContentItem current = chapterContents.get(0);
        int curLevel = Integer.valueOf(current.getTagName().substring(1));
        if (curLevel < prevLevel && curLevel >= minLevel)
        {
            return;
        }
        else if (curLevel > prevLevel)
        {
            ContentsHolder lastContentsHolder = contentsHolders.get(contentsHolders.size() - 1);
            if (lastContentsHolder.getChildren() == null)
            {
                lastContentsHolder.setChildren(new ArrayList<ContentsHolder>());
            }
            processChapterTextContents(minLevel, curLevel, lastContentsHolder.getChildren(), chapterContents);
        }
        else
        {
            contentsHolders.add(new ContentsHolder("#h_id-" + current.getTagId(), current.getTitle()));
            chapterContents.remove(0);
        }
        processChapterTextContents(minLevel, prevLevel, contentsHolders, chapterContents);
    }

    @Override
    protected String getPageTitle()
    {
        return titleName != null ? titleName + " - РуРанобэ" : super.getPageTitle();
    }

    @Override
    public void doInstantiationSecurityCheck()
    {
        redirectTo404(!chapter.isPublished() && !LoginSession.get().isProjectEditAllowedByUser(
                chapter.getUrlParameters().get("project").toString()));
    }
}
