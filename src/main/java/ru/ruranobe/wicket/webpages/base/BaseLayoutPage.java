package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import ru.ruranobe.wicket.components.EmailPasswordRecoveryPanel;
import ru.ruranobe.wicket.components.LoginPanel;
import ru.ruranobe.wicket.components.UserActionsPanel;

import java.util.Calendar;

public abstract class BaseLayoutPage extends WebPage
{
    protected Panel userActionsPanelAndroidView = null;
    protected Panel userActionsPanel = null;
    protected Panel loginPanel = null;
    protected Panel resetPasswordPanel = null;

    @Override
    protected void onInitialize()
    {
        if (loginPanel == null)
        {
            add(loginPanel = new LoginPanel("loginPanel"));
        }
        if (userActionsPanel == null)
        {
            add(userActionsPanel = new UserActionsPanel("userActionsPanel", false));
        }
        if (userActionsPanelAndroidView == null)
        {
            add(userActionsPanelAndroidView = new UserActionsPanel("userActionsPanelAndroidView", true));
        }
        if (resetPasswordPanel == null)
        {
            add(resetPasswordPanel = new EmailPasswordRecoveryPanel("resetPasswordPanel"));
        }
        add(new Label("pageTitle", getPageTitle()));
        add(new Label("currentYear", Calendar.getInstance().get(Calendar.YEAR)));
        super.onInitialize();
    }

    protected String getPageTitle()
    {
        return "РуРанобэ";
    }
}