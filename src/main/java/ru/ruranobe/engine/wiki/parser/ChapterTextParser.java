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
            boolean preferColoredImgs = LoginSession.get().getUser() == null
                                        || LoginSession.get().getUser().isPreferColoredImgs() != Boolean.FALSE;
            if (chapterImage.getColoredImage() != null
                && !Strings.isEmpty(chapterImage.getColoredImage().getUrl()) && preferColoredImgs)
            {
                image = chapterImage.getColoredImage();
            }
            else if (chapterImage.getNonColoredImage() != null
                     && !Strings.isEmpty(chapterImage.getNonColoredImage().getUrl()))
            {
                image = chapterImage.getNonColoredImage();
            }
            else
            {
                image = new ExternalResource(-1, RuraConstants.UNKNOWN_IMAGE, RuraConstants.UNKNOWN_IMAGE);
            }
            images.add(image);
        }
        return images;
    }

    public static void parseChapterText(Chapter chapter, SqlSession session, TextsMapper textsMapper)
    {
        List<ExternalResource> images = ChapterTextParser.getChapterExternalResources(chapter, session);

        Text chapterText = chapter.getText();
        WikiParser wikiParser = new WikiParser(chapterText.getTextId(), chapter.getChapterId(), chapterText.getTextWiki());
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

    public static boolean getChapterText(Chapter chapter, SqlSession session)
    {
        TextsMapper textsMapper = CachingFacade.getCacheableMapper(session, TextsMapper.class);
        return getChapterText(chapter, session, textsMapper);
    }

    public static boolean getChapterText(Chapter chapter, SqlSession session, TextsMapper textsMapper)
    {
        if (chapter.getTextId() != null)
        {
            chapter.setText(textsMapper.getHtmlInfoById(chapter.getTextId()));
        }
        if (chapter.getText() != null && Strings.isEmpty(chapter.getText().getTextHtml()))
        {
            chapter.setText(textsMapper.getTextById(chapter.getTextId()));
            ChapterTextParser.parseChapterText(chapter, session, textsMapper);
            return true;
        }
        return false;
    }

    public static final String DELIMITER = ",;,";
}
