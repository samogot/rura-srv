package ru.ruranobe.wicket.components.admin;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

/**
 * Created by samogot on 10.09.15.
 */
public abstract class AdminInfoFormPanel<T> extends AdminFormPanel
{
    public AdminInfoFormPanel(String id, String title, IModel<T> model)
    {
        super(id, title, model);
        form.add(getContentItemLabelComponent("item", model));
    }
    protected abstract Component getContentItemLabelComponent(String id, IModel<T> model);

}
