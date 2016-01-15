package ru.ruranobe.wicket.components.admin;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

/**
 * Created by samogot on 27.08.15.
 */
public class AdminToolboxModalButton extends WebMarkupContainer
{

    public String getBody()
    {
        return body;
    }

    public AdminToolboxModalButton setBody(String body)
    {
        this.body = body;
        return this;
    }

    public String getIcon()
    {
        return icon;
    }

    public AdminToolboxModalButton setIcon(String icon)
    {
        this.icon = icon;
        return this;
    }

    public AdminToolboxModalButton setSelectableOnly()
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

    private void initialize(String title, String colorClass, String modal)
    {
        add(new AttributeAppender("class", Model.of("btn-" + colorClass), " "));
        add(new AttributeModifier("data-toggle", "modal"));
        add(new AttributeModifier("data-target", modal));
        add(new AttributeModifier("title", title));
        setEscapeModelStrings(false);
    }

    public AdminToolboxModalButton(String title, String modal, String colorClass, String icon)
    {
        super("button");
        this.icon = icon;
        initialize(title, colorClass, modal);
    }

    public AdminToolboxModalButton(String title, String modal, String colorClass)
    {
        super("button");
        initialize(title, colorClass, modal);
    }

    private String body;
    private String icon;

}
