package ru.ruranobe.wicket.components;

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
    public UserActionsPanel(String id)
    {
        super(id);
        add(new UserActionsForm("userActionsForm"));
    }

    private class UserActionsForm extends StatelessForm
    {
        public UserActionsForm(String id)
        {
            super(id);

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
                    throw new RedirectToUrlException(urlFor(getPage().getClass(), getPage().getPageParameters()).toString());
                }
            });
        }
    }
}
