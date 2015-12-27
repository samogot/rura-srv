package ru.ruranobe.wicket.components.admin.formitems;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.ruranobe.mybatis.entities.tables.Team;
import ru.ruranobe.mybatis.entities.tables.TeamMember;

import java.util.List;

/**
 * Created by samogot on 27.08.15.
 */
public class TeamMemberFormItemPanel extends Panel
{
    public TeamMemberFormItemPanel(String id, IModel<TeamMember> model, List<Team> teams)
    {
        super(id, model);
        add(new TextField<String>("nickname").setRequired(true).setLabel(Model.of("Никнейм")));
        add(new DropDownChoice<Team>("team", teams).setChoiceRenderer(new ChoiceRenderer<Team>("teamName", "teamId"))
                                                   .setOutputMarkupId(true));
        add(new CheckBox("active"));
    }
}
