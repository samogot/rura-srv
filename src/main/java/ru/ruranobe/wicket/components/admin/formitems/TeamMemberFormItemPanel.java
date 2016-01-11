package ru.ruranobe.wicket.components.admin.formitems;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.*;
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
    public TeamMemberFormItemPanel(String id, IModel<TeamMember> model, List<Team> teams, List<String> allRoles)
    {
        super(id, model);
        add(new TextField<String>("nickname").setRequired(true).setLabel(Model.of("Никнейм")));
        add(new DropDownChoice<>("team", teams).setNullValid(true)
                                               .setChoiceRenderer(new ChoiceRenderer<Team>("teamName", "teamId"))
                                               .setOutputMarkupId(true));
        add(new TextField<String>("userName"));
        add(new HiddenField<Integer>("userId"));
        add(new ListMultipleChoice<String>("userRoles", allRoles)
        {
            @Override
            protected void onComponentTag(ComponentTag tag)
            {
                super.onComponentTag(tag);
                if (getDefaultModelObject() == null)
                {
                    tag.getAttributes().put("disabled", "disabled");
                }
            }
        });
    }
}
