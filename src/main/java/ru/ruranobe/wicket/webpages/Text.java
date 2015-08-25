package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.ContentItem;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ChapterImagesMapper;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.Chapter;
import ru.ruranobe.mybatis.tables.ChapterImage;
import ru.ruranobe.mybatis.tables.ExternalResource;
import ru.ruranobe.mybatis.tables.Volume;
import ru.ruranobe.wicket.components.CommentsPanel;
import ru.ruranobe.wicket.components.ContentsHolder;
import ru.ruranobe.wicket.components.sidebar.ContentsModule;
import ru.ruranobe.wicket.webpages.base.TextLayoutPage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Text extends TextLayoutPage
{

    public Text(PageParameters parameters)
    {
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
                throw RuranobeUtils.REDIRECT_TO_404;
            }

            String volumeUrl = parameters.get("volume").toString();
            if (Strings.isEmpty(volumeUrl))
            {
                throw RuranobeUtils.REDIRECT_TO_404;
            }

            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            volume = volumesMapperCacheable.getVolumeByUrl(projectUrl + "/" + volumeUrl);

            if (volume == null)
            {
                throw RuranobeUtils.REDIRECT_TO_404;
            }

            allChapterList = chaptersMapperCacheable.getChaptersByVolumeId(volume.getVolumeId());

            String chapterUrl = parameters.get("chapter").toString();
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
                            prevNested.setNextChapter(chapter);
                            if (prevNested.getParentChapter() != parent)
                            {
                                prevNested = null;
                            }
                        }
                        parent = chapter;
                    }
                }

                if (currentChapter == null)
                {
                    throw RuranobeUtils.REDIRECT_TO_404;
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
                    ru.ruranobe.mybatis.tables.Text chapterText = null;
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

                        WikiParser wikiParser = new WikiParser(chapterText.getTextId(), chapterText.getTextWiki());
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
                        List<String> footnoteList = wikiParser.getFootnotes();
                        for (int i = 0; i < footnoteList.size(); ++i)
                        {
                            String footnote = footnoteList.get(i);
                            footnotes.append(footnote).append(i < footnoteList.size() - 1 ? DELIMITER : "");
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
                        for (String footnote : footnotes)
                        {
                            volumeFootnotes.append("<li>").append(footnote).append("</li>");
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
            if (chapter.getParentChapter() == null)
            {
                processChapterContents(chapter, contentsHolders, 2);
                if (chapter.getChildChapters() != null)
                {
                    for (Chapter nestedChapter : chapter.getChildChapters())
                    {
                        ContentsHolder lastContentsHolder = contentsHolders.get(contentsHolders.size() - 1);
                        if (lastContentsHolder.getChildren() == null)
                        {
                            lastContentsHolder.setChildren(new ArrayList<ContentsHolder>());
                        }
                        processChapterContents(chapter, lastContentsHolder.getChildren(), 3);
                    }
                }
            }
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
//        sidebarModules.add(new UpdatesSidebarModule("sidebarModule", volume.getProjectId()));
//        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
//        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
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
                chapterContents.add(new ContentItem(contents[i], Long.valueOf(contents[i + 1]), contents[i + 2]));
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
