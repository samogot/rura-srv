package ru.ruranobe.wicket.components;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.CoreLibrariesContributor;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.OrphusCommentsMapper;
import ru.ruranobe.mybatis.tables.OrphusComment;

import java.util.Date;

public class AjaxOrphusMessageDialog extends WebComponent
{
    /* Must be named the same way in orhus.js -> getOrphusParameters() */
    public static final String ORPHUS_COMMENT_CHAPTER_ID = "chapterId";
    public static final String ORPHUS_COMMENT_PARAGRAPH = "paragraph";
    public static final String ORPHUS_COMMENT_START_OFFSET = "startOffset";
    public static final String ORPHUS_COMMENT_ORIGINAL_TEXT = "originalText";
    public static final String ORPHUS_COMMENT_REPLACEMENT_TEXT = "replacementText";
    public static final String ORPHUS_COMMENT_OPTIONAL_COMMENT = "optionalComment";
    private static final ResourceReference CSS = new CssResourceReference(
            AjaxOrphusMessageDialog.class, "bootstrap.min.css");
    private static final ResourceReference JAVASCRIPT_BOOTSTRAP = new JavaScriptResourceReference(
            AjaxOrphusMessageDialog.class, "bootstrap.min.js");
    private static final ResourceReference JAVASCRIPT_BOOTBOX = new JavaScriptResourceReference(
            AjaxOrphusMessageDialog.class, "bootbox.min.js");
    private static final String SHOW_BOOTBOX = "showOrphusDialog('%s', '%s', '%s', '%s', '%s');";
    private final AbstractDefaultAjaxBehavior behavior;

    public AjaxOrphusMessageDialog(String id, IModel model)
    {
        super(id, model);
        setOutputMarkupId(true);
        behavior = new AbstractDefaultAjaxBehavior()
        {
            @Override
            protected void updateAjaxAttributes(final AjaxRequestAttributes attributes)
            {
                super.updateAjaxAttributes(attributes);
                attributes.setAllowDefault(true);
            }

            @Override
            protected void respond(AjaxRequestTarget target)
            {
                Request request = RequestCycle.get().getRequest();

// For development purposes "0". Should be replaced later on.
                int chapterId = Integer.parseInt(request.getRequestParameters()
                                                        .getParameterValue(ORPHUS_COMMENT_CHAPTER_ID).toString(""));
                /*int paragraph = Integer.parseInt(request.getRequestParameters()
                        .getParameterValue(ORPHUS_COMMENT_PARAGRAPH).toString(""));*/
                int paragraph = Integer.parseInt("0");
                int startOffset = Integer.parseInt(request.getRequestParameters()
                                                          .getParameterValue(ORPHUS_COMMENT_START_OFFSET).toString(""));
                String originalText = request.getRequestParameters()
                                             .getParameterValue(ORPHUS_COMMENT_ORIGINAL_TEXT).toString("");
                String replacementText = request.getRequestParameters()
                                                .getParameterValue(ORPHUS_COMMENT_REPLACEMENT_TEXT).toString("");
                String optionalComment = request.getRequestParameters()
                                                .getParameterValue(ORPHUS_COMMENT_OPTIONAL_COMMENT).toString("");

                OrphusComment orphusComment = new OrphusComment(chapterId, paragraph,
                        startOffset, originalText, replacementText, optionalComment, new Date(System.currentTimeMillis()));

                SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
                SqlSession session = sessionFactory.openSession();
                try
                {
                    OrphusCommentsMapper orphusCommentsMapper = session.getMapper(OrphusCommentsMapper.class);
                    orphusCommentsMapper.insertOrphusComment(orphusComment);
                }
                finally
                {
                    session.close();
                }
            }
        };
        add(behavior);
    }

    @Override
    public void renderHead(final IHeaderResponse response)
    {
        super.renderHead(response);
        CoreLibrariesContributor.contributeAjax(getApplication(), response);
        response.render(CssHeaderItem.forReference(CSS));
        response.render(JavaScriptHeaderItem.forReference(JAVASCRIPT_BOOTSTRAP));
        response.render(JavaScriptHeaderItem.forReference(JAVASCRIPT_BOOTBOX));
    }

    public void show(final AjaxRequestTarget target, String originalText, String startOffset, String paragraph)
    {
        target.add(this);
        // TODO: add chapterId
        target.appendJavaScript(String.format(SHOW_BOOTBOX, originalText, behavior.getCallbackUrl().toString(), startOffset, paragraph, 1));
    }
}