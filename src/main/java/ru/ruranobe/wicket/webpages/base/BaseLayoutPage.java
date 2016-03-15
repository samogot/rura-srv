package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.cookies.CookieUtils;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.components.UserActionsPanel;
import ru.ruranobe.wicket.components.modals.ModalEmailPasswordRecoveryPanel;
import ru.ruranobe.wicket.components.modals.ModalLoginPanel;

import java.time.Year;
import java.util.Arrays;
import java.util.List;

public abstract class BaseLayoutPage extends WebPage
{
    private final TransparentWebMarkupContainer body;
    protected Panel userActionsPanelAndroidView = null;
    protected Panel userActionsPanel = null;
    protected Panel loginPanel = null;
    protected Panel resetPasswordPanel = null;

    public BaseLayoutPage()
    {
        checkLogin();
        body = new TransparentWebMarkupContainer("body");
        checkStyleCookies();
    }

    private void checkStyleCookies()
    {
        CookieUtils cookieUtils = new CookieUtils();
        String color = cookieUtils.load("rura_style_color");
        cookieUtils.remove("rura_style_color");
        String dayNight = cookieUtils.load("rura_style_day_night");
        cookieUtils.remove("rura_style_day_night");
        LoginSession loginSession = LoginSession.get();
        if (Arrays.asList("white", "blue", "red", "black").contains(color))
        {
            loginSession.setStyleColor(color);
        }
        if ("night".equals(dayNight))
        {
            loginSession.setStyleDayNight(dayNight);
        }
        else if ("day".equals(dayNight))
        {
            loginSession.setStyleDayNight("");
        }
        addBodyClassAttribute(loginSession.getBodyClassStyle());
    }

    protected void addBodyClassAttribute(String value)
    {
        body.add(AttributeModifier.append("class", " " + value));
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
        WebMarkupContainer ogTitle = new WebMarkupContainer("ogTitle");
        add(ogTitle);
        ogTitle.add(new AttributeModifier("content", getPageTitle()));
        if (get("ogImage") == null) {
            WebMarkupContainer ogImage = new WebMarkupContainer("ogImage");
            add(ogImage);
        }
        add(new Label("currentYear", Year.now()));
        add(body);
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