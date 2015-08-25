package ru.ruranobe.wicket.webpages;

import com.google.common.collect.Lists;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.resource.CoreLibrariesContributor;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.ContentItem;
import ru.ruranobe.engine.wiki.parser.FootnoteItem;
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
import ru.ruranobe.wicket.components.ContentsHolder;
import ru.ruranobe.wicket.components.sidebar.ContentsModule;
import ru.ruranobe.wicket.webpages.base.TextLayoutPage;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Text extends TextLayoutPage
{
    public Text(PageParameters parameters)
    {
        setStatelessHint(true);

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        StringBuilder volumeText = new StringBuilder();
        StringBuilder volumeFootnotes = new StringBuilder();
        final List<ContentItem> volumeContents = new ArrayList<ContentItem>();

        try
        {
            List<Chapter> chapterList = getChaptersToDisplay(parameters, session);

            TextsMapper textsMapperCacheable = CachingFacade.getCacheableMapper(session, TextsMapper.class);
            ChapterImagesMapper chapterImagesMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterImagesMapper.class);
            boolean committionNeeded = false;

            for (Chapter chapter : chapterList)
            {
                Integer textId = chapter.getTextId();
                ru.ruranobe.mybatis.tables.Text chapterText;
                String textHtml = "";
                String chapterFootnotes = "";
                String chapterContents = "";
                if (textId != null)
                {
                    chapterText = textsMapperCacheable.getHtmlInfoById(textId);
                    textHtml = chapterText.getTextHtml();
                    chapterFootnotes = chapterText.getFootnotes();
                    chapterContents = chapterText.getContents();
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
                        String s = ((i < contentList.size()-1) ? DELIMITER : "");
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
                        String s = ((i < footnoteList.size()-1) ? DELIMITER : "");
                        footnotes.append(footnoteItem.getFootnoteId()).append(DELIMITER)
                                .append(footnoteItem.getFootnoteText()).append(s);
                    }
                    chapterText.setFootnotes(footnotes.toString());

                    textsMapperCacheable.updateText(chapterText);

                    textHtml = chapterText.getTextHtml();
                    chapterFootnotes = footnotes.toString();
                    chapterContents = contents.toString();
                }

                String headerTag = chapter.isNested() ? "h3" : "h2";

                StringBuilder chapterContentsExtended = new StringBuilder();
                String s = Strings.isEmpty(chapterContents) ? "" : DELIMITER;
                chapterContentsExtended.append(headerTag).append(DELIMITER)
                                       .append(chapter.getChapterId()).append(DELIMITER)
                                       .append(chapter.getTitle()).append(s)
                        .append(chapterContents);
                chapterContents = chapterContentsExtended.toString();

                StringBuilder textHtmlExtended = new StringBuilder();
                textHtmlExtended.append("<").append(headerTag).append(" id=\"h_id-")
                                .append(chapter.getChapterId()).append("\">")
                                .append(chapter.getTitle()).append("</")
                                .append(headerTag).append(">")
                        .append(textHtml);
                textHtml = textHtmlExtended.toString();

                if (!Strings.isEmpty(chapterFootnotes))
                {
                    String[] footnotes = chapterFootnotes.split(DELIMITER);
                    for (int i = 0; i < footnotes.length;)
                    {
                        volumeFootnotes.append("<li id=\"cite_note-").append(footnotes[i]).append("\">")
                                       .append("<a href=\"#cite_ref-").append(footnotes[i]).append("\">↑</a> <span class=\"reference-text\">")
                                       .append(footnotes[i + 1]).append("</span></li>");
                        i+=2;
                    }
                }

                if (!Strings.isEmpty(chapterContents))
                {
                    if (nested && "h3".equals(chapterContents.substring(0, 2)))
                    {
                        volumeContents.add(new ContentItem("h2", "", ""));
                    }

                    String[] contents = chapterContents.split(DELIMITER);
                    for (int i = 0; i < contents.length;)
                    {
                        volumeContents.add(new ContentItem(contents[i], contents[i+1], contents[i+2]));
                        i+=3;
                    }
                }

                volumeText.append(textHtml);
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
            volumeFootnotes.insert(0, "<h2 id=\"footnotes\"><span>Примечания</span></h2><ol class=\"references\">");
            volumeFootnotes.append("</ol>");
            volumeText.append(volumeFootnotes);
        }

        add(new Label("htmlText", volumeText.toString()).setEscapeModelStrings(false));

        List<ContentItem> h2s = new ArrayList<ContentItem>();
        final Map<ContentItem, ArrayList<ContentItem>> h2Toh3 = new HashMap<ContentItem, ArrayList<ContentItem>>();
        final Map<ContentItem, ArrayList<ContentItem>> h3Toh4 = new HashMap<ContentItem, ArrayList<ContentItem>>();

        ContentItem h2 = null;
        ArrayList<ContentItem> h3s = null;
        ArrayList<ContentItem> h4s = null;
        for (ContentItem contentItem : volumeContents)
        {
            if ("h2".equals(contentItem.getTagName()))
            {
                h2s.add(contentItem);
                if (h2 != null && h3s != null && h3s.size() > 0)
                {
                    h2Toh3.put(h2, h3s);
                    if (h4s != null && h4s.size() > 0)
                    {
                        h3Toh4.put(h3s.get(h3s.size() - 1), h4s);
                        h4s = null;
                    }
                    h3s = null;
                }
                h2 = contentItem;
            }
            else if ("h3".equals(contentItem.getTagName()) && h2 != null)
            {
                if (h3s != null && h3s.size() > 0)
                {
                    if (h4s != null && h4s.size() > 0)
                    {
                        h3Toh4.put(h3s.get(h3s.size() - 1), h4s);
                        h4s = null;
                    }
                }
                else
                {
                    h3s = new ArrayList<ContentItem>();
                }
                h3s.add(contentItem);
            }
            else if ("h4".equals(contentItem.getTagName()) && h3s != null)
            {
                if (h4s == null || h4s.size() == 0)
                {
                    h4s = new ArrayList<ContentItem>();
                }
                h4s.add(contentItem);
            }
        }

        if (h3s != null && h4s != null && !h4s.isEmpty())
        {
            h3Toh4.put(h3s.get(h3s.size() - 1), h4s);
        }

        if (h2 != null && h3s != null && !h3s.isEmpty())
        {
            h2Toh3.put(h2, h3s);
        }

        List<ContentsHolder> contentsHolders = new ArrayList<ContentsHolder>();
        for (ContentItem h2Tag : h2s)
        {
            ContentsHolder h2Content = new ContentsHolder("#h_id-" + h2Tag.getTagId(), h2Tag.getTitle());
            List<ContentItem> h3Tags = h2Toh3.get(h2Tag);
            if (h3Tags != null)
            {
                for (ContentItem h3Tag : h3Tags)
                {
                    ContentsHolder h3Content = new ContentsHolder("#h_id-" + h3Tag.getTagId(), h3Tag.getTitle());
                    h2Content.addChild(h3Content);
                    List<ContentItem> h4Tags = h3Toh4.get(h3Tag);
                    if (h4Tags != null)
                    {
                        for (ContentItem h4Tag : h4Tags)
                        {
                            ContentsHolder h4Content = new ContentsHolder("#h_id-" + h4Tag.getTagId(), h4Tag.getTitle());
                            h3Content.addChild(h4Content);
                        }
                    }
                }
            }
            contentsHolders.add(h2Content);
        }

        WebMarkupContainer nextChapter = new WebMarkupContainer("nextChapter");
        nextChapter.setVisible(!Strings.isEmpty(nextUrl));
        AttributeAppender href = new AttributeAppender("href", "../../"+nextUrl);
        nextChapter.add(href);
        add(nextChapter);

        WebMarkupContainer prevChapter = new WebMarkupContainer("prevChapter");
        prevChapter.setVisible(!Strings.isEmpty(prevUrl));
        href = new AttributeAppender("href", "../../"+prevUrl);
        prevChapter.add(href);
        add(prevChapter);

        sidebarModules.add(new ContentsModule("sidebarModule", contentsHolders));
    }

    private List<Chapter> getChaptersToDisplay(PageParameters parameters, SqlSession session)
    {
        List<Chapter> chapterList;

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

        String chapterUrl = parameters.get("chapter").toString();

        ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
        if (Strings.isEmpty(chapterUrl))
        {
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            Volume volume = volumesMapperCacheable.getVolumeByUrl(projectUrl + "/" + volumeUrl);

            if (volume == null)
            {
                throw RuranobeUtils.REDIRECT_TO_404;
            }

            chapterList = chaptersMapperCacheable.getChaptersByVolumeId(volume.getVolumeId());
        }
        else
        {
            Chapter chapter = chaptersMapperCacheable.getChapterNextPrevByUrl(projectUrl + "/" + volumeUrl + "/" + chapterUrl);

            if (chapter == null)
            {
                throw RuranobeUtils.REDIRECT_TO_404;
            }

            nested = chapter.isNested();
            prevUrl = chapter.getPrevUrl();
            nextUrl = chapter.getNextUrl();
            chapterList = Lists.newArrayList();
            if (chapter.isNested() && !chapter.isPrevChapterNested())
            {
                // show prev chapter
                chapterList.add(chaptersMapperCacheable.getChapterById(chapter.getPrevChapterId()));
            }
            chapterList.add(chapter);
        }

        return chapterList;
    }

    private boolean nested = false;
    private String nextUrl = null;
    private String prevUrl = null;
}
