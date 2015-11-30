package ru.ruranobe.wicket.components.admin.formitems;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.ruranobe.mybatis.entities.tables.Project;

/**
 * Created by samogot on 13.09.15.
 */
public class SubProjectSelectorItemPanel extends Panel
{
    public SubProjectSelectorItemPanel(String id, IModel<Project> model)
    {
        super(id, model);
        add(new TextField<String>("title").setRequired(true).setLabel(Model.of("Подзаголовок")));
    }
}
