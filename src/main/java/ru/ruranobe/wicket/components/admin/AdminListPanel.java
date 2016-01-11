package ru.ruranobe.wicket.components.admin;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by samogot on 27.08.15.
 */
public abstract class AdminListPanel<T> extends AdminFormPanel
{

//    @Override
//    public void renderHead(IHeaderResponse response)
//    {
//        response.render(CssHeaderItem.forReference(new PackageResourceReference(this.getClass(), "AdminListPanel.css")));
//        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(this.getClass(), "AdminListPanel.js")));
//    }

    protected abstract T makeItem();

    protected abstract void onRemoveItem(T removedItem, AjaxRequestTarget target, Form form);

    protected abstract void onAddItem(T newItem, AjaxRequestTarget target, Form form);

    public AdminListPanel(String id, String title, final IModel<? extends List<T>> model)
    {
        super(id, title, model);
        this.model = model;

        toolbarButtons.add(0, new AdminToolboxAjaxButton("Добавить", "success", "plus", form)
        {
            @Override
            public void onSubmit(AjaxRequestTarget target, Form form)
            {
                T newItem = makeItem();
                model.getObject().add(newItem);
                onAddItem(newItem, target, form);
                onRefresh(target, form);
            }
        });

        toolbarButtons.add(1, new AdminToolboxAjaxButton("Удалить", "danger", "trash-o", form)
        {
            @Override
            public void onSubmit(AjaxRequestTarget target, Form form)
            {
                if (selectedItem == null)
                {
                    target.appendJavaScript("alert('Ничего не выбрано!');");
                }
                else
                {
                    removed.add(selectedItem);
                    onRemoveItem(selectedItem, target, form);
                    onRefresh(target, form);
                    selectedItem = null;
                }
            }
        }.setSelectableOnly());
    }

    protected T selectedItem = null;
    protected IModel<? extends List<T>> model;
    protected Set<T> removed = new HashSet<>();
}
