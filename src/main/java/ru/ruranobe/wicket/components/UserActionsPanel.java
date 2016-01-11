package ru.ruranobe.wicket.components;

import org.apache.tools.ant.taskdefs.optional.Cab;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.webpages.Cabinet;
import ru.ruranobe.wicket.webpages.Register;

public class UserActionsPanel extends Panel
{
    public UserActionsPanel(String id, boolean androidView)
    {
        super(id);
        add(new UserActionsForm("userActionsForm", androidView));
    }

    private class UserActionsForm extends StatelessForm
    {
        public UserActionsForm(String id, boolean androidView)
        {
            super(id);

            if (androidView)
            {
                add(new AttributeModifier("class", "navbar-form visible-xs-block"));
            }

            add(new Button("signIn") {
                @Override
                public boolean isVisible() {
                    return !LoginSession.get().isSignedIn();
                }
            });

            add(new BookmarkablePageLink("registerPageLink", Register.class) {
                @Override
                public boolean isVisible() {
                    return !LoginSession.get().isSignedIn();
                }
            });

            BookmarkablePageLink cabingPageLink = new BookmarkablePageLink("cabinetPageLink", Cabinet.class)
            {
                @Override
                public boolean isVisible()
                {
                    return LoginSession.get().isSignedIn();
                }
            };
            add(cabingPageLink);

            User user = ((LoginSession) LoginSession.get()).getUser();
            String username = user == null ? "" : user.getUsername();
            cabingPageLink.add(new Label("username", username)
            {
                @Override
                public boolean isVisible()
                {
                    return LoginSession.get().isSignedIn();
                }
            });

            add(new Button("signOut")
            {
                @Override
                public boolean isVisible()
                {
                    return LoginSession.get().isSignedIn();
                }

                @Override
                public void onSubmit()
                {
                    LoginSession.get().invalidate();
                    Class pageClazz = getPage().getClass();
                    /* If a user try to sign out on the cabinet page the reloading will fail, because
                       we can load personal cabinet if only the user is signed in. */
                    if (Cabinet.class.equals(pageClazz))
                    {
                        setResponsePage(getApplication().getHomePage());
                    }
                    else
                    {
                        throw new RedirectToUrlException(urlFor(pageClazz, getPage().getPageParameters()).toString());
                    }
                }
            });
        }
    }
}
