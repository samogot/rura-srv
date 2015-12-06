package ru.ruranobe.wicket.components.admin.formitems;

import com.google.common.collect.ImmutableMap;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.wicket.components.admin.BannerUploadComponent;

/**
 * Created by samogot on 10.09.15.
 */
public class ProjectInfoPanel extends Panel
{
    public ProjectInfoPanel(String id, IModel<Project> model)
    {
        super(id, model);
        add(new TextField<String>("url").setRequired(true).setLabel(Model.of("Ссылка")));
        add(new BannerUploadComponent("image", model).setContextVariables(new ImmutableMap.Builder<String, String>()
                .put("project", model.getObject().getUrl())
                .build()));
        add(new TextField<String>("title").setRequired(true).setLabel(Model.of("Заголовок")));
        add(new TextField<String>("nameJp"));
        add(new TextField<String>("nameEn"));
        add(new TextField<String>("nameRu"));
        add(new TextField<String>("nameRomaji"));
        add(new TextField<String>("author"));
        add(new TextField<String>("illustrator"));
        add(new CheckBox("onevolume"));
        add(new CheckBox("projectHidden"));
        add(new CheckBox("bannerHidden"));
        add(new TextArea<String>("franchise"));
        add(new TextArea<String>("annotation"));
    }
}
