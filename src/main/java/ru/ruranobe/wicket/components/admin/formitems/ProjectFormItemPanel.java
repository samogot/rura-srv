package ru.ruranobe.wicket.components.admin.formitems;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.wicket.components.admin.BannerUploadComponent;
import ru.ruranobe.wicket.webpages.admin.ProjectEdit;


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
        add(new BannerUploadComponent("image").setProject(model.getObject()));
        add(new BookmarkablePageLink("link", ProjectEdit.class, model.getObject().getUrlParameters()));
    }
}
