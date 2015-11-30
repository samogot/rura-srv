package ru.ruranobe.wicket.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by Samogot on 16.05.2015.
 */
public class CommentsPanel extends Panel
{
    public CommentsPanel(String id, Integer topicId)
    {
        super(id);
        add(new WebMarkupContainer("comments").add(new AttributeModifier("data-topic-id", topicId)));
    }
}
