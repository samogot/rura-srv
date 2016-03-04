package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.components.UserActionsPanel;
import ru.ruranobe.wicket.components.modals.ModalEmailPasswordRecoveryPanel;
import ru.ruranobe.wicket.components.modals.ModalLoginPanel;

import java.time.Year;

public abstract class BaseLayoutPage extends WebPage
{
    protected Panel userActionsPanelAndroidView = null;
    protected Panel userActionsPanel = null;
    protected Panel loginPanel = null;
    protected Panel resetPasswordPanel = null;

    public BaseLayoutPage()
    {
        checkLogin();
    }

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
        add(new Label("currentYear", Year.now()));
        super.onInitialize();
    }

    private void checkLogin()
    {
        if (!LoginSession.get().isSignedIn())
        {
            IAuthenticationStrategy authenticationStrategy = getApplication()
                    .getSecuritySettings().getAuthenticationStrategy();

            // get username and password from persistence store
            String[] data = authenticationStrategy.load();

            if ((data != null) && (data.length > 1))
            {
                // try to sign in the user
                if (LoginSession.get().signIn(data[0], data[1]))
                {
                    // logon successful. Continue to the original destination
                    continueToOriginalDestination();
                }
                else
                {
                    // the loaded credentials are wrong. erase them.
                    authenticationStrategy.remove();
                }
            }
        }
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
        redirectTo404(argument == null);
    }

    protected String getPageTitle()
    {
        return "РуРанобэ";
    }
}