package ru.ruranobe.wicket.components.admin.formitems;

import com.google.common.collect.ImmutableMap;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.wicket.components.admin.BannerUploadComponent;


/**
 * Created by samogot on 27.08.15.
 */
public class ProjectFormItemPanel extends Panel
{
    public ProjectFormItemPanel(String id, IModel<Project> model)
    {
        super(id, model);
        add(new TextField<String>("url").setRequired(true).setLabel(Model.of("Ссылка")));
        add(new TextField<String>("title").setRequired(true).setLabel(Model.of("Заголовок")));
        add(new CheckBox("projectHidden"));
        add(new CheckBox("bannerHidden"));
        add(new BannerUploadComponent("image", model).setContextVariables(new ImmutableMap.Builder<String, String>()
                .put("project", model.getObject().getUrl())
                .build()));
    }
}
