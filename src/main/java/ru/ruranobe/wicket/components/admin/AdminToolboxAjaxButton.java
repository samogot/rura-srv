package ru.ruranobe.wicket.components.admin;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

/**
 * Created by samogot on 27.08.15.
 */
public class AdminToolboxAjaxButton extends AjaxButton
{
//    @Override
//    public void renderHead(IHeaderResponse response)
//    {
//        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(this.getClass(), "AdminToolboxAjaxButton.js")));
//    }

    public String getBody()
    {
        return body;
    }

    public AdminToolboxAjaxButton setBody(String body)
    {
        this.body = body;
        return this;
    }

    public String getIcon()
    {
        return icon;
    }

    public AdminToolboxAjaxButton setIcon(String icon)
    {
        this.icon = icon;
        return this;
    }

    public AdminToolboxAjaxButton setSelectableOnly()
    {
        add(new AttributeAppender("class", Model.of("selectable-only"), " "));
        add(new AttributeModifier("disabled", "disabled"));
        return this;
    }


    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
    {
        replaceComponentTagBody(markupStream, openTag,
                Strings.isEmpty(body) ? "<i class=\"fa fa-" + icon + "\"></i>" : body);
    }

    private void initialize(String title, String colorClass)
    {
        add(new AttributeAppender("class", Model.of("btn-" + colorClass), " "));
        add(new AttributeModifier("title", title));
        setEscapeModelStrings(false);
        setDefaultFormProcessing(false);
    }

    public AdminToolboxAjaxButton(String title, String colorClass, String icon, Form<?> form)
    {
        super("button", form);
        this.icon = icon;
        initialize(title, colorClass);
    }

    public AdminToolboxAjaxButton(String title, String colorClass, Form<?> form)
    {
        super("button", form);
        initialize(title, colorClass);
    }

    private String body;
    private String icon;

}
