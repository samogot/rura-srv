package ru.ruranobe.wicket.webpages.admin;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.ChapterTextParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.Text;
import ru.ruranobe.mybatis.entities.tables.TextHistory;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsHistoryMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.Date;

@AuthorizeInstantiation("ADMIN")
public class Editor extends SidebarLayoutPage
{
    public Editor(PageParameters parameters)
    {
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        try (SqlSession session = sessionFactory.openSession())
        {
            Chapter chapter = getChapter(parameters, session);
            final Integer textId = chapter.getTextId();

            final Text currentText = new Text();
            Text prevText = null;
            TextArea<String> editor = new TextArea<>("editor", new Model<String>()
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
                final Text previousText = textsMapperCacheable.getTextById(textId);
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

            add(new BookmarkablePageLink("breadcrumbProject", ProjectEdit.class, chapter.getUrlParameters().remove("vhapter").remove("volume")));
            add(new BookmarkablePageLink("breadcrumbVolume", ProjectEdit.class, chapter.getUrlParameters().remove("vhapter")));
            add(new Label("breadcrumbActive", chapter.getTitle()));
        }


    }

    public Chapter getChapter(PageParameters parameters, SqlSession session)
    {
        Chapter chapter = null;

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

        String chapterUrl = parameters.get("chapter").toString();
        if (!Strings.isEmpty(chapterUrl))
        {
            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            chapter = chaptersMapperCacheable.getChapterByUrl(projectUrl + "/" + volumeUrl + "/" + chapterUrl);

            if (chapter == null)
            {
                throw RuranobeUtils.getRedirectTo404Exception(this);
            }
        }

        return chapter;
    }

    private class SaveText extends AjaxButton
    {
        private Text previousText;
        private Text text;
        private Chapter chapter;

        public SaveText(String name, Form form, Text text, Chapter chapter, Text previousText)
        {
            super(name, form);
            this.chapter = chapter;
            this.text = text;
            this.previousText = previousText;
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form)
        {
            SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
            try (SqlSession session = sessionFactory.openSession())
            {
                TextsMapper textsMapper = CachingFacade.getCacheableMapper(session, TextsMapper.class);
                textsMapper.insertText(text);
                chapter.setText(text);
                ChapterTextParser.parseChapterText(chapter, session, textsMapper, !chapter.getUrl().startsWith("system/"));

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
        }
    }
}
