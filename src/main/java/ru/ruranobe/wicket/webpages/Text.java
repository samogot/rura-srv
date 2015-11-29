package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.ContentItem;
import ru.ruranobe.engine.wiki.parser.FootnoteItem;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.ChapterImage;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.ChapterImagesMapper;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.CommentsPanel;
import ru.ruranobe.wicket.components.ContentsHolder;
import ru.ruranobe.wicket.components.sidebar.ContentsModule;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.UpdatesSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Text extends SidebarLayoutPage
{
    public static final String DELIMITER = ",;,";

    public Text(PageParameters parameters)
    {
        setStatelessHint(true);

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        StringBuilder volumeText = new StringBuilder();
        StringBuilder volumeFootnotes = new StringBuilder();
        final List<ContentItem> volumeContents = new ArrayList<ContentItem>();
        Chapter currentChapter = null;
        Volume volume = null;
        List<Chapter> allChapterList;

        try
        {
            String projectUrl = parameters.get("project").toString();
            if (Strings.isEmpty(projectUrl))
            {
                throw RuranobeUtils.getRedirectTo404Exception(this);
            }

            String volumeUrl = parameters.get("volume").toString();
            if (Strings.isEmpty(volumeUrl))
            {
                throw RuranobeUtils.getRedirectTo404Exception(this);
            }

            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            volume = volumesMapperCacheable.getVolumeByUrl(projectUrl + "/" + volumeUrl);

            if (volume == null)
            {
                throw RuranobeUtils.getRedirectTo404Exception(this);
            }

            allChapterList = chaptersMapperCacheable.getChaptersByVolumeId(volume.getVolumeId());

            String chapterUrl = parameters.get("chapter").toString();
            boolean preferNested = false;// TODO load from user preferences
            if (Strings.isEmpty(chapterUrl))
            {
                for (Chapter chapter : allChapterList)
                {
                    chapter.setVisibleOnPage(true);
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

                if (currentChapter == null)
                {
                    throw RuranobeUtils.getRedirectTo404Exception(this);
                }

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

            TextsMapper textsMapperCacheable = CachingFacade.getCacheableMapper(session, TextsMapper.class);
            ChapterImagesMapper chapterImagesMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterImagesMapper.class);
            boolean committionNeeded = false;

            for (Chapter chapter : allChapterList)
            {
                if (chapter.isVisibleOnPage())
                {
                    Integer textId = chapter.getTextId();
                    ru.ruranobe.mybatis.entities.tables.Text chapterText = null;
                    String textHtml = "";
                    String chapterFootnotes = "";
                    if (textId != null)
                    {
                        chapterText = textsMapperCacheable.getHtmlInfoById(textId);
                        textHtml = chapterText.getTextHtml();
                        chapterFootnotes = chapterText.getFootnotes();
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

                        StringBuilder contents = new StringBuilder();
                        List<ContentItem> contentList = wikiParser.getContents();
                        for (int i = 0; i < contentList.size(); ++i)
                        {
                            ContentItem contentItem = contentList.get(i);
                            String s = ((i < contentList.size() - 1) ? DELIMITER : "");
                            contents.append(contentItem.getTagName()).append(DELIMITER)
                                    .append(contentItem.getTagId()).append(DELIMITER)
                                    .append(contentItem.getTitle()).append(s);
                        }
                        chapterText.setContents(contents.toString());

                        StringBuilder footnotes = new StringBuilder();
                        List<FootnoteItem> footnoteList = wikiParser.getFootnotes();
                        for (int i = 0; i < footnoteList.size(); ++i)
                        {
                            FootnoteItem footnoteItem = footnoteList.get(i);
                            String s = ((i < footnoteList.size() - 1) ? DELIMITER : "");
                            footnotes.append(footnoteItem.getFootnoteId()).append(DELIMITER)
                                     .append(footnoteItem.getFootnoteText()).append(s);
                        }
                        chapterText.setFootnotes(footnotes.toString());

                        textsMapperCacheable.updateText(chapterText);

                        textHtml = chapterText.getTextHtml();
                        chapterFootnotes = footnotes.toString();
                    }
                    chapter.setText(chapterText);

                    String headerTag = chapter.isNested() ? "h3" : "h2";

                    textHtml = "<" + headerTag + " id=\"" + chapter.getUrlPart() + "\">" + chapter.getTitle() + "</" + headerTag + ">" + textHtml;

                    if (!Strings.isEmpty(chapterFootnotes))
                    {
                        String[] footnotes = chapterFootnotes.split(DELIMITER);
                        for (int i = 0; i < footnotes.length; i += 2)
                        {
                            volumeFootnotes.append("<li id=\"cite_note-").append(footnotes[i]).append("\">")
                                           .append("<a href=\"#cite_ref-").append(footnotes[i]).append("\">↑</a> <span class=\"reference-text\">")
                                           .append(footnotes[i + 1]).append("</span></li>");
                        }
                    }

                    volumeText.append(textHtml);
                }
            }

            if (committionNeeded)
            {
                session.commit();
            }
        }
        finally
        {
            session.close();
        }

        if (!Strings.isEmpty(volumeFootnotes))
        {
            volumeFootnotes.insert(0, "<h2 id=\"footnotes\">Примечания</h2><ol class=\"references\">");
            volumeFootnotes.append("</ol>");
            volumeText.append(volumeFootnotes);
        }


        add(new Label("htmlText", volumeText.toString()).setEscapeModelStrings(false));

        List<ContentsHolder> contentsHolders = new ArrayList<ContentsHolder>();
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
        if (volume != null)
        {
            textPageUtils.add(homeTextLink = volume.makeBookmarkablePageLink("homeTextLink"));
        }
        if (currentChapter != null && currentChapter.getNextChapter() != null)
        {
            textPageUtils.add(nextTextLink = currentChapter.getNextChapter().makeBookmarkablePageLink("nextTextLink"));
        }
        if (currentChapter != null && currentChapter.getPrevChapter() != null)
        {
            textPageUtils.add(prevTextLink = currentChapter.getPrevChapter().makeBookmarkablePageLink("prevTextLink"));
        }

        add(new CommentsPanel("comments"));
        sidebarModules.add(new UpdatesSidebarModule("sidebarModule", volume.getProjectId()));
        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
        sidebarModules.add(new ContentsModule("sidebarModule", contentsHolders));
    }

    private void processChapterContents(Chapter chapter, List<ContentsHolder> contentsHolders, int level)
    {
        String chapterLink = chapter.isVisibleOnPage() ? "#" + chapter.getUrlPart() : chapter.getBookmarkablePageUrlString(this);
        ContentsHolder holder = new ContentsHolder(chapterLink, chapter.getTitle());
        contentsHolders.add(holder);
        if (chapter.getText() != null && !Strings.isEmpty(chapter.getText().getContents()))
        {
            String[] contents = chapter.getText().getContents().split(DELIMITER);
            List<ContentItem> chapterContents = new LinkedList<ContentItem>();
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

}
