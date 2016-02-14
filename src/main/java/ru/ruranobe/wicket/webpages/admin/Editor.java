package ru.ruranobe.wicket.webpages.admin;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.ChapterTextParser;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.Text;
import ru.ruranobe.mybatis.entities.tables.TextHistory;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsHistoryMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.InstantiationSecurityCheck;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;
import ru.ruranobe.wicket.webpages.common.TextPage;

import javax.servlet.http.HttpSession;
import java.util.Date;

public class Editor extends AdminLayoutPage implements InstantiationSecurityCheck
{
    @Override
    public void doInstantiationSecurityCheck()
    {
        if (!LoginSession.get().isProjectEditAllowedByUser(projectUrl))
        {
            throw new UnauthorizedInstantiationException(this.getClass());
        }
    }

    private String projectUrl;
    Text text;
    Integer prevTextId;
    Chapter chapter = null;

    public Editor(PageParameters parameters)
    {
        projectUrl = parameters.get("project").toString();
        doInstantiationSecurityCheck();
        addContentsItem(urlFor(TextPage.class, parameters).toString(), "Просмотр");
        HttpSession httpSession = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest().getSession();
        if (httpSession != null)
        {
            int sixHours = 60 * 60 * 6;
            httpSession.setMaxInactiveInterval(sixHours);
        }
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            getChapter(parameters, session);
            prevTextId = chapter.getTextId();
            if (prevTextId == null)
            {
                text = new Text();
            }
            else
            {
                text = CachingFacade.getCacheableMapper(session, TextsMapper.class).getTextById(prevTextId);
            }
            text.setTextId(null);
            if (text.getTextWiki() == null)
            {
                text.setTextHtml("");
            }
        }
        final TextArea<String> editor = new TextArea<>("editor", PropertyModel.of(this, "text.textWiki"));
        final Label previewText = new Label("previewText");

        Form editorForm = new Form("editorForm");
        editorForm.add(new AjaxButton("saveTextAjax")
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                {
                    TextsMapper textsMapper = CachingFacade.getCacheableMapper(session, TextsMapper.class);
                    if (Strings.isEmpty(text.getTextWiki()))
                    {
                        text.setTextWiki("");
                    }
                    textsMapper.insertText(text);
                    chapter.setText(text);
                    ChapterTextParser.parseChapterText(chapter, session, textsMapper, !projectUrl.equals("system"));

                    CachingFacade.getCacheableMapper(session, ChaptersMapper.class).updateChapterText(chapter);

                    TextHistory textHistory = new TextHistory();
                    textHistory.setCurrentTextId(text.getTextId());
                    textHistory.setPreviousTextId(prevTextId);
                    textHistory.setInsertionTime(new Date());
                    textHistory.setChapterId(chapter.getChapterId());
                    textHistory.setUserId(LoginSession.get().getUser().getUserId());
                    CachingFacade.getCacheableMapper(session, TextsHistoryMapper.class).insertTextHistory(textHistory);
                    prevTextId = text.getTextId();
                    text.setTextId(null);

                    session.commit();
                }
            }
        });
        editorForm.add(new AjaxButton("preview")
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                if (editor.isVisible())
                {
                    try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                    {
                        String wikiText = editor.getModelObject();
                        WikiParser parser = new WikiParser(null, null, wikiText, !projectUrl.equals("system"));

                        String heading = ChapterTextParser.getChapterHeading(chapter);
                        String body = parser.parseWikiText(ChapterTextParser.getChapterExternalResources(chapter, session), true);
                        StringBuilder footnotes = new StringBuilder();
                        ChapterTextParser.addFootnotes(footnotes, parser.getFootnotes());
                        ChapterTextParser.endFootnotes(footnotes);
                        previewText.setDefaultModel(Model.of(heading + body + footnotes));
                        previewText.setVisible(true);
                        editor.setVisible(false);
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
        });
        editorForm.add(previewText.setEscapeModelStrings(false).setOutputMarkupId(true));
        editorForm.add(editor.setEscapeModelStrings(false).setOutputMarkupId(true));

        add(editorForm.setOutputMarkupId(true));

        add(new BookmarkablePageLink("breadcrumbProject", ProjectEdit.class, chapter.getUrlParameters().remove("chapter").remove("volume")));
        add(new BookmarkablePageLink("breadcrumbVolume", VolumeEdit.class, chapter.getUrlParameters().remove("chapter")));
        add(new Label("breadcrumbActive", chapter.getTitle()));
    }

    public void getChapter(PageParameters parameters, SqlSession session)
    {
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
    }

}
