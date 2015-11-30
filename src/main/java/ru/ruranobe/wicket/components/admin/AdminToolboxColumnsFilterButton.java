package ru.ruranobe.wicket.components.admin;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Created by samogot on 18.09.15.
 */
public class AdminToolboxColumnsFilterButton extends Panel
{
    @Override
    protected void onComponentTag(ComponentTag tag)
    {
        super.onComponentTag(tag);
        tag.setName("div");
        tag.getAttributes().clear();
        tag.getAttributes().put("class", "btn-group");
    }

    public AdminToolboxColumnsFilterButton(String id, IModel<List<? extends String>> model)
    {
        super(id, model);
        add(new ListView<String>("repeater", model)
        {
            @Override
            protected void populateItem(ListItem<String> item)
            {
                CheckBox checkbox = new CheckBox("checkbox", Model.of(true));
                checkbox.setOutputMarkupId(true);
                checkbox.add(new AttributeModifier("data-toggle", ".column-number-" + item.getIndex()));
                item.add(checkbox);
                Label label = new Label("label", item.getModel());
                label.add(new AttributeModifier("for", checkbox.getMarkupId()));
                item.add(label);
            }
        });
    }
}
