package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.wicket.components.modals.ModalEmailPasswordRecoveryPanel;
import ru.ruranobe.wicket.components.modals.ModalLoginPanel;
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
            add(loginPanel = new ModalLoginPanel("loginPanelModal"));
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
            add(resetPasswordPanel = new ModalEmailPasswordRecoveryPanel("resetPasswordPanelModal"));
        }
        add(new Label("pageTitle", getPageTitle()));
        add(new Label("currentYear", Calendar.getInstance().get(Calendar.YEAR)));
        super.onInitialize();
    }

    protected void redirectTo404()
    {
        throw RuranobeUtils.getRedirectTo404Exception(this);
    }

    protected void redirectTo404(boolean expression)
    {
        if (expression)
        {
            redirectTo404();
        }
    }

    protected void redirectTo404IfArgumentIsNull(Object argument)
    {
        redirectTo404(argument==null);
    }

    protected String getPageTitle()
    {
        return "РуРанобэ";
    }
}