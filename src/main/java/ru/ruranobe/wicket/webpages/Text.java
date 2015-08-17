package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
import ru.ruranobe.wicket.webpages.base.TextLayoutPage;

import javax.management.Attribute;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Text extends TextLayoutPage
{
    public Text(PageParameters parameters)
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

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        StringBuilder volumeText = new StringBuilder();
        StringBuilder volumeFootnotes = new StringBuilder();
        final List<ContentItem> volumeContents = new ArrayList<ContentItem>();
        try
        {
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            Volume volume = volumesMapperCacheable.getVolumeByUrl(projectUrl + "/" + volumeUrl);

            if (volume == null)
            {
                throw RuranobeUtils.REDIRECT_TO_404;
            }

            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            List<Chapter> chapterList = chaptersMapperCacheable.getChaptersByVolumeId(volume.getVolumeId());

            TextsMapper textsMapperCacheable = CachingFacade.getCacheableMapper(session, TextsMapper.class);
            ChapterImagesMapper chapterImagesMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterImagesMapper.class);
            boolean committionNeeded = false;

            for (Chapter chapter : chapterList)
            {
                Integer textId = chapter.getTextId();
                ru.ruranobe.mybatis.tables.Text chapterText = textsMapperCacheable.getHtmlInfoById(textId);
                String textHtml = chapterText.getTextHtml();
                String chapterFootnotes = chapterText.getFootnotes();
                String chapterContents = chapterText.getContents();

                if (Strings.isEmpty(textHtml))
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
                        contents.append(contentItem.getTagName()).append(",")
                                .append(contentItem.getTagId()).append(",")
                                .append(contentItem.getTitle()).append(i < contentList.size() ? "," : "");
                    }
                    chapterText.setContents(contents.toString());

                    StringBuilder footnotes = new StringBuilder();
                    List<String> footnoteList = wikiParser.getFootnotes();
                    for (int i = 0; i < footnoteList.size(); ++i)
                    {
                        String footnote = footnoteList.get(i);
                        footnotes.append(footnote).append(i < footnoteList.size() ? "," : "");
                    }
                    chapterText.setFootnotes(footnotes.toString());

                    textsMapperCacheable.updateText(chapterText);

                    textHtml = chapterText.getTextHtml();
                    chapterFootnotes = footnotes.toString();
                    chapterContents = contents.toString();
                }

                if (!Strings.isEmpty(chapterFootnotes))
                {
                    String[] footnotes = chapterFootnotes.split(",");
                    for (String footnote : footnotes)
                    {
                        volumeFootnotes.append("<li>").append(footnote).append("</li>");
                    }
                }

                if (!Strings.isEmpty(chapterFootnotes))
                {
                    String[] footnotes = chapterFootnotes.split(",");
                    for (String footnote : footnotes)
                    {
                        volumeFootnotes.append("<li>").append(footnote).append("</li>");
                    }
                }

                if (!Strings.isEmpty(chapterContents))
                {
                    String[] contents = chapterContents.split(",");
                    for (int i = 0; i < contents.length;)
                    {
                        volumeContents.add(new ContentItem(contents[i], Long.valueOf(contents[i+1]), contents[i+2]));
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
            volumeFootnotes.insert(0, "<h2 id=\"comments\"><span>Примечания</span></h2><ol class=\"references\">");
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

        ListView<ContentItem> h2Repeater = new ListView<ContentItem>("h2Repeater", h2s)
        {
            @Override
            protected void populateItem(ListItem<ContentItem> item)
            {
                ContentItem contentItem = item.getModelObject();
                Label h2level = new Label("h2level",contentItem.getTitle());
                AttributeAppender href = new AttributeAppender("href", "#h_id-"+contentItem.getTagId());
                h2level.add(href);
                item.add(h2level);

                List<ContentItem> h3s = h2Toh3.get(contentItem);
                ListView<ContentItem> h3Repeater;
                h3Repeater = new ListView<ContentItem>("h3Repeater", h3s)
                {
                    @Override
                    protected void populateItem(ListItem<ContentItem> item)
                    {
                        ContentItem contentItem = item.getModelObject();
                        Label h3level = new Label("h3level",contentItem.getTitle());
                        AttributeAppender href = new AttributeAppender("href", "#h_id-"+contentItem.getTagId());
                        h3level.add(href);
                        item.add(h3level);

                        List<ContentItem> h4s = h3Toh4.get(contentItem);
                        ListView<ContentItem> h4Repeater;
                        h4Repeater = new ListView<ContentItem>("h4Repeater", h4s)
                        {
                            @Override
                            protected void populateItem(ListItem<ContentItem> item)
                            {
                                ContentItem contentItem = item.getModelObject();
                                Label h4level = new Label("h4level",contentItem.getTitle());
                                AttributeAppender href = new AttributeAppender("href", "#h_id-"+contentItem.getTagId());
                                h4level.add(href);
                                item.add(h4level);
                            }
                        };

                        item.add(h4Repeater);
                    }
                    };
                item.add(h3Repeater);
            }
        };
        add(h2Repeater);

        WebMarkupContainer footnotes = new WebMarkupContainer("footnotes");
        AttributeAppender href = new AttributeAppender("href", "#footnotes");
        footnotes.add(href);
        add(footnotes);

        WebMarkupContainer comments = new WebMarkupContainer("comments");
        href = new AttributeAppender("href", "#comments");
        comments.add(href);
        add(comments);
    }
}
