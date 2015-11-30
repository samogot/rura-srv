package ru.ruranobe.wicket.components.admin.formitems;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.ruranobe.mybatis.entities.tables.Team;

/**
 * Created by samogot on 27.08.15.
 */
public class TeamFormItemPanel extends Panel
{
    public TeamFormItemPanel(String id, IModel<Team> model)
    {
        super(id, model);
        add(new TextField<String>("teamName").setRequired(true).setLabel(Model.of("Название")));
        add(new TextField<String>("teamWebsiteLink"));
    }
}
