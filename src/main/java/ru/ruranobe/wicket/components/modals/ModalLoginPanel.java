package ru.ruranobe.wicket.components.modals;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import ru.ruranobe.wicket.components.LoginPanel;

public class ModalLoginPanel extends Panel
{
    public ModalLoginPanel(final String id)
    {
        super(id, Model.of("false"));

        add(new LoginPanel("loginPanel")
        {
            @Override
            protected void onLoginFailed()
            {
                ModalLoginPanel.this.setDefaultModelObject("true");
                super.onLoginFailed();
            }

            @Override
            protected void onLoginSucceeded()
            {
                ModalLoginPanel.this.setDefaultModelObject("false");
                super.onLoginSucceeded();
            }
        });
    }

    @Override
    protected void onComponentTag(ComponentTag tag)
    {
        super.onComponentTag(tag);
        tag.put("data-show", this.getDefaultModelObjectAsString());
    }

    private static final long serialVersionUID = 1L;
}