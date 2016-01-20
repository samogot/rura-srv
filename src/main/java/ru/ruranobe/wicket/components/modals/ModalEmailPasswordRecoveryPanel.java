package ru.ruranobe.wicket.components.modals;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import ru.ruranobe.wicket.components.EmailPasswordRecoveryPanel;

public class ModalEmailPasswordRecoveryPanel extends Panel
{
    public ModalEmailPasswordRecoveryPanel(final String id)
    {
        super(id, Model.of("false"));

        add(new EmailPasswordRecoveryPanel("resetPasswordPanel")
        {
            @Override
            protected void onFail(String message)
            {
                ModalEmailPasswordRecoveryPanel.this.setDefaultModelObject("true");
                super.onFail(message);
            }

            @Override
            protected void onSuccess(String message)
            {
                ModalEmailPasswordRecoveryPanel.this.setDefaultModelObject("false");
                super.onSuccess(message);
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