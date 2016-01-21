package ru.ruranobe.wicket.webpages.admin;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.ChapterTextParser;
import ru.ruranobe.engine.wiki.parser.FootnoteItem;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.Text;
import ru.ruranobe.mybatis.entities.tables.TextHistory;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsHistoryMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import javax.servlet.http.HttpSession;
import java.util.Date;

@AuthorizeInstantiation({"ADMIN", "TEAM MEMBER"})
public class Editor extends SidebarLayoutPage
{
    public Editor(PageParameters parameters)
    {
        HttpSession httpSession = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest().getSession();
        if (httpSession != null)
        {
            int sixHours = 60*60*6;
            httpSession.setMaxInactiveInterval(sixHours);
        }
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            Chapter chapter = getChapter(parameters, session);
            Integer textId = chapter.getTextId();

            Text prevText = null;
            final Text currentText = new Text();
            TextArea<String> editor = new TextArea<>("editor");
            if (textId == null)
            {
                editor.setModel(new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        return currentText.getTextWiki() == null ? "" : currentText.getTextWiki();
                    }

                    @Override
                    public void setObject(String wikiText)
                    {
                        currentText.setTextWiki(wikiText == null ? "" : wikiText);
                    }
                });
            }
            else
            {
                final Text previousText = CachingFacade.getCacheableMapper(session, TextsMapper.class)
                        .getTextById(textId);
                editor.setModel(new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        return currentText.getTextWiki() == null
                               ? previousText.getTextWiki()
                               : currentText.getTextWiki();
                    }

                    @Override
                    public void setObject(String wikiText)
                    {
                        currentText.setTextWiki(wikiText == null ? "" : wikiText);
                    }
                });
                prevText = previousText;
            }

            Label previewText = new Label("previewText");

            Form editorForm = new Form("editorForm");
            editorForm.add(new SaveText("saveTextAjax", editorForm, currentText, chapter, prevText));
            editorForm.add(new Preview("preview", previewText, editor, editorForm, chapter));
            editorForm.add(previewText.setEscapeModelStrings(false).setOutputMarkupId(true));
            editorForm.add(editor.setEscapeModelStrings(true).setOutputMarkupId(true));

            add(editorForm.setOutputMarkupId(true));

            add(new BookmarkablePageLink("breadcrumbProject", ProjectEdit.class, chapter.getUrlParameters().remove("vhapter").remove("volume")));
            add(new BookmarkablePageLink("breadcrumbVolume", ProjectEdit.class, chapter.getUrlParameters().remove("vhapter")));
            add(new Label("breadcrumbActive", chapter.getTitle()));
        }
    }

    public Chapter getChapter(PageParameters parameters, SqlSession session)
    {
        Chapter chapter = null;

        String projectUrl = parameters.get("project").toString();
        redirectTo404(Strings.isEmpty(projectUrl));

        String volumeUrl = parameters.get("volume").toString();
        redirectTo404(Strings.isEmpty(volumeUrl));

        String chapterUrl = parameters.get("chapter").toString();
        if (!Strings.isEmpty(chapterUrl))
        {
            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            chapter = chaptersMapperCacheable.getChapterByUrl(projectUrl + "/" + volumeUrl + "/" + chapterUrl);

            redirectTo404IfArgumentIsNull(chapter);
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
            try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
            {
                TextsMapper textsMapper = CachingFacade.getCacheableMapper(session, TextsMapper.class);
                textsMapper.insertText(text);
                chapter.setText(text);
                ChapterTextParser.parseChapterText(chapter, session, textsMapper, !chapter.getUrl().startsWith("system/"));

                CachingFacade.getCacheableMapper(session, ChaptersMapper.class).updateChapter(chapter);

                TextHistory textHistory = new TextHistory();
                textHistory.setCurrentTextId(text.getTextId());
                if (previousText != null)
                {
                    textHistory.setPreviousTextId(previousText.getTextId());
                }
                textHistory.setInsertionTime(new Date());
                CachingFacade.getCacheableMapper(session, TextsHistoryMapper.class).insertTextHistory(textHistory);

                session.commit();
            }
        }
    }

    private class Preview extends AjaxButton
    {
        private Chapter chapter;
        private TextArea<String> editor;
        private Label previewText;

        public Preview(String name, Label previewText, TextArea<String> editor, Form form, Chapter chapter)
        {
            super(name, form);
            this.chapter = chapter;
            this.editor = editor;
            this.previewText = previewText;
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form)
        {
            if (editor.isVisible())
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                {
                    String wikiText = editor.getModelObject();
                    WikiParser parser = new WikiParser(null, null, wikiText, true);

                    String headerTag = chapter.isNested() ? "h3" : "h2";
                    String textHtml = "<" + headerTag + " id=\"" + chapter.getUrlPart() + "\">" + chapter.getTitle() +
                            "</" + headerTag + ">" + parser.parseWikiText(ChapterTextParser.getChapterExternalResources(chapter, session), true);

                    StringBuilder footnotes = new StringBuilder();
                    for (FootnoteItem footnoteItem : parser.getFootnotes())
                    {
                        footnotes.append("<li id=\"cite_note-").append(footnoteItem.getFootnoteId()).append("\">")
                                .append("<a href=\"#cite_ref-").append(footnoteItem.getFootnoteId()).append("\">↑</a> <span class=\"reference-text\">")
                                .append(footnoteItem.getFootnoteText()).append("</span></li>");
                    }

                    if (footnotes.length() != 0)
                    {
                        footnotes.insert(0, "<h2 id=\"footnotes\">Примечания</h2><ol class=\"references\">");
                        footnotes.append("</ol>");
                    }

                    previewText.setDefaultModel(Model.of(textHtml + footnotes.toString()));

                    editor.setVisible(false);
                    previewText.setVisible(true);
                }
            }
            else
            {
                editor.setVisible(true);
                previewText.setVisible(false);
            }

            target.add(form);
            target.add(previewText);
            target.add(editor);
        }
    }
}
