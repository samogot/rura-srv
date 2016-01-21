package ru.ruranobe.wicket.components.admin;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samogot on 27.08.15.
 */
public abstract class AdminFormPanel extends Panel
{

    public Form getForm()
    {
        return form;
    }

    public abstract boolean onSubmit();

    protected void onRefresh(AjaxRequestTarget target, Form<?> form)
    {
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        if (getParent() instanceof AdminLayoutPage)
        {
            ((AdminLayoutPage) getParent()).addContentsItem("#" + getMarkupId(), title);
        }
    }

    protected String getInitJavaScript()
    {
        return ";reinitAffix();";
    }

    public AdminFormPanel(String id, final String title, IModel<?> model)
    {
        super(id, model);
        setDefaultModel(model);
        this.title = title;
        setOutputMarkupId(true);
        setMarkupId(id);
        add(form = new Form("form")
        {
            @Override
            protected void onSubmit()
            {
                try
                {
                    if (AdminFormPanel.this.onSubmit())
                    {
                        success(title + " сохранены успешно");
                    }
                }
                catch (Exception ex)
                {
                    error("При сохранении произошла ошибка. Сообщите администратору что вы пытались поменять");
                    LOG.error("Error on ajax submit", ex);
                }
            }
        });
        form.add(new Label("heading", title));
        form.add(new ListView<Component>("buttonRepeater", toolbarButtons)
        {
            @Override
            protected void populateItem(ListItem<Component> item)
            {
                item.add(item.getModelObject());
            }
        });
        AdminToolboxAjaxButton saveButton = new AdminToolboxAjaxButton("Сохранить", "primary", "floppy-o", form)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                onAjaxSubmit(target);
                onAjaxProcess(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form)
            {
                onAjaxError(target);
                onAjaxProcess(target);
            }
        };
        saveButton.setDefaultFormProcessing(true);
        form.setDefaultButton(saveButton);
        toolbarButtons.add(saveButton);
        form.add(feedbackPanel = new FeedbackPanel("feedbackPanel", new ContainerFeedbackMessageFilter(this)));
        feedbackPanel.setOutputMarkupId(true);
    }

    protected void onAjaxSubmit(AjaxRequestTarget target)
    {
    }

    protected void onAjaxError(AjaxRequestTarget target)
    {
    }

    protected void onAjaxProcess(AjaxRequestTarget target)
    {
        target.add(feedbackPanel);
        target.appendJavaScript(String.format(";updateFeedbackPanelTimeout('#%s');", feedbackPanel.getMarkupId()));
    }

    private static final Logger LOG = LoggerFactory.getLogger(AdminFormPanel.class);
    protected List<Component> toolbarButtons = new ArrayList<>();
    protected Form form;
    private String title;
    protected FeedbackPanel feedbackPanel;
}
