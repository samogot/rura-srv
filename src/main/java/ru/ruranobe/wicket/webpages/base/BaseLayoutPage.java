package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import ru.ruranobe.wicket.components.LoginPanel;
import ru.ruranobe.wicket.components.UserActionsPanel;
import ru.ruranobe.wicket.webpages.Register;

public abstract class BaseLayoutPage extends WebPage
{
    @Override
    protected void onInitialize()
    {
        if (loginPanel == null)
        {
            add(loginPanel = new LoginPanel("loginPanel"));
        }
        if (userActionsPanel == null)
        {
            add(userActionsPanel = new UserActionsPanel("userActionsPanel"));
        }
        super.onInitialize();
    }

    protected UserActionsPanel userActionsPanel = null;
    protected LoginPanel loginPanel = null;

}