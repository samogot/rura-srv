package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import ru.ruranobe.wicket.components.LoginPanel;
import ru.ruranobe.wicket.webpages.Register;

public abstract class BaseLayoutPage extends WebPage
{
    @Override
    protected void onInitialize()
    {
        if (registerPageLink == null)
        {
            add(registerPageLink = new BookmarkablePageLink("registerPageLink", Register.class));
        }
        if (loginPanel == null)
        {
            add(loginPanel = new LoginPanel("loginPanel"));
        }
        super.onInitialize();
    }

    protected LoginPanel loginPanel = null;
    protected BookmarkablePageLink registerPageLink = null;
}