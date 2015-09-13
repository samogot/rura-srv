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

    public abstract void onSubmit();

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
                AdminFormPanel.this.onSubmit();
                success(title + " сохранены успешно");
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
        AdminToolboxAjaxButton saveButton = new AdminToolboxAjaxButton("button", "Сохранить", "primary", "floppy-o", form)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                onAjaxProcess(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form)
            {
                onAjaxProcess(target);
            }
        };
        saveButton.setDefaultFormProcessing(true);
        form.setDefaultButton(saveButton);
        toolbarButtons.add(saveButton);
        form.add(feedbackPanel = new FeedbackPanel("feedbackPanel", new ContainerFeedbackMessageFilter(this)));
        feedbackPanel.setOutputMarkupId(true);
    }

    private void onAjaxProcess(AjaxRequestTarget target)
    {
        target.add(feedbackPanel);
        target.appendJavaScript(String.format(";updateFeedbackPanelTimeout('#%s');", feedbackPanel.getMarkupId()));
    }

    protected List<Component> toolbarButtons = new ArrayList<Component>();
    protected Form form;
    private String title;
    protected FeedbackPanel feedbackPanel;
}
