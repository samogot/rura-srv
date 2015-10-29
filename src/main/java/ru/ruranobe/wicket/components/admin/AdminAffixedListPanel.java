package ru.ruranobe.wicket.components.admin;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Created by samogot on 27.08.15.
 */
public abstract class AdminAffixedListPanel<T> extends AdminListPanel<T>
{
//    @Override
//    public void renderHead(IHeaderResponse response)
//    {
//        response.render(CssHeaderItem.forReference(new PackageResourceReference(this.getClass(), "AdminAffixedListPanel.css")));
//        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(this.getClass(), "../ReinitAffix.js")));
//        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(this.getClass(), "AdminAffixedListPanel.js")));
//    }


    public boolean isSortable()
    {
        return sortable;
    }

    public AdminAffixedListPanel<T> setSortable(boolean sortable)
    {
        this.sortable = sortable;
        return this;
    }

    protected abstract Component getSelectorItemLabelComponent(String id, IModel<T> model);

    protected abstract Component getFormItemLabelComponent(String id, IModel<T> model);

    @Override
    protected void onInitialize()
    {
        super.onInitialize();

        WebMarkupContainer formBlock = new WebMarkupContainer("formBlock");
        formBlock.setOutputMarkupId(true);
        form.add(formBlock);
        formBlock.add(formBlockItemRepeater = new PropertyListView<T>("repeater", model)
        {
            @Override
            protected void populateItem(ListItem<T> item)
            {
                initializeFormBlockListItem(item);
            }
        });


        WebMarkupContainer selectorBlock = new WebMarkupContainer("selectorBlock");
        selectorBlock.setOutputMarkupId(true);
        form.add(selectorBlock);
        selectorBlock.add(selectorBlockItemRepeater = new PropertyListView<T>("repeater", model)
        {
            @Override
            protected void populateItem(ListItem<T> item)
            {
                initializeSelectorBlockListItem(item);
            }
        });
        if (sortable)
        {
            selectorBlock.add(new AttributeAppender("class", Model.of("sortable"), " "));
        }
    }

    @Override
    protected void onAddItem(T newItem, AjaxRequestTarget target, Form form)
    {
        ListItem<T> formBlockListItem = new ListItem<T>(formBlockItemRepeater.size(), new CompoundPropertyModel<T>(newItem));
        initializeFormBlockListItem(formBlockListItem);
        formBlockItemRepeater.add(formBlockListItem);
        target.prependJavaScript(String.format(";addFormItemStub('%s', '%s');", formBlockListItem.getMarkupId(), form.getMarkupId()));
        target.add(formBlockListItem);

        ListItem<T> selectorBlockListItem = new ListItem<T>(selectorBlockItemRepeater.size(), new CompoundPropertyModel<T>(newItem));
        initializeSelectorBlockListItem(selectorBlockListItem);
        selectorBlockItemRepeater.add(selectorBlockListItem);
        target.prependJavaScript(String.format(";addSelectorItemStub('%s', '%s');", selectorBlockListItem.getMarkupId(), form.getMarkupId()));
        target.add(selectorBlockListItem);

        target.appendJavaScript(String.format(";$('#%s').click();", selectorBlockListItem.getMarkupId()));
        if (sortable)
        {
            target.appendJavaScript(String.format(";$('#%s .list-group.select.sortable').trigger('sortupdate');", form.getMarkupId()));
        }

    }

    @Override
    protected void onRemoveItem(T removedItem, AjaxRequestTarget target, Form form)
    {
        target.appendJavaScript(String.format(";removeAdminAffixItem('%s');", form.getMarkupId()));
        formBlockItemRepeater.get(model.getObject().indexOf(removedItem)).setVisible(false);
        selectorBlockItemRepeater.get(model.getObject().indexOf(removedItem)).setVisible(false);
    }

    protected void initializeSelectorBlockListItem(final ListItem<T> item)
    {
        item.setOutputMarkupId(true);
        item.add(new AjaxEventBehavior("click")
        {
            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes)
            {
                super.updateAjaxAttributes(attributes);
//                attributes.setAllowDefault(true);
                attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.BUBBLE);
            }

            @Override
            protected void onEvent(AjaxRequestTarget target)
            {
                selectedItem = item.getModelObject();
            }
        });
        String formItemMarkupId = formBlockItemRepeater.get(item.getIndex()).get("item").getMarkupId();
        item.add(new AttributeModifier("href", "#" + formItemMarkupId));
        item.add(new AttributeModifier("aria-controls", formItemMarkupId));
        item.add(new WebMarkupContainer("sortableHandler")
        {

            @Override
            public void renderHead(IHeaderResponse response)
            {
                super.renderHead(response);
                if (sortable)
                {
                    response.render(OnDomReadyHeaderItem.forScript(String.format("setMoveHandlerPadding('%s')", item.getMarkupId())));
                }
            }

        }.setVisible(sortable));
        item.add(getSelectorItemLabelComponent("label", item.getModel()));
    }

    private void initializeFormBlockListItem(ListItem<T> item)
    {
        item.setOutputMarkupId(true);
        WebMarkupContainer innerItem = new WebMarkupContainer("item");
        item.add(innerItem);
        innerItem.setOutputMarkupId(true);
        innerItem.add(new HiddenField<Integer>("orderNumber").setVisible(sortable));
        innerItem.add(getFormItemLabelComponent("label", item.getModel()));
    }

    public AdminAffixedListPanel(String id, String title, IModel<? extends List<T>> model)
    {
        super(id, title, model);
    }

    private boolean sortable;
    private PropertyListView<T> formBlockItemRepeater;
    private PropertyListView<T> selectorBlockItemRepeater;
}
