package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.ContentItem;
import ru.ruranobe.engine.wiki.parser.FootnoteItem;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ChapterImagesMapper;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsHistoryMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.Chapter;
import ru.ruranobe.mybatis.tables.ChapterImage;
import ru.ruranobe.mybatis.tables.ExternalResource;
import ru.ruranobe.mybatis.tables.TextHistory;
import ru.ruranobe.wicket.webpages.base.TextLayoutPage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Editor extends TextLayoutPage
{
    public Editor(PageParameters parameters)
    {
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            Chapter chapter = getChapter(parameters, session);
            final Integer textId = chapter.getTextId();

            final ru.ruranobe.mybatis.tables.Text currentText = new ru.ruranobe.mybatis.tables.Text();
            ru.ruranobe.mybatis.tables.Text prevText = null;
            TextArea<String> editor = new TextArea<String>("editor", new Model<String>()
            {
                @Override
                public void setObject(String wikiText)
                {
                    currentText.setTextWiki(wikiText);
                }
            });
            if (textId != null)
            {
                TextsMapper textsMapperCacheable = CachingFacade.getCacheableMapper(session, TextsMapper.class);
                final ru.ruranobe.mybatis.tables.Text previousText = textsMapperCacheable.getTextById(textId);
                prevText = previousText;
                editor.setModel(new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        return previousText.getTextWiki();
                    }

                    @Override
                    public void setObject(String wikiText)
                    {
                        currentText.setTextWiki(wikiText);
                    }
                });
            }

            Form editorForm = new Form("editorForm");
            AjaxButton saveTextAjax = new SaveText("saveTextAjax", editorForm, currentText, chapter, prevText);
            editorForm.add(saveTextAjax);
            editorForm.add(editor);

            add(editorForm);
        }
        finally
        {
            session.close();
        }


    }

    public Chapter getChapter(PageParameters parameters, SqlSession session)
    {
        Chapter chapter = null;

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
        if (!Strings.isEmpty(chapterUrl))
        {
            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            chapter = chaptersMapperCacheable.getChapterByUrl(projectUrl + "/" + volumeUrl + "/" + chapterUrl);

            if (chapter == null)
            {
                throw RuranobeUtils.REDIRECT_TO_404;
            }
        }

        return chapter;
    }

    private class SaveText extends AjaxButton
    {
        public SaveText(String name, Form form, ru.ruranobe.mybatis.tables.Text text, Chapter chapter, ru.ruranobe.mybatis.tables.Text previousText)
        {
            super(name,form);
            this.chapter = chapter;
            this.text = text;
            this.previousText = previousText;
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form)
        {
            SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
            SqlSession session = sessionFactory.openSession();
            try
            {
                TextsMapper textsMapperCacheable = CachingFacade.getCacheableMapper(session, TextsMapper.class);
                textsMapperCacheable.insertText(text);

                ChapterImagesMapper chapterImagesMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterImagesMapper.class);
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

                WikiParser wikiParser = new WikiParser(text.getTextId(), chapter.getChapterId(), text.getTextWiki());
                text.setTextHtml(wikiParser.parseWikiText(imageUrls, true));

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
                text.setContents(contents.toString());

                StringBuilder footnotes = new StringBuilder();
                List<FootnoteItem> footnoteList = wikiParser.getFootnotes();
                for (int i = 0; i < footnoteList.size(); ++i)
                {
                    FootnoteItem footnoteItem = footnoteList.get(i);
                    String s = ((i < footnoteList.size()-1) ? DELIMITER : "");
                    footnotes.append(footnoteItem.getFootnoteId()).append(DELIMITER)
                             .append(footnoteItem.getFootnoteText()).append(s);
                }
                text.setFootnotes(footnotes.toString());

                textsMapperCacheable.updateText(text);

                chapter.setTextId(text.getTextId());

                ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
                chaptersMapperCacheable.updateChapter(chapter);

                TextsHistoryMapper textsHistoryMapperCacheable = CachingFacade.getCacheableMapper(session, TextsHistoryMapper.class);
                TextHistory textHistory = new TextHistory();
                textHistory.setCurrentTextId(text.getTextId());
                if (previousText != null)
                {
                    textHistory.setPreviousTextId(previousText.getTextId());
                }
                textHistory.setInsertionTime(new Date());
                textsHistoryMapperCacheable.insertTextHistory(textHistory);

                session.commit();
            }
            finally
            {
                session.close();
            }
        }

        private ru.ruranobe.mybatis.tables.Text previousText;
        private ru.ruranobe.mybatis.tables.Text text;
        private Chapter chapter;
    }
}
