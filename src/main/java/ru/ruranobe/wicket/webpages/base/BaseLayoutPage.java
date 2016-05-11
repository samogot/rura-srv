package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.cookies.CookieUtils;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.components.UserActionsPanel;
import ru.ruranobe.wicket.components.modals.ModalEmailPasswordRecoveryPanel;
import ru.ruranobe.wicket.components.modals.ModalLoginPanel;

import java.time.Year;
import java.util.Arrays;

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
        if (get("ogImage") == null)
        {
            WebMarkupContainer ogImage = new WebMarkupContainer("ogImage");
            add(ogImage);
        }
        add(new Label("currentYear", Year.now()));
        add(body);
        super.onInitialize();
        handleForceDesktopFlag();
    }

    private void handleForceDesktopFlag()
    {
        if (!getPageParameters().get("forceDesktopVersion").isEmpty())
        {
            LoginSession.get().setForceDesktopVersion(getPageParameters().get("forceDesktopVersion").toBoolean());
            setResponsePage(getPageClass(), new PageParameters(getPageParameters()).remove("forceDesktopVersion"));
        }
        PageParameters forceDesktopVersionParameters = new PageParameters(getPageParameters())
                .add("forceDesktopVersion", !LoginSession.get().isForceDesktopVersion());
        BookmarkablePageLink forceDesktopVersionLink = new BookmarkablePageLink("forceDesktopVersion", getPageClass(), forceDesktopVersionParameters);
        if (!LoginSession.get().isForceDesktopVersion())
        {
            forceDesktopVersionLink.add(new AttributeAppender("class", " visible-xs-inline"));
            forceDesktopVersionLink.setBody(Model.of("Полня версия"));
        }
        else
        {
            forceDesktopVersionLink.setBody(Model.of("Вернутся к мобильной версии"));
        }
        add(forceDesktopVersionLink);
    }

    @Override
    public void renderHead(IHeaderResponse response)
    {
        super.renderHead(response);
        if (LoginSession.get().isForceDesktopVersion())
        {
            response.render(composeMetaItem("viewport", "width=1024"));
        }
        else
        {
            response.render(composeMetaItem("viewport", "width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"));
        }
    }

    private static HeaderItem composeMetaItem(String name, String content)
    {
        return StringHeaderItem.forString(String.format("<meta name=\"%s\" content=\"%s\" />", name, content));
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