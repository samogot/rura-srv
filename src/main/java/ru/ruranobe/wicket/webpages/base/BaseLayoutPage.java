package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import ru.ruranobe.wicket.components.LoginPanel;
import ru.ruranobe.wicket.components.UserActionsPanel;

import java.util.Calendar;

public abstract class BaseLayoutPage extends WebPage
{

    protected UserActionsPanel userActionsPanelAndroidView = null;
    protected UserActionsPanel userActionsPanel = null;
    protected LoginPanel loginPanel = null;

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
        add(new Label("pageTitle", getPageTitle()));
        add(new Label("currentYear", Calendar.getInstance().get(Calendar.YEAR)));
        super.onInitialize();
    }

    protected String getPageTitle()
    {
        return "РуРанобэ";
    }
}