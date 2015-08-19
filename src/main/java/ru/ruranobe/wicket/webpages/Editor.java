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
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.Chapter;
import ru.ruranobe.wicket.webpages.base.TextLayoutPage;

public class Editor extends TextLayoutPage
{
    public Editor(PageParameters parameters)
    {
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            final Integer textId = getTextId(parameters, session);

            TextArea<String> editor;
            if (textId != null)
            {
                TextsMapper textsMapperCacheable = CachingFacade.getCacheableMapper(session, TextsMapper.class);
                final ru.ruranobe.mybatis.tables.Text text = textsMapperCacheable.getTextById(textId);
                editor = new TextArea<String>("editor", new Model<String>(text.getTextWiki()));
            }
            else
            {
                editor = new TextArea<String>("editor");
            }

            Form editorForm = new Form("editorForm");
            AjaxButton saveTextAjax = new AjaxButton("saveTextAjax", editorForm)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    
                }
            };
            editorForm.add(saveTextAjax);
            editorForm.add(editor);

            add(editorForm);
        }
        finally
        {
            session.close();
        }


    }

    public Integer getTextId(PageParameters parameters, SqlSession session)
    {
        Integer textId = null;

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
            Chapter chapter = chaptersMapperCacheable.getChapterByUrl(projectUrl + "/" + volumeUrl + "/" + chapterUrl);

            if (chapter == null)
            {
                throw RuranobeUtils.REDIRECT_TO_404;
            }

            textId = chapter.getTextId();
        }

        return textId;
    }
}
