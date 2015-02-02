package ru.ruranobe.wicket.components;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class RuranobeSignInPanel extends Panel
{

    public RuranobeSignInPanel(final String id)
    {
        super(id);

        add(new FeedbackPanel("feedback"));

        // Add sign-in form to page, passing feedback panel as
        // validation error handler
        add(new RuranobeSignInForm(SIGN_IN_FORM));
    }

    protected RuranobeSignInForm getForm()
    {
        return (RuranobeSignInForm) get(SIGN_IN_FORM);
    }

    @Override
    protected void onBeforeRender()
    {
        if (!isSignedIn())
        {
            IAuthenticationStrategy authenticationStrategy = getApplication()
                    .getSecuritySettings().getAuthenticationStrategy();
            
            // get username and password from persistence store
            String[] data = authenticationStrategy.load();

            if ((data != null) && (data.length > 1))
            {
                // try to sign in the user
                if (signIn(data[0], data[1]))
                {
                    username = data[0];
                    password = data[1];

                    // logon successful. Continue to the original destination
                    continueToOriginalDestination();
                    
                    // If we get this far, it means that we should redirect to the home page
                    throw new RestartResponseException(getSession().getPageFactory().newPage(
                                getApplication().getHomePage()));
                }
                else
                {
                    // the loaded credentials are wrong. erase them.
                    authenticationStrategy.remove();
                }
            }
        }
        
        super.onBeforeRender();
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(final String username)
    {
        this.username = username;
    }

    public boolean getRememberMe()
    {
        return rememberMe;
    }

    public void setRememberMe(final boolean rememberMe)
    {
        this.rememberMe = rememberMe;
    }

    private boolean signIn(String username, String password)
    {
        return AuthenticatedWebSession.get().signIn(username, password);
    }

    private boolean isSignedIn()
    {
        return AuthenticatedWebSession.get().isSignedIn();
    }

    protected void onSignInFailed()
    {
        error("Не удалось распознать введенные данные. ");
    }

    protected void onSignInSucceeded()
    {
        // logon successful. Continue to the original destination
        continueToOriginalDestination();

        // If we get this far, it means that we should redirect to the home page
        setResponsePage(getApplication().getHomePage());
    }

    public final class RuranobeSignInForm extends StatelessForm<RuranobeSignInPanel>
    {

        public RuranobeSignInForm(final String id)
        {
            super(id);

            setModel(new CompoundPropertyModel<RuranobeSignInPanel>(RuranobeSignInPanel.this));

            // Attach textfields for username and password
            add(new TextField<String>("username"));
            add(new PasswordTextField("password"));
            add(new CheckBox("rememberMe"));
        }

        @Override
        public final void onSubmit()
        {
            IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                    .getAuthenticationStrategy();

            if (signIn(getUsername(), getPassword()))
            {
                if (rememberMe == true)
                {
                    strategy.save(username, password);
                }
                else
                {
                    strategy.remove();
                }

                onSignInSucceeded();
            }
            else
            {
                onSignInFailed();
                strategy.remove();
            }
        }
        
        private static final long serialVersionUID = 1L;
    }
    
    private static final String SIGN_IN_FORM = "signInForm";
    private boolean rememberMe = true;
    private String password;
    private String username;
    private static final long serialVersionUID = 1L;
}
