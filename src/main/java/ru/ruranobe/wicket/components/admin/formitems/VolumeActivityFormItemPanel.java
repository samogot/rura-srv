package ru.ruranobe.wicket.components.admin.formitems;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.ruranobe.mybatis.entities.tables.VolumeActivity;

import java.util.Arrays;

/**
 * Created by samogot on 27.08.15.
 */
public class VolumeActivityFormItemPanel extends Panel
{
    public VolumeActivityFormItemPanel(String id, IModel<VolumeActivity> model)
    {
        super(id, model);
        add(new TextField<String>("activityName").setRequired(true).setLabel(Model.of("Название")));
        add(new DropDownChoice<String>("activityType", Arrays.asList("text", "image"))
        {
            @Override
            protected boolean localizeDisplayValues()
            {
                return true;
            }
        }.setRequired(true));
    }
}
