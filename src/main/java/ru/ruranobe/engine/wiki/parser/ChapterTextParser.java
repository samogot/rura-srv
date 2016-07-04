package ru.ruranobe.engine.wiki.parser;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.ChapterImage;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.mybatis.entities.tables.Text;
import ru.ruranobe.mybatis.mappers.ChapterImagesMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.RuraConstants;

import java.util.ArrayList;
import java.util.List;

public class ChapterTextParser
{
    public static List<ExternalResource> getChapterExternalResources(Chapter chapter, SqlSession session)
    {
        ChapterImagesMapper chapterImagesMapper = CachingFacade.getCacheableMapper(session, ChapterImagesMapper.class);
        List<ChapterImage> chapterImages = chapterImagesMapper.getChapterImagesByChapterId(chapter.getChapterId());

        List<ExternalResource> images = new ArrayList<>();
        for (ChapterImage chapterImage : chapterImages)
        {
            ExternalResource image;
            if (chapterImage.getColoredImage() != null
                && !Strings.isEmpty(chapterImage.getColoredImage().getUrl()))
            {
                image = chapterImage.getColoredImage();
                if (chapterImage.getNonColoredImage() != null
                    && !Strings.isEmpty(chapterImage.getNonColoredImage().getUrl()))
                {
                    image.setNonColored(chapterImage.getNonColoredImage());
                }
            }
            else if (chapterImage.getNonColoredImage() != null
                     && !Strings.isEmpty(chapterImage.getNonColoredImage().getUrl()))
            {
                image = chapterImage.getNonColoredImage();
            }
            else
            {
                image = RuraConstants.UNKNOWN_IMAGE;
            }
            images.add(image);
        }
        return images;
    }

    public static void parseChapterText(Chapter chapter, SqlSession session, TextsMapper textsMapper, boolean sanitize)
    {
        List<ExternalResource> images = ChapterTextParser.getChapterExternalResources(chapter, session);

        Text chapterText = chapter.getText();
        WikiParser wikiParser = new WikiParser(chapterText.getTextId(), chapter.getChapterId(), chapterText.getTextWiki(), sanitize);
        chapterText.setTextHtml(wikiParser.parseWikiText(images, true));

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

        textsMapper.updateText(chapterText);
    }

    public static boolean getChapterText(Chapter chapter, SqlSession session, boolean sanitize)
    {
        TextsMapper textsMapper = CachingFacade.getCacheableMapper(session, TextsMapper.class);
        return getChapterText(chapter, session, textsMapper, sanitize);
    }

    public static boolean getChapterText(Chapter chapter, SqlSession session, TextsMapper textsMapper, boolean sanitize)
    {
        boolean result = false;
        if (chapter.getTextId() != null)
        {
            chapter.setText(textsMapper.getHtmlInfoById(chapter.getTextId()));
        }
        if (chapter.getText() != null && Strings.isEmpty(chapter.getText().getTextHtml()))
        {
            chapter.setText(textsMapper.getTextById(chapter.getTextId()));
            ChapterTextParser.parseChapterText(chapter, session, textsMapper, sanitize);
            result = true;
        }
        if (chapter.getTextId() != null)
        {
            chapter.getText().setTextHtml(replaceNonColoredImages(chapter.getText().getTextHtml()));
        }
        return result;
    }

    public static String replaceNonColoredImages(String textHtml)
    {
        boolean preferColoredImgs = LoginSession.get().getUser() == null
                                    || LoginSession.get().getUser().getPreferColoredImgs() != Boolean.FALSE;
        if (!preferColoredImgs)
        {
            textHtml = textHtml.replaceAll("\"[^\"]+\" data-non-colored-href=", "");
            textHtml = textHtml.replaceAll("\"[^\"]+\" data-non-colored-src=", "");
            textHtml = textHtml.replaceAll("\"[^\"]+\" data-non-colored-resource-id=", "");
        }
        return textHtml;
    }

    public static String getChapterHeading(Chapter chapter)
    {
        String headerTag = chapter.isNested() ? "h3" : "h2";
        return "<" + headerTag + " id=\"" + chapter.getUrlPart() + "\">" + chapter.getTitle() + "</" + headerTag + ">";
    }

    private static void makeFootnote(StringBuilder builder, String id, String text)
    {
        builder.append("<li id=\"cite_note-").append(id).append("\">")
               .append("<a href=\"#cite_ref-").append(id).append("\">↑</a> <span class=\"reference-text\">")
               .append(text).append("</span></li>");
    }

    public static void addFootnotes(StringBuilder builder, List<FootnoteItem> footnotes)
    {
        for (FootnoteItem footnoteItem : footnotes)
        {
            makeFootnote(builder, footnoteItem.getFootnoteId(), footnoteItem.getFootnoteText());
        }
    }

    public static void addFootnotes(StringBuilder builder, String footnotes)
    {
        if (!Strings.isEmpty(footnotes))
        {
            String[] footnotesSplited = footnotes.split(DELIMITER);
            for (int i = 0; i < footnotesSplited.length; i += 2)
            {
                makeFootnote(builder, footnotesSplited[i], footnotesSplited[i + 1]);
            }
        }
    }

    public static void endFootnotes(StringBuilder builder)
    {
        if (!Strings.isEmpty(builder))
        {
            builder.insert(0, "<h2 id=\"footnotes\">Примечания</h2><ol class=\"references\">");
            builder.append("</ol>");
        }
    }

    public static final String DELIMITER = ",;,";
}
